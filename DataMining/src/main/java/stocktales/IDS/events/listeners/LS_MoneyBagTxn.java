package stocktales.IDS.events.listeners;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import stocktales.IDS.enums.EnumTxnType;
import stocktales.IDS.events.EV_MoneyBagTxn;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoMoneyBag;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;

@Service
/**
 * 
 * Listener/Handler for Money Bag Txn event Handling
 */
public class LS_MoneyBagTxn implements ApplicationListener<EV_MoneyBagTxn>
{
	@Autowired
	private RepoMoneyBag repoMB;

	@Autowired
	private IDS_MoneyBagSrv mBSrv;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private RepoHC repoHC; // for dividend Scenario

	@Override
	@Transactional
	public void onApplicationEvent(EV_MoneyBagTxn mbEV)
	{
		if (mbEV != null)
		{
			if (mbEV.getMbagTxn() != null)
			{
				/*
				 * Total Deployable AMount not needed - as it is not updated on Purchase of
				 * Scrips
				 */
				// double depAmnt = mBSrv.getDeployableAmount();
				double depAmnt = 0;
				boolean isDividendSenario = false;
				boolean isWithdrawScenario = false;

				if (mbEV.getMbagTxn().getAmount() == 0)
				{
					depAmnt = mBSrv.getDeployableAmount();
				}

				if (mbEV.getMbagTxn().getAmount() > 0)
				{
					switch (mbEV.getMbagTxn().getType())
					{
					case Deposit:
						depAmnt += mbEV.getMbagTxn().getAmount();
						break;

					case PandL:
						depAmnt += mbEV.getMbagTxn().getAmount();
						break;

					case Withdraw:
						depAmnt = mbEV.getMbagTxn().getAmount();
						isWithdrawScenario = true;
						break;

					case Dividend:
						depAmnt += mbEV.getMbagTxn().getAmount();
						isDividendSenario = true;
						break;

					case Clear:
						depAmnt -= mbEV.getMbagTxn().getAmount();
						break;

					default:
						break;
					}

					if (depAmnt > 0) // Withdraw Can't take below zero
					{
						// Persist Money Bag Txn
						repoMB.save(mbEV.getMbagTxn());

						// Update dep Amnts for PfScehmas

						List<PFSchema> pfScehmas = repoPFSchema.findAll();
						for (PFSchema pfSchema : pfScehmas)
						{
							double amnt = 0;
							if (!isWithdrawScenario)
							{
								amnt = Precision
										.round((pfSchema.getDepamnt() + (pfSchema.getIncalloc() * depAmnt * .01)), 0);
							} else
							{
								amnt = Precision
										.round((pfSchema.getDepamnt() - (pfSchema.getIncalloc() * depAmnt * .01)), 0);

							}
							repoPFSchema.updateDeployableAmountforScrip(pfSchema.getSccode(), amnt);
						}

						if (isDividendSenario)
						{
							Optional<HC> hcO = repoHC.findById(mbEV.getMbagTxn().getRemarks());
							if (hcO.isPresent())
							{
								// Scrip Code Maintained in Remarks
								// Get existing dividend if any
								double exisDiv = hcO.get().getDividend();
								exisDiv += depAmnt;

								repoHC.updateDividendforScrip(mbEV.getMbagTxn().getRemarks(), exisDiv);
							}
						}
					}

					else
					{
						// Remove Until 0
						mbEV.getMbagTxn().setAmount(mBSrv.getDeployableAmount());
						mbEV.getMbagTxn().setType(EnumTxnType.Clear);

						// Persist Money Bag Txn
						repoMB.save(mbEV.getMbagTxn());

						// Update dep Amnts for PfSchemas
						repoPFSchema.clearDeployableAmounts();

					}
				}
			}
		}
	}

}
