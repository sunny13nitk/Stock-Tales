package stocktales.IDS.events.listeners;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import stocktales.IDS.enums.EnumSchemaDepAmntsUpdateMode;
import stocktales.IDS.enums.EnumTxnType;
import stocktales.IDS.events.EV_PFTxn;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.MoneyBag;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoHCI;
import stocktales.IDS.model.pf.repo.RepoMoneyBag;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SCAlloc;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.srv.intf.IDS_CorePFSrv;
import stocktales.IDS.srv.intf.IDS_PFTxn_Validator;
import stocktales.durations.UtilDurations;

@Service
/*
 * CORE PF Transaction Event Listener
 */
public class LS_CorePFTxn implements ApplicationListener<EV_PFTxn>
{
	@Autowired
	private IDS_PFTxn_Validator txnValidSrv;

	@Autowired
	private RepoHC repoHC;

	@Autowired
	private RepoHCI repoHCI;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private RepoMoneyBag repoMB;

	@Autowired
	private IDS_CorePFSrv corePFSrv;

	@Override
	@Transactional
	public void onApplicationEvent(EV_PFTxn evPFTxn)
	{
		if (evPFTxn != null)
		{
			if (evPFTxn.getPfTxn() != null)
			{
				// 1. Validate the txn as a first Step

				try
				{
					if (txnValidSrv.isTxnValid(evPFTxn.getPfTxn()))
					{
						switch (evPFTxn.getPfTxn().getTxntype())
						{
						case Buy:
							repoHCI.save(evPFTxn.getPfTxn()); // Insert in HCI

							Optional<HC> hcO = repoHC.findById(evPFTxn.getPfTxn().getSccode());
							if (hcO.isPresent())
							{
								HC exisHolding = hcO.get();
								double totalInv = (exisHolding.getPpu() * exisHolding.getUnits())
										+ (evPFTxn.getPfTxn().getTxnppu() * evPFTxn.getPfTxn().getUnits());

								int totalUnits = exisHolding.getUnits() + evPFTxn.getPfTxn().getUnits();

								double ppu = Precision.round((totalInv / totalUnits), 2);

								// Update in HC
								repoHC.updatePPUUnitsforScrip(exisHolding.getSccode(), totalUnits, ppu);

							} else
							{
								// Insert in HC
								HC newHolding = new HC(evPFTxn.getPfTxn().getSccode(), evPFTxn.getPfTxn().getTxnppu(),
										evPFTxn.getPfTxn().getUnits(), 0);
								repoHC.save(newHolding);
							}

							/*
							 * Update Deployable Amount for Scrip in PFSchema
							 */
							double depAmtSc = repoPFSchema.getDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode());
							depAmtSc = depAmtSc - (evPFTxn.getPfTxn().getTxnppu() * evPFTxn.getPfTxn().getUnits());
							repoPFSchema.updateDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode(), depAmtSc);

							break;

						case Sell:
							double remUnits = 0;
							hcO = repoHC.findById(evPFTxn.getPfTxn().getSccode());
							if (hcO.isPresent())
							{
								remUnits = hcO.get().getUnits() - evPFTxn.getPfTxn().getUnits();
							}

							if (remUnits > 0)
							{
								repoHCI.save(evPFTxn.getPfTxn()); // Insert in HCI

								hcO = repoHC.findById(evPFTxn.getPfTxn().getSccode());
								if (hcO.isPresent())
								{
									HC exisHolding = hcO.get();

									int totalUnits = exisHolding.getUnits() - evPFTxn.getPfTxn().getUnits();

									// Update in HC
									repoHC.updatePPUUnitsforScrip(exisHolding.getSccode(), totalUnits,
											exisHolding.getPpu());

								}

								/*
								 * Update Deployable Amount for Scrip in PFSchema
								 */
								depAmtSc = repoPFSchema.getDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode());
								depAmtSc = depAmtSc + (evPFTxn.getPfTxn().getTxnppu() * evPFTxn.getPfTxn().getUnits());
								repoPFSchema.updateDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode(), depAmtSc);

							} else
							{
								/*
								 * Delete Completely from COre Holdings and ITems
								 */
								repoHC.deleteById(evPFTxn.getPfTxn().getSccode());
								repoHCI.removeScrip(evPFTxn.getPfTxn().getSccode());

								/*
								 * Update Deployable Amount for Scrip in PFSchema
								 */
								depAmtSc = repoPFSchema.getDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode());
								depAmtSc = depAmtSc + (evPFTxn.getPfTxn().getTxnppu() * evPFTxn.getPfTxn().getUnits());
								repoPFSchema.updateDeployableAmountforScrip(evPFTxn.getPfTxn().getSccode(), depAmtSc);

							}

							/**
							 * PF Schema allocations & dep Amounts needs to be holistically adjusted for
							 * Sale Proceeds both in case of Profit or loss
							 */
							IDS_ScAllocMassUpdate allocMassUpdate = new IDS_ScAllocMassUpdate();
							for (PFSchema sch : repoPFSchema.findAll())
							{
								allocMassUpdate.getScAllocList().add(new IDS_SCAlloc(sch.getSccode(), sch.getSector(),
										sch.getIdealalloc(), sch.getIncalloc()));

							}
							allocMassUpdate.setDepAmtMode(EnumSchemaDepAmntsUpdateMode.Holistic);
							corePFSrv.processAllocationChanges(allocMassUpdate);

							break;

						case BonusSell:

							repoHCI.save(evPFTxn.getPfTxn()); // Insert in HCI
							/*
							 * Treat as Fresh Deposit- Money Bag Preparation
							 */
							MoneyBag mbPLB = new MoneyBag();
							mbPLB.setDate(UtilDurations.getTodaysDate());
							mbPLB.setRemarks("Bonus Issue Adjustment :  " + evPFTxn.getPfTxn().getUnits() + " units of "
									+ evPFTxn.getPfTxn().getSccode() + " @ Rs. " + evPFTxn.getPfTxn().getTxnppu()
									+ " per Unit");
							mbPLB.setType(EnumTxnType.Deposit);
							mbPLB.setAmount(
									Precision.round(evPFTxn.getPfTxn().getUnits() * evPFTxn.getPfTxn().getTxnppu(), 0));
							/*
							 * Process P&L Money Bag Txn Using Money Bag Service
							 */
							repoMB.save(mbPLB);

							break;

						case Exit:
							/*
							 * Get Exited Scrip Deployable Amount and Distribute as per Adjusted Ideal/Inc
							 * Allocations
							 */

							if (evPFTxn.getPfTxn().getUnits() > 0) // Scrip in PF Too
							{
								double ppuPL = 0;
								double addDEpAmnt = 0;
								Optional<HC> HCExitO = repoHC.findById(evPFTxn.getPfTxn().getSccode());
								if (HCExitO.isPresent())
								{
									// PPUPL = Sell PPU - Exit Holding acquisition PPU
									ppuPL = evPFTxn.getPfTxn().getTxnppu() - HCExitO.get().getPpu();
									addDEpAmnt = ppuPL * evPFTxn.getPfTxn().getUnits();
								}

								/*
								 * update the Schema
								 */
								updatePFSchemaforExitScrip(evPFTxn.getPfTxn().getSccode(), addDEpAmnt);

								/*
								 * After P&L Money Bag Preparation
								 */
								MoneyBag mbPL = new MoneyBag();
								mbPL.setDate(UtilDurations.getTodaysDate());
								mbPL.setRemarks("Exit of " + evPFTxn.getPfTxn().getUnits() + " units of "
										+ evPFTxn.getPfTxn().getSccode() + " @ Rs. " + evPFTxn.getPfTxn().getTxnppu()
										+ " per Unit");
								mbPL.setType(EnumTxnType.PandL);
								mbPL.setAmount(Precision.round(ppuPL * evPFTxn.getPfTxn().getUnits(), 0));

								/*
								 * Delete Completely from COre Holdings and ITems
								 */
								repoHC.deleteById(evPFTxn.getPfTxn().getSccode());
								repoHCI.removeScrip(evPFTxn.getPfTxn().getSccode());

								/*
								 * Process P&L Money Bag Txn Using Money Bag Service
								 */
								repoMB.save(mbPL);

							} else // Scrip Only in Schema and not in PF
							{

								/*
								 * Update the Schema
								 */
								updatePFSchemaforExitScrip(evPFTxn.getPfTxn().getSccode(), 0);
							}

						}
					}
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Update PFSchema for Exit of Scrip
	 * 
	 * @param sccode - SCrip to be Exited
	 */
	private void updatePFSchemaforExitScrip(String sccode, double addDepAmntPL)
	{
		if (sccode != null)
		{

			PFSchema tobeExited = repoPFSchema.findById(sccode).get();
			if (tobeExited != null)
			{
				repoPFSchema.deleteById(sccode); // REmove Scrip to Be Exited from REPO - PF SCHEMA
			}

			List<PFSchema> schemaList = repoPFSchema.findAll(); // Only Ones after exclusion remains
			if (schemaList.size() > 0)
			{

				for (PFSchema pfSchema : schemaList)
				{
					PFSchema updSchema = new PFSchema();
					updSchema.setIdealalloc(
							Precision.round(pfSchema.getIdealalloc() / (100 - tobeExited.getIdealalloc()) * 100, 2));

					updSchema.setIncalloc(
							Precision.round(pfSchema.getIncalloc() / (100 - tobeExited.getIncalloc()) * 100, 2));

					if (tobeExited.getDepamnt() > 0)
					{
						updSchema.setDepamnt(Precision.round(
								pfSchema.getDepamnt()
										+ (((tobeExited.getDepamnt() + addDepAmntPL) * updSchema.getIncalloc()) * .01),
								2));
					} else
					{
						updSchema.setDepamnt(pfSchema.getDepamnt());
					}

					repoPFSchema.updateSchemEntityforScrip(pfSchema.getSccode(), updSchema.getIdealalloc(),
							updSchema.getIncalloc(), updSchema.getDepamnt());

				}
			}

		}

	}

}
