package stocktales.IDS.srv.impl;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.DataLake.srv.intf.DL_HistoricalPricesSrv;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.model.pf.entity.MoneyBag;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoHCI;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SC_BonusIP;
import stocktales.IDS.pojo.IDS_SC_SplitIP;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.UI.IDSOverAllocList;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.IDS_PFTxn_UI;
import stocktales.IDS.pojo.UI.IDS_PF_OverAllocations;
import stocktales.IDS.pojo.UI.IDS_PF_OverAllocsContainer;
import stocktales.IDS.pojo.UI.PFDBContainer;
import stocktales.IDS.pojo.UI.PFHoldingsPL;
import stocktales.IDS.pojo.UI.PFStatsH;
import stocktales.IDS.pojo.UI.ScripPLSS;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;
import stocktales.IDS.srv.intf.IDS_PFSchema_REbalUI_Srv;
import stocktales.IDS.srv.intf.IDS_PFTxn_Validator;
import stocktales.IDS.utility.SMASortUtility;
import stocktales.NFS.enums.EnumMCapClassification;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.durations.UtilDurations;
import stocktales.exceptions.PFTxnInvalidException;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.usersPF.enums.EnumTxnType;
import yahoofinance.Stock;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service()
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class IDS_PFDashBoardUISrv implements stocktales.IDS.srv.intf.IDS_PFDashBoardUISrv
{

	/**
	 * ----------------------- AUTOWIRED SECTION STARTS --------
	 */

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	@Qualifier("DL_HistoricalPricesSrv_IDS")
	private DL_HistoricalPricesSrv hpDBSrv;

	@Autowired
	private IDS_PFTxn_Validator txnValidSrv;

	@Autowired
	private RepoHC repoHC;

	@Autowired
	private RepoHCI repoHCI;

	@Autowired
	private IDS_CorePFSrv corePFSrv;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private IDS_PFSchema_REbalUI_Srv schRebalSrv;

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private IDS_MoneyBagSrv mbSrv;

	@Autowired
	private EntityManager entityManager;

	/**
	 * ----------------------- AUTOWIRED SECTION ENDS ----------
	 */

	private PFDBContainer pfDBCtr;

	/**
	 * ----------------------- IMPLEMENTATION SECTION STARTS --------
	 */

	@Override
	public PFDBContainer getPFDashBoardContainer() throws Exception
	{
		if (pfDBCtr == null)
		{
			pfDBCtr = new PFDBContainer();
			initialize();
		} else
		{
			// Always REfresh the Basic Portion
			this.refreshContainer4RoundTrip();
		}

		return this.pfDBCtr;
	}

	@Override
	public PFDBContainer getPFDashBoardContainer4mSession() throws Exception
	{

		return this.pfDBCtr;
	}

	@Override
	public void refreshContainer4SchemaChange() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			if (repoPFSchema.count() > 0)
			{
				this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
				if (this.schRebalSrv != null) // Only if Null
				{
					if (this.schRebalSrv.getRebalContainer() == null)
					{
						pfDBCtr.setSchemaStats(this.schRebalSrv.uploadSchemaforUpdate().getStats());
					}
				}

				/*
				 * LOAD Buy Proposals
				 */
				IDS_BuyProposalBO buyP = corePFSrv.getBuyProposals();
				if (buyP != null)
				{
					this.pfDBCtr.setBuyProposals(buyP);
				}

				/*
				 * Load SMA Preview
				 */
				this.pfDBCtr.setSmaPvwL(corePFSrv.getPFSchemaSMAPreview());

				/*
				 * Build Holdings P&L Table
				 */
				buildHoldingsPLTable();

			}
		}
	}

	@Override
	public void refreshContainer4RoundTrip() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			// Set Holdings P&L List
			this.pfDBCtr.getHoldings().clear();
			buildHoldingsPLTable();

			// Build Stats Header
			this.pfDBCtr.setStatsH(new PFStatsH());
			buildStatsHeader();

			// Buy Proposals
			IDS_BuyProposalBO buyPBO = corePFSrv.getBuyProposals();
			this.pfDBCtr.setBuyProposals(buyPBO);

			// SMA PVW No need to change on Each REfresh - Anyways computed for Buy
			// Proposals each time

			// Schema Details No need to change on each refresh

			// Update XIRR for PF
			this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());

		}

	}

	@Override
	public void refreshContainer4RoundTrip(IDS_BuyProposalBO buyPBO) throws Exception
	{
		if (this.pfDBCtr != null)
		{
			// Set Holdings P&L List
			this.pfDBCtr.getHoldings().clear();
			buildHoldingsPLTable();

			// Build Stats Header
			this.pfDBCtr.setStatsH(new PFStatsH());
			buildStatsHeader();

			// Buy Proposals
			this.pfDBCtr.setBuyProposals(buyPBO);

			// SMA PVW No need to change on Each REfresh - Anyways computed for Buy
			// Proposals each time

			// Schema Details No need to change on each refresh

			// Update XIRR for PF
			this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());
		}

	}

	@Override
	public void refreshContainer4Txn() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			refreshContainer4RoundTrip();
			this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
		}

	}

	@Override
	public boolean areOverAllocationsPresent()
	{
		boolean isPresent = false;

		if (pfDBCtr.getHoldings() != null)
		{
			if (pfDBCtr.getHoldings().size() > 0)
			{
				if (pfDBCtr.getHoldings().stream().filter(w -> w.getDepAmnt() < 0).findAny().isPresent())
				{
					isPresent = true;
				}
			}
		}

		return isPresent;
	}

	@Override
	public IDS_PF_OverAllocsContainer fetchOverAllocations()
	{
		/*
		 * Clear Over Allocations
		 */
		this.pfDBCtr.getOverAllocsContainer().getOverAllocs().clear();
		this.pfDBCtr.getOverAllocsContainer().setPlSum(0);
		this.pfDBCtr.getOverAllocsContainer().setPlSumStr("");
		this.pfDBCtr.getOverAllocsContainer().setTxnSum(0);
		this.pfDBCtr.getOverAllocsContainer().setTxnSumStr("");

		if (this.areOverAllocationsPresent())
		{
			List<PFHoldingsPL> overallocHoldings = this.pfDBCtr.getHoldings().stream().filter(x -> x.getDepAmnt() < 0)
					.collect(Collectors.toList());
			if (overallocHoldings != null)
			{
				if (overallocHoldings.size() > 0)
				{
					double txnAmount = 0;
					for (PFHoldingsPL overlAllocHolding : overallocHoldings)
					{
						IDS_PF_OverAllocations ovAlloc = new IDS_PF_OverAllocations();
						ovAlloc.setScCode(overlAllocHolding.getScCode());
						ovAlloc.setCmp(overlAllocHolding.getCmp());
						ovAlloc.setDepAmnt(overlAllocHolding.getDepAmnt());
						ovAlloc.setDepAmntStr(overlAllocHolding.getDepAmntStr());
						ovAlloc.setDepPer(overlAllocHolding.getDepPer());
						ovAlloc.setUnitsSell((int) (overlAllocHolding.getDepAmnt() * -1 / ovAlloc.getCmp()));
						ovAlloc.setPl(Precision.round(
								((overlAllocHolding.getCmp() - overlAllocHolding.getPpu()) * ovAlloc.getUnitsSell()),
								1));
						ovAlloc.setSelect(true);

						if (ovAlloc.getUnitsSell() > 0)
						{
							txnAmount += (ovAlloc.getUnitsSell() * ovAlloc.getCmp());
							this.pfDBCtr.getOverAllocsContainer().getOverAllocs().add(ovAlloc);
						}

					}

					this.pfDBCtr.getOverAllocsContainer().setPlSum(this.pfDBCtr.getOverAllocsContainer().getOverAllocs()
							.stream().mapToDouble(IDS_PF_OverAllocations::getPl).sum());
					this.pfDBCtr.getOverAllocsContainer().setPlSumStr(UtilDecimaltoMoneyString
							.getMoneyStringforDecimal(this.pfDBCtr.getOverAllocsContainer().getPlSum(), 2));
					this.pfDBCtr.getOverAllocsContainer().setTxnSum(Precision.round(txnAmount, 2));
					this.pfDBCtr.getOverAllocsContainer()
							.setTxnSumStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(txnAmount, 2));
				}
			}

		}

		return this.pfDBCtr.getOverAllocsContainer();

	}

	@Override
	public IDS_PF_OverAllocsContainer refreshOverAllocationsPL(IDSOverAllocList viewList)
	{
		this.pfDBCtr.getOverAllocsContainer().getOverAllocs().clear();
		this.pfDBCtr.getOverAllocsContainer().setPlSum(0);
		this.pfDBCtr.getOverAllocsContainer().setPlSumStr("");
		this.pfDBCtr.getOverAllocsContainer().setTxnSum(0);
		this.pfDBCtr.getOverAllocsContainer().setTxnSumStr("");

		if (this.areOverAllocationsPresent() && viewList != null)
		{
			if (viewList.getOverAllocList().size() > 0)
			{
				List<PFHoldingsPL> overallocHoldings = this.pfDBCtr.getHoldings().stream()
						.filter(x -> x.getDepAmnt() < 0).collect(Collectors.toList());
				if (overallocHoldings != null)
				{
					if (overallocHoldings.size() > 0)
					{

						double txnAmount = 0;
						for (PFHoldingsPL overlAllocHolding : overallocHoldings)
						{
							IDS_PF_OverAllocations ovAlloc = new IDS_PF_OverAllocations();
							ovAlloc.setScCode(overlAllocHolding.getScCode());
							ovAlloc.setCmp(overlAllocHolding.getCmp());
							ovAlloc.setDepAmnt(overlAllocHolding.getDepAmnt());
							ovAlloc.setDepAmntStr(overlAllocHolding.getDepAmntStr());
							ovAlloc.setDepPer(overlAllocHolding.getDepPer());

							try
							{
								IDS_PF_OverAllocations vwHolding = viewList.getOverAllocList().stream()
										.filter(x -> x.getScCode().equals(overlAllocHolding.getScCode())).findFirst()
										.get();
								if (vwHolding != null)
								{
									if (vwHolding.getUnitsSell() <= overlAllocHolding.getUnits())
									{
										ovAlloc.setUnitsSell(vwHolding.getUnitsSell());

										ovAlloc.setPl(Precision
												.round(((overlAllocHolding.getCmp() - overlAllocHolding.getPpu())
														* ovAlloc.getUnitsSell()), 1));
									} else
									{
										ovAlloc.setUnitsSell(
												(int) (overlAllocHolding.getDepAmnt() * -1 / ovAlloc.getCmp()));
										ovAlloc.setPl(Precision
												.round(((overlAllocHolding.getCmp() - overlAllocHolding.getPpu())
														* ovAlloc.getUnitsSell()), 1));
									}
									ovAlloc.setSelect(vwHolding.isSelect());
								}

								if (vwHolding.isSelect())
								{
									txnAmount += (ovAlloc.getUnitsSell() * ovAlloc.getCmp());
								}

								this.pfDBCtr.getOverAllocsContainer().getOverAllocs().add(ovAlloc);
							} catch (NoSuchElementException e)
							{
								// do Nothing
							}

						}

						this.pfDBCtr.getOverAllocsContainer()
								.setPlSum(this.pfDBCtr.getOverAllocsContainer().getOverAllocs().stream()
										.filter(w -> w.isSelect() == true).collect(Collectors.toList()).stream()
										.mapToDouble(IDS_PF_OverAllocations::getPl).sum());

						this.pfDBCtr.getOverAllocsContainer().setPlSumStr(UtilDecimaltoMoneyString
								.getMoneyStringforDecimal(this.pfDBCtr.getOverAllocsContainer().getPlSum(), 2));

						this.pfDBCtr.getOverAllocsContainer().setTxnSum(Precision.round(txnAmount, 2));
						this.pfDBCtr.getOverAllocsContainer()
								.setTxnSumStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(txnAmount, 2));
					}
				}
			}

		}

		return this.pfDBCtr.getOverAllocsContainer();
	}

	@Override
	@Transactional
	public void commitOverAllocationsSells(IDSOverAllocList viewList) throws Exception
	{
		for (IDS_PF_OverAllocations overAlloc : viewList.getOverAllocList())
		{
			if (overAlloc.isSelect() && overAlloc.getUnitsSell() > 0)
			{
				// Commit the SAle
				HCI sellTxn = new HCI();
				sellTxn.setDate(UtilDurations.getTodaysDateOnly());
				sellTxn.setSccode(overAlloc.getScCode());
				sellTxn.setTxntype(EnumTxnType.Sell);
				sellTxn.setSmarank(0);
				sellTxn.setTxnppu(overAlloc.getCmp());
				sellTxn.setUnits(overAlloc.getUnitsSell());

				/*
				 * Implicit Holistic Adjustment of Sales would happen
				 */
				corePFSrv.processCorePFTxn(sellTxn);

			}
		}

	}

	@Override
	public void refreshSchemaPostTxn() throws Exception
	{
		if (entityManager != null)
		{
			entityManager.clear();
			if (repoPFSchema != null && repoPFSchema.count() > 0)
			{
				this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
			}
		}

	}

	@Override
	@Transactional
	public void processUIPFTxn(IDS_PFTxn_UI pfTxnUI) throws Exception
	{
		if (pfTxnUI != null)
		{
			boolean rankFoundinSess = false;
			if (StringUtils.hasText(pfTxnUI.getScCode()))
			{
				switch (pfTxnUI.getTxnType())
				{
				case Buy:
					if (pfTxnUI.getNumSharesTxn() > 0 && pfTxnUI.getPpuTxn() > 0)
					{
						HCI pfTxn = new HCI();
						pfTxn.setSccode(pfTxnUI.getScCode());
						/*
						 * Get Current SMA of the Scrip as per CMP
						 */
						if (this.getPFDashBoardContainer4mSession() != null)
						{
							if (this.getPFDashBoardContainer4mSession().getHoldings() != null)
							{
								if (this.getPFDashBoardContainer4mSession().getHoldings().size() > 0)
								{
									Optional<PFHoldingsPL> holdingSessO = this.getPFDashBoardContainer4mSession()
											.getHoldings().stream()
											.filter(f -> f.getScCode().equals(pfTxnUI.getScCode())).findFirst();
									if (holdingSessO.isPresent())
									{
										if (holdingSessO.get().getSmaLvl() != null)
										{
											pfTxn.setSmarank(holdingSessO.get().getSmaLvl().ordinal());
										} else
										{
											pfTxn.setSmarank(EnumSMABreach.sma1.ordinal()); // Default
										}
										rankFoundinSess = true;
									}

								}
							}
						}
						if (!rankFoundinSess)
						{
							pfTxn.setSmarank(0);
						}

						pfTxn.setTxnppu(pfTxnUI.getPpuTxn());
						pfTxn.setUnits(pfTxnUI.getNumSharesTxn());
						pfTxn.setTxntype(EnumTxnType.Buy);
						pfTxn.setDate(UtilDurations.getTodaysDateOnly());

						/**
						 * Validator Explicitly called as in LS_PFTxn it is not possible to throw and
						 * thus capture the exception
						 */

						if (txnValidSrv.isTxnValid(pfTxn))
						{

							/**
							 * Process the Transaction - Core PF Service
							 */
							corePFSrv.processCorePFTxn(pfTxn);

							/*
							 * REfresh the Schema load
							 */
							this.refreshSchemaPostTxn();
						}

					} else
					{
						throw new PFTxnInvalidException("Quantity & Buy Price/Unit should be > 0 for Purchase Txn.");

					}

					break;

				case Sell:
					if (pfTxnUI.getNumSharesTxn() > 0 && pfTxnUI.getPpuTxn() > 0)
					{
						HCI pfTxn = new HCI();
						pfTxn.setSccode(pfTxnUI.getScCode());
						/*
						 * Get Current SMA of the Scrip as per CMP
						 */
						if (this.getPFDashBoardContainer4mSession() != null)
						{
							if (this.getPFDashBoardContainer4mSession().getHoldings() != null)
							{
								if (this.getPFDashBoardContainer4mSession().getHoldings().size() > 0)
								{
									Optional<PFHoldingsPL> holdingSessO = this.getPFDashBoardContainer4mSession()
											.getHoldings().stream()
											.filter(f -> f.getScCode().equals(pfTxnUI.getScCode())).findFirst();
									if (holdingSessO.isPresent())
									{
										pfTxn.setSmarank(holdingSessO.get().getSmaLvl().ordinal());
										rankFoundinSess = true;
									}

								}
							}
						}
						if (!rankFoundinSess)
						{
							pfTxn.setSmarank(0);
						}

						pfTxn.setTxnppu(pfTxnUI.getPpuTxn());
						pfTxn.setUnits(pfTxnUI.getNumSharesTxn());
						pfTxn.setTxntype(EnumTxnType.Sell);
						pfTxn.setDate(UtilDurations.getTodaysDateOnly());

						/**
						 * Validator Explicitly called as in LS_PFTxn it is not possible to throw and
						 * thus capture the exception
						 */
						if (txnValidSrv.isTxnValid(pfTxn))
						{

							/**
							 * Process the Transaction - Core PF Service
							 */
							corePFSrv.processCorePFTxn(pfTxn);

							/*
							 * REfresh the Schema load
							 */
							this.refreshSchemaPostTxn();
						}

					} else
					{
						throw new PFTxnInvalidException("Quantity & Buy Price/Unit should be > 0 for Sell Txn.");

					}
					break;

				case Dividend:
					if (pfTxnUI.getNumSharesTxn() > 0 && pfTxnUI.getDivPS() > 0)
					{
						if (repoHC != null)
						{
							Optional<HC> holdingO = repoHC.findById(pfTxnUI.getScCode());
							if (holdingO.isPresent())
							{
								HC holding = holdingO.get();
								if (holding.getUnits() < pfTxnUI.getNumSharesTxn())
								{
									// Trigger Custom Exception
									throw new PFTxnInvalidException(msgSrc.getMessage("pfTxn.divQty", new Object[]
									{ pfTxnUI.getNumSharesTxn(), holding.getUnits() }, Locale.ENGLISH));
								} else
								{
									double CMP = StockPricesUtility.getQuoteforScrip(pfTxnUI.getScCode()).getQuote()
											.getPrice().doubleValue();

									if ((CMP * .2) <= pfTxnUI.getDivPS())
									{
										// Trigger Custom Exception
										throw new PFTxnInvalidException(msgSrc.getMessage("pfTxn.divAmnt", new Object[]
										{ pfTxnUI.getDivPS(), (CMP * .2) }, Locale.ENGLISH));
									} else
									{
										MoneyBag mbTxn = new MoneyBag();
										mbTxn.setType(stocktales.IDS.enums.EnumTxnType.Dividend);
										mbTxn.setDate(UtilDurations.getTodaysDateOnly());
										mbTxn.setRemarks("Dividend:" + pfTxnUI.getScCode());
										mbTxn.setAmount(
												Precision.round(pfTxnUI.getDivPS() * pfTxnUI.getNumSharesTxn(), 1));
										mbSrv.processMBagTxn(mbTxn);

										/*
										 * REfresh the Schema load
										 */
										this.refreshSchemaPostTxn();
									}

								}
							}
						}

					} else
					{

						throw new PFTxnInvalidException(
								"Quantity & Dividend per share should be > 0 for Dividend Txn.");

					}
					break;

				case Split:
					if (pfTxnUI.getOneToSplitIntoSharesNum() > 1)
					{
						IDS_SC_SplitIP splitIP = new IDS_SC_SplitIP(pfTxnUI.getScCode(),
								pfTxnUI.getOneToSplitIntoSharesNum());
						corePFSrv.adjustPF4StockSplit(splitIP);
					}

					break;

				case Bonus:
					if (pfTxnUI.getForeveryNShares() > 1 && pfTxnUI.getToGetSharesNum() > 1)
					{
						IDS_SC_BonusIP bonusIP = new IDS_SC_BonusIP(pfTxnUI.getScCode(), pfTxnUI.getForeveryNShares(),
								pfTxnUI.getToGetSharesNum());
						corePFSrv.adjustPF4StockBonus(bonusIP);
					}

				default:
					break;
				}
			}
		}

	}

	/**
	 * -------------------------------------------------------------------
	 * ----------- PRIVATE METHODS ---------------------------------------
	 * -------------------------------------------------------------------
	 */
	private void initialize() throws Exception
	{

		if (this.corePFSrv != null)
		{

			/*
			 * LOAD PFSchema
			 */

			if (repoPFSchema.count() > 0)
			{
				this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
				if (this.schRebalSrv != null) // Only if Null
				{
					if (this.schRebalSrv.getRebalContainer() == null)
					{
						pfDBCtr.setSchemaStats(this.schRebalSrv.uploadSchemaforUpdate().getStats());
					}
				}

				/*
				 * LOAD Buy Proposals
				 */
				IDS_BuyProposalBO buyP = corePFSrv.getBuyProposals();
				if (buyP != null)
				{
					this.pfDBCtr.setBuyProposals(buyP);
				}

				/*
				 * Load SMA Preview
				 */
				this.pfDBCtr.setSmaPvwL(corePFSrv.getPFSchemaSMAPreview());

				/*
				 * Build Holdings P&L Table
				 */
				buildHoldingsPLTable();

				/*
				 * Build Stats Header
				 */
				buildStatsHeader();

				// Update XIRR for PF
				this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());
			}

		}
	}

	private void buildHoldingsPLTable() throws Exception
	{
		if (repoHC != null)
		{
			List<HC> pfHoldings = repoHC.findAll();
			if (pfHoldings.size() > 0)
			{

				for (HC hc : pfHoldings)
				{

					PFHoldingsPL pfPL_H = new PFHoldingsPL();
					pfPL_H.setScCode(hc.getSccode());
					Optional<PFSchema> pfSChO = this.pfDBCtr.getSchemaDetails().stream()
							.filter(x -> x.getSccode().equals(hc.getSccode())).findFirst();
					if (pfSChO.isPresent())
					{
						pfPL_H.setSector(pfSChO.get().getSector());
						pfPL_H.setDepAmnt(Precision.round(pfSChO.get().getDepamnt(), 0));
						pfPL_H.setDepAmntStr(
								UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfSChO.get().getDepamnt(), 2));
						double inv = hc.getUnits() * hc.getPpu();
						pfPL_H.setInvestments(Precision.round(inv, 1));
						pfPL_H.setInvString(UtilDecimaltoMoneyString.getMoneyStringforDecimal(inv, 2));
						double total = inv + pfSChO.get().getDepamnt();
						pfPL_H.setDepPer(Precision.round((pfSChO.get().getDepamnt() * 100 / total), 1));
					}

					pfPL_H.setUnits(hc.getUnits());
					pfPL_H.setPpu(hc.getPpu());

					Stock quote = StockPricesUtility.getQuoteforScrip(hc.getSccode());
					if (quote != null)
					{
						pfPL_H.setCmp(Precision.round(quote.getQuote().getPrice().doubleValue(), 1));
						pfPL_H.setCurrVal(Precision.round(pfPL_H.getCmp() * pfPL_H.getUnits(), 0));
						pfPL_H.setCurrValString(
								UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfPL_H.getCurrVal(), 2));
						if (quote.getStats().getMarketCap() != null)
						{
							double Mcap = Precision.round((quote.getStats().getMarketCap().doubleValue() / 10000000),
									0);
							pfPL_H.setMCap(UtilDecimaltoMoneyString.getMoneyStringforDecimal(Mcap, 1));

							EnumMCapClassification mcapName = nfsConfig.getMcapClassificationForMCapKCr(Mcap);
							pfPL_H.setMCapClass(mcapName);

						}

						double pl = hc.getUnits() * (pfPL_H.getCmp() - hc.getPpu());
						double plPer = (pl / (hc.getUnits() * hc.getPpu())) * 100;

						pfPL_H.setPl(Precision.round(pl, 2));
						pfPL_H.setPlPer(Precision.round(plPer, 1));

						pfPL_H.setPlStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfPL_H.getPl(), 2));

						pfPL_H.setDayPLPer(
								UtilPercentages.getPercentageDelta(quote.getQuote().getPreviousClose().doubleValue(),
										quote.getQuote().getPrice().doubleValue(), 1));

						pfPL_H.setDayPL(
								Precision.round((pfPL_H.getDayPLPer() * (hc.getUnits() * hc.getPpu()) / 100), 0));

						/**
						 * SMA Classification v/s CMP
						 */

						Optional<IDS_SMAPreview> smaO = this.pfDBCtr.getSmaPvwL().stream()
								.filter(w -> w.getScCode().equals(hc.getSccode())).findFirst();
						if (smaO.isPresent())
						{
							IDS_SMAPreview smaEnt1 = smaO.get();
							IDS_SMAPreview smaEnt = SMASortUtility.getSMASortedforIDS(smaEnt1);
							if (smaEnt != null)
							{
								if (pfPL_H.getCmp() < smaEnt.getSMAI1())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma1);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI2())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma2);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI3())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma3);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI4())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma4);
								}
							}

						}

					}

					try
					{

						pfPL_H.setLastBuyDate(repoHCI.getlastBuyTxnDateforScrip(hc.getSccode()));

						pfPL_H.setNumDaysBuy(UtilDurations.getNumDaysbwSysDates(pfPL_H.getLastBuyDate(),
								UtilDurations.getTodaysDate()));

					} catch (Exception e)
					{
						// TODO: handle exception
					}

					try
					{
						Date sellDate = repoHCI.getlastSellTxnDateforScrip(hc.getSccode());
						pfPL_H.setLastSellDate(sellDate);

					} catch (Exception e)
					{
						// TODO: handle exception
					}

					this.pfDBCtr.getHoldings().add(pfPL_H);

				}
			}
		}

	}

	private void buildStatsHeader()
	{

		if (repoHC.count() > 0)
		{
			pfDBCtr.getStatsH().setTotalInv(repoHC.getTotalInvestments());
			pfDBCtr.getStatsH().setTotalInvStr(
					UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfDBCtr.getStatsH().getTotalInv(), 2));

			double totalDepAmnt = repoPFSchema.getSumDeploymentAmount();

			double utlzPer = 100 - ((totalDepAmnt / (pfDBCtr.getStatsH().getTotalInv() + totalDepAmnt)) * 100);
			pfDBCtr.getStatsH().setAmntUtilPer(Precision.round(utlzPer, 1));

			double pl = pfDBCtr.getHoldings().stream().mapToDouble(PFHoldingsPL::getPl).sum();
			pfDBCtr.getStatsH().getPfPLSS().setAmountPL(pl);
			pfDBCtr.getStatsH().getPfPLSS().setAmountPLStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(pl, 1));
			pfDBCtr.getStatsH().getPfPLSS()
					.setPerPL(Precision.round(((pl * 100) / pfDBCtr.getStatsH().getTotalInv()), 1));
			pfDBCtr.getStatsH()
					.setCurrVal(pfDBCtr.getStatsH().getTotalInv() + pfDBCtr.getStatsH().getPfPLSS().getAmountPL());

			pfDBCtr.getStatsH().setCurrValStr(
					UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfDBCtr.getStatsH().getCurrVal(), 2));

			int pfPos = (int) pfDBCtr.getHoldings().stream().filter(s -> s.getPl() > 0).count();
			int pfLos = pfDBCtr.getHoldings().size() - pfPos;

			pfDBCtr.getStatsH().getPfPLSS().setNumGainers(pfPos);
			pfDBCtr.getStatsH().getPfPLSS().setNumLosers(pfLos);

			double dayPL = pfDBCtr.getHoldings().stream().mapToDouble(PFHoldingsPL::getDayPL).sum();
			pfDBCtr.getStatsH().getTodayPLSS().setAmountPL(dayPL);
			pfDBCtr.getStatsH().getTodayPLSS()
					.setAmountPLStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(dayPL, 1));
			int dayPos = (int) pfDBCtr.getHoldings().stream().filter(c -> c.getDayPL() > 0).count();
			int dayLos = pfDBCtr.getHoldings().size() - dayPos;
			pfDBCtr.getStatsH().getTodayPLSS().setNumGainers(dayPos);
			pfDBCtr.getStatsH().getTodayPLSS().setNumLosers(dayLos);

			double ydayValue = (pfDBCtr.getStatsH().getTotalInv() + pfDBCtr.getStatsH().getPfPLSS().getAmountPL())
					- dayPL;
			double daysGainPer = (dayPL / ydayValue) * 100;
			pfDBCtr.getStatsH().getTodayPLSS().setPerPL((Precision.round(daysGainPer, 1)));

			// Get Max Gainer
			PFHoldingsPL maxGainer = Collections.max(pfDBCtr.getHoldings(), Comparator.comparing(x -> x.getPlPer()));
			if (maxGainer != null)
			{
				pfDBCtr.getStatsH().setMaxGainer(new ScripPLSS(maxGainer.getScCode(), maxGainer.getPl(),
						maxGainer.getPlStr(), maxGainer.getPlPer()));
			}

			// Get Max Loser
			PFHoldingsPL maxLoser = Collections.min(pfDBCtr.getHoldings(), Comparator.comparing(x -> x.getPlPer()));
			if (maxGainer != null)
			{
				pfDBCtr.getStatsH().setMaxLoser(new ScripPLSS(maxLoser.getScCode(), maxLoser.getPl(),
						maxLoser.getPlStr(), maxLoser.getPlPer()));
			}

			// Populate PF Start Date
			pfDBCtr.getStatsH().setInvSince(repoHCI.getPFInvSinceDate());
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			pfDBCtr.getStatsH().setInvSinceStr(formatter.format(pfDBCtr.getStatsH().getInvSince()));
		}

	}

}
