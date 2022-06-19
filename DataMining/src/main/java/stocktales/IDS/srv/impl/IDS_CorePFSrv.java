package stocktales.IDS.srv.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.DataLake.srv.intf.DL_HistoricalPricesSrv;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumSchemaDepAmntsUpdateMode;
import stocktales.IDS.events.EV_PFTxn;
import stocktales.IDS.exceptions.CorePFException;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.entity.PFVolProfile;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoHCI;
import stocktales.IDS.model.pf.repo.RepoMoneyBag;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.model.pf.repo.RepoPFVolProfile;
import stocktales.IDS.pojo.DateAmount;
import stocktales.IDS.pojo.IDS_SCAlloc;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.IDS_SC_BonusIP;
import stocktales.IDS.pojo.IDS_SC_PL;
import stocktales.IDS.pojo.IDS_SC_PL_Items;
import stocktales.IDS.pojo.IDS_SC_SplitIP;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_ScripUnits;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.pojo.XIRRContainer;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.IDS_PFTxn_UI;
import stocktales.IDS.pojo.UI.IDS_PF_BuyPHeader;
import stocktales.IDS.pojo.UI.IDS_Scrip_Details;
import stocktales.IDS.srv.intf.IDS_DeploymentAmntSrv;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.basket.allocations.config.pojos.Urls;
import stocktales.durations.UtilDurations;
import stocktales.exceptions.SchemaUpdateException;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.strategy.helperPOJO.SectorAllocations;
import stocktales.usersPF.enums.EnumTxnType;
import yahoofinance.histquotes.Interval;

/*
 * CORE PF Service
 */
@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_CorePFSrv implements stocktales.IDS.srv.intf.IDS_CorePFSrv
{
	@Autowired
	private RepoPFVolProfile repoPFVP;

	@Autowired
	@Qualifier("DL_HistoricalPricesSrv_IDS")
	private DL_HistoricalPricesSrv hpDBSrv;

	@Autowired
	private SCPricesMode scPriceModeDB;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private IDS_VPSrv vpSrv;

	@Autowired
	private RepoHCI repoHCI;

	@Autowired
	private RepoHC repoHC;

	@Autowired
	private RepoMoneyBag repoMB;

	@Autowired
	private Urls urls;

	@Autowired
	private IDS_DeploymentAmntSrv depAmntSrv;

	@Autowired
	private IDS_MoneyBagSrv mbSrv;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private MessageSource msgSrc;

	@Value("${pf.allocTotalErr}")
	private final String allocSumErr = "";

	private static final long daysFreshBuyGap = 20;
	private static final double minbuymnt = 3000;

	@Override
	public List<IDS_VPDetails> refreshPFVolatilityProfiles() throws Exception
	{
		List<IDS_VPDetails> vpDetailsList = null;
		if (vpSrv != null && repoPFSchema != null && repoPFVP != null)
		{
			List<String> scodes = repoPFSchema.getPFScripCodes();
			if (scodes != null)
			{
				if (scodes.size() > 0)
				{
					vpDetailsList = new ArrayList<IDS_VPDetails>();
					for (String scrip : scodes)
					{
						IDS_VPDetails vpDetails = vpSrv.getVolatilityProfileDetailsforScrip(scrip);
						if (vpDetails != null)
						{
							vpDetailsList.add(vpDetails);
						}
					}

					if (vpDetailsList.size() > 0)
					{
						repoPFVP.deleteAll();
						for (IDS_VPDetails ids_VPDetails : vpDetailsList)
						{
							PFVolProfile pfvp = new PFVolProfile();
							pfvp.setSccode(ids_VPDetails.getSccode());
							pfvp.setSma1b(ids_VPDetails.getSma1breaches());
							pfvp.setSma2b(ids_VPDetails.getSma2breaches());
							pfvp.setSma3b(ids_VPDetails.getSma3breaches());
							pfvp.setSma4b(ids_VPDetails.getSma4breaches());
							pfvp.setScore(ids_VPDetails.getVolscore());
							pfvp.setProfile(ids_VPDetails.getVolprofile());

							repoPFVP.save(pfvp);
						}
					}
				}
			}
		}

		return vpDetailsList;

	}

	@Override
	public void processCorePFTxn(HCI txn) throws Exception
	{
		if (txn.getDate() == null)
		{
			txn.setDate(UtilDurations.getTodaysDateOnly());
		}
		publishCorePFTxn(txn);

	}

	@Override
	public List<IDS_SMAPreview> getPFSchemaSMAPreview() throws Exception
	{
		List<IDS_SMAPreview> pfSMAPreview = null;

		if (repoPFSchema.count() > 0)
		{
			pfSMAPreview = new ArrayList<IDS_SMAPreview>();
			int[] smaIntervals = new int[]
			{ 264, 396, 528, 660 };

			for (PFSchema pfSchema : repoPFSchema.findAll())
			{
				// 1. Get the SMA Details for the Scrip

				IDS_ScSMASpread scSMASpread = null;
				try
				{
					if (scPriceModeDB.getScpricesDBMode() == 1)
					{
						scSMASpread = hpDBSrv.getSMASpreadforScrip(pfSchema.getSccode(), smaIntervals);
					} else
					{
						scSMASpread = StockPricesUtility.getSMASpreadforScrip(pfSchema.getSccode(), smaIntervals,
								Calendar.YEAR, 1, Interval.DAILY);
					}
				} catch (Exception e)
				{
					// DO nothing Ignore that Scrip
				}
				if (scSMASpread != null)
				{
					if (scSMASpread.getPrSMAList().size() > 0)
					{
						if (scPriceModeDB.getScpricesDBMode() == 1)
						{
							IDS_SMAPreview smaPreview = new IDS_SMAPreview();
							smaPreview.setScCode(pfSchema.getSccode());
							smaPreview.setClosePrice(
									Precision.round(scSMASpread.getPrSMAList().get(0).getClosePrice(), 0));

							// Get SMA1
							IDS_SMASpread sma1Entry = scSMASpread.getPrSMAList().get(smaIntervals[0]);
							if (sma1Entry != null)
							{
								smaPreview.setSMAI1(Precision.round(sma1Entry.getSMAI1(), 0));
							}

							// Get SMA2
							IDS_SMASpread sma2Entry = scSMASpread.getPrSMAList().get(smaIntervals[1]);
							if (sma2Entry != null)
							{
								smaPreview.setSMAI2(Precision.round(sma2Entry.getSMAI2(), 0));
							}

							// Get SMA3
							IDS_SMASpread sma3Entry = scSMASpread.getPrSMAList().get(smaIntervals[2]);
							if (sma3Entry != null)
							{
								smaPreview.setSMAI3(Precision.round(sma3Entry.getSMAI3(), 0));
							}

							// Get SMA4
							IDS_SMASpread sma4Entry = scSMASpread.getPrSMAList().get(smaIntervals[3]);
							if (sma4Entry != null)
							{
								smaPreview.setSMAI4(Precision.round(sma4Entry.getSMAI4(), 0));
							}
							pfSMAPreview.add(smaPreview);

						} else
						{
							IDS_SMASpread smaSpread = scSMASpread.getPrSMAList()
									.get(scSMASpread.getPrSMAList().size() - 1);
							if (smaSpread != null)
							{
								IDS_SMAPreview smaPreview = new IDS_SMAPreview();
								smaPreview.setScCode(pfSchema.getSccode());
								smaPreview.setClosePrice(Precision.round(smaSpread.getClosePrice(), 0));
								smaPreview.setSMAI1(Precision.round(smaSpread.getSMAI1(), 0));
								smaPreview.setSMAI2(Precision.round(smaSpread.getSMAI2(), 0));
								smaPreview.setSMAI3(Precision.round(smaSpread.getSMAI3(), 0));
								smaPreview.setSMAI4(Precision.round(smaSpread.getSMAI4(), 0));
								pfSMAPreview.add(smaPreview);
							}
						}
					}

				}
			}
		}

		return pfSMAPreview;
	}

	@Override
	public IDS_BuyProposalBO getBuyProposals() throws Exception
	{
		IDS_BuyProposalBO buyP = null;
		List<IDS_SCBuyProposal> buyProps = null;

		List<PFSchema> pfSchEntities = repoPFSchema.findAll();
		if (pfSchEntities.size() > 0)
		{
			// Only where deployable amnt > 0
			List<PFSchema> pfSchEntitiesPosDepAmnts = pfSchEntities.stream().filter(x -> x.getDepamnt() > 0)
					.collect(Collectors.toList());
			if (pfSchEntitiesPosDepAmnts.size() > 0)
			{
				List<IDS_SMAPreview> pfSMAPvw = null;
				buyP = new IDS_BuyProposalBO();

				pfSMAPvw = this.getPFSchemaSMAPreview();

				if (pfSMAPvw != null)
				{
					if (pfSMAPvw.size() > 0)
					{
						buyProps = new ArrayList<IDS_SCBuyProposal>();
						long millis = System.currentTimeMillis();
						Date dateToday = new java.util.Date(millis);

						for (PFSchema pfSchema : pfSchEntitiesPosDepAmnts)
						{
							boolean isPurchase = false;
							boolean isBreached = false;
							double buyAmnt = 0;
							Optional<IDS_SMAPreview> smaPwO = pfSMAPvw.stream()
									.filter(w -> w.getScCode().equals(pfSchema.getSccode())).findFirst();
							if (smaPwO.isPresent())
							{
								IDS_SMAPreview smaPWUS = smaPwO.get();
								IDS_SMAPreview smaPW = this.getSMASortedforIDS(smaPWUS);
								List<HCI> hcIListBuyScrip = repoHCI.findAllByTxntypeAndSccode(EnumTxnType.Buy,
										pfSchema.getSccode());
								Date lastBuyDate = repoHCI.getlastBuyTxnDateforScrip(pfSchema.getSccode());

								// Get CMP of Scrip

								double cmpScrip = StockPricesUtility.getQuoteforScrip(pfSchema.getSccode()).getQuote()
										.getPrice().doubleValue();

								/*
								 * Check - CMP below any SMA
								 */

								// SMA4
								if (cmpScrip < smaPW.getSMAI4())
								{
									/*
									 * Last breach was sma4 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1, sma2, sma3
									 */
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										} else
										{
											/**
											 * EXCEPTIONAL PURCHASE - MARKET FALL In case of last txn not elapsed 20
											 * days, but - cmp < sma4 by at least 10% and then stagger Buy for every 5%
											 * fall from last purchase at least SHARP & SWIFT CORRECTIONS
											 */
											if (UtilPercentages.getPercentageDelta(cmpScrip, smaPW.getSMAI4(), 0) >= 10)
											{
												double depAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(
														pfSchema.getSccode(), EnumSMABreach.sma4);
												if (depAmnt > cmpScrip)
												{
													// Get Last Purchase Txn. for Scrip

													Date latestPurchase = repoHCI
															.getlastBuyTxnDateforScrip(pfSchema.getSccode());
													if (lastBuyDate != null)
													{
														HCI lastTxn = repoHCI
																.findAllByTxntypeAndSccode(EnumTxnType.Buy,
																		pfSchema.getSccode())
																.stream()
																.filter(x -> x.getDate().compareTo(latestPurchase) == 0)
																.findFirst().get();
														if (lastTxn != null)
														{

															if (UtilPercentages.getPercentageDelta(cmpScrip,
																	lastTxn.getTxnppu(), 0) >= 5)
															{

																isPurchase = true;
															}

														}
													}

												}

											}
										}

									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank

											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(lastBuyDate) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma4.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{
										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma4);
										if (buyAmnt > cmpScrip)
										{
											buyProps.add(generateBuyProposal(pfSchema, buyAmnt, EnumSMABreach.sma4));
											isBreached = true;
										}

									}

								}

								// SMA3
								if (cmpScrip < smaPW.getSMAI3() && !isBreached)
								{
									/*
									 * Last breach was sma3 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1 or sma2
									 */
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank

											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(lastBuyDate) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma3.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{
										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma3);
										if (buyAmnt > cmpScrip)
										{
											buyProps.add(generateBuyProposal(pfSchema, buyAmnt, EnumSMABreach.sma3));
											isBreached = true;
										}
									}

								}

								// SMA2
								if (cmpScrip < smaPW.getSMAI2() && !isBreached)
								{
									/*
									 * Last breach was sma2 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1
									 */

									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank

											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(lastBuyDate) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{

												/*
												 * Last breach for less than 20 days should be shallower than current
												 * SMA Breach - then only current Breach can be invoked
												 */
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma2.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma2);
										if (buyAmnt > cmpScrip)
										{
											buyProps.add(generateBuyProposal(pfSchema, buyAmnt, EnumSMABreach.sma2));
											isBreached = true;
										}

									}

								}

								// SMA1 - Only 20 days gap or fresh BUY
								if (cmpScrip < smaPW.getSMAI1() && !isBreached)
								{
									// Last breach was sma1 so no further buy since 20 days at least
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (isPurchase)
									{
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma1);
										if (buyAmnt > cmpScrip)
										{
											buyProps.add(generateBuyProposal(pfSchema, buyAmnt, EnumSMABreach.sma1));
											isBreached = true;
										}
									}

								}

							}

						}

					}
				}
			}
		}

		if (buyProps != null)
		{
			if (buyProps.size() > 0)
			{
				/*
				 * Remove Individual Purchases below minimum threshold amount
				 */
				buyProps.removeIf(s -> s.getAmount() < minbuymnt);
				if (buyProps.size() > 0)
				{
					buyP.setBuyP(buyProps);
					populateBuyProposalHeaderandSMA(buyP);
					buyP.getBuyPHeader().setScurl(urls.getCorepfUrl());
				} else
				{
					buyP = new IDS_BuyProposalBO();
				}

			}
		}
		return buyP;

	}

	@Override
	@Transactional
	public void processAllocationChanges(IDS_ScAllocMassUpdate allocMassUpdate) throws Exception
	{

		if (allocMassUpdate != null)
		{
			if (allocMassUpdate.getScAllocList().size() > 0)
			{
				double totalDepAmnt = 0; // A
				double totalUsedAmnt = 0; // B
				double totalCorpus = 0; // P
				boolean isCreate = false;

				double sumIdealAloc = Precision.round(
						allocMassUpdate.getScAllocList().stream().mapToDouble(IDS_SCAlloc::getIdealAlloc).sum(), 1);

				double sumIncAlloc = Precision.round(
						allocMassUpdate.getScAllocList().stream().mapToDouble(IDS_SCAlloc::getIncAlloc).sum(), 1);

				if (sumIdealAloc != 100 || sumIncAlloc != 100) // Incremental Allocation Sum != 0
				{
					throw new SchemaUpdateException(msgSrc.getMessage("pf.allocTotalErr", new Object[]
					{ sumIdealAloc, sumIncAlloc }, Locale.ENGLISH));
				}

				if (repoPFSchema.count() > 0)
				{
					totalDepAmnt = repoPFSchema.getSumDeploymentAmount();
				} else
				{
					allocMassUpdate.setDepAmtMode(EnumSchemaDepAmntsUpdateMode.None);
					isCreate = true;
				}

				List<PFSchema> currSchemaList = repoPFSchema.findAll();

				switch (allocMassUpdate.getDepAmtMode())
				{
				case CurrDepAmnts:
				case None:
					for (IDS_SCAlloc chgAlloc : allocMassUpdate.getScAllocList())
					{
						if (!isCreate)
						{
							// Search for Entity in Current Schema
							Optional<PFSchema> currSchemaEntO = currSchemaList.stream()
									.filter(x -> x.getSccode().equals(chgAlloc.getScCode())).findFirst();
							if (currSchemaEntO.isPresent())
							{
								PFSchema currSchemaEnt = currSchemaEntO.get();
								// Check for Changes
								if (chgAlloc.getIdealAlloc() != currSchemaEnt.getIdealalloc())
								{
									repoPFSchema.updateIdealAllocforScrip(chgAlloc.getScCode(),
											chgAlloc.getIdealAlloc());
								}
								if (chgAlloc.getIncAlloc() != currSchemaEnt.getIncalloc())
								{
									repoPFSchema.updateIncAllocforScrip(chgAlloc.getScCode(), chgAlloc.getIncAlloc());
								}
								if ((allocMassUpdate.getDepAmtMode() == EnumSchemaDepAmntsUpdateMode.CurrDepAmnts)
										&& totalDepAmnt != 0)
								{
									double chgDepAmnt = totalDepAmnt * chgAlloc.getIncAlloc() * .01;
									repoPFSchema.updateDeployableAmountforScrip(chgAlloc.getScCode(), chgDepAmnt);
								}
								if (!chgAlloc.getSector().equals(currSchemaEnt.getSector()))
								{
									repoPFSchema.updateSectorforScrip(chgAlloc.getScCode(), chgAlloc.getSector());
								}

							} else // New Scrip to Existing Schema
							{
								double newDepAmnt = totalDepAmnt * chgAlloc.getIncAlloc() * .01;
								PFSchema newPFSchema = new PFSchema(chgAlloc.getScCode(), chgAlloc.getSector(),
										chgAlloc.getIdealAlloc(), chgAlloc.getIncAlloc(), newDepAmnt);
								repoPFSchema.save(newPFSchema);
							}

						} else // New Schema Creation
						{
							// Get Deployable Amount from Money Bag
							double newDepAmnt = 0;
							if (repoMB.count() > 0)
							{
								newDepAmnt = mbSrv.getDeployableAmount() * chgAlloc.getIncAlloc() * .01;
							} else
							{
								newDepAmnt = 0;
							}

							PFSchema newPFSchema = new PFSchema(chgAlloc.getScCode(), chgAlloc.getSector(),
									chgAlloc.getIdealAlloc(), chgAlloc.getIncAlloc(), newDepAmnt);
							repoPFSchema.save(newPFSchema);
						}
					}

					break;

				case Holistic:
					totalUsedAmnt = repoHC.getTotalInvestments();
					totalCorpus = totalDepAmnt + totalUsedAmnt;

					for (IDS_SCAlloc chgAlloc : allocMassUpdate.getScAllocList())
					{
						if (!isCreate)
						{
							// Search for Entity in Current Schema
							Optional<PFSchema> currSchemaEntO = currSchemaList.stream()
									.filter(x -> x.getSccode().equals(chgAlloc.getScCode())).findFirst();
							if (currSchemaEntO.isPresent())
							{
								PFSchema currSchemaEnt = currSchemaEntO.get();
								// Check for Changes
								if (chgAlloc.getIdealAlloc() != currSchemaEnt.getIdealalloc())
								{
									repoPFSchema.updateIdealAllocforScrip(chgAlloc.getScCode(),
											chgAlloc.getIdealAlloc());
								}
								if (chgAlloc.getIncAlloc() != currSchemaEnt.getIncalloc())
								{
									repoPFSchema.updateIncAllocforScrip(chgAlloc.getScCode(), chgAlloc.getIncAlloc());
								}
								if ((allocMassUpdate.getDepAmtMode() == EnumSchemaDepAmntsUpdateMode.Holistic)
										&& totalDepAmnt != 0)
								{
									// Get Holistic Allocation for Scrip
									double AH = totalCorpus * chgAlloc.getIdealAlloc() * .01;
									// Get Used allocation for Scrip

									double UA;
									try
									{
										UA = repoHC.getTotalInvestmentforScrip(chgAlloc.getScCode());
									} catch (Exception e)
									{
										UA = 0;
									}

									// Calculate Remaining Allocation for Scrip
									double RA = AH - UA;

									repoPFSchema.updateDeployableAmountforScrip(chgAlloc.getScCode(),
											Precision.round(RA, 2));
								}
								if (!chgAlloc.getSector().equals(currSchemaEnt.getSector()))
								{
									repoPFSchema.updateSectorforScrip(chgAlloc.getScCode(), chgAlloc.getSector());
								}

							} else // New Scrip to Existing Schema
							{
								double newDepAmnt = totalCorpus * chgAlloc.getIncAlloc() * .01;
								PFSchema newPFSchema = new PFSchema(chgAlloc.getScCode(), chgAlloc.getSector(),
										chgAlloc.getIdealAlloc(), chgAlloc.getIncAlloc(), newDepAmnt);
								repoPFSchema.save(newPFSchema);
							}

						} else // New Schema Creation
						{
							// Get Deployable Amount from Money Bag
							double newDepAmnt = 0;
							if (repoMB.count() > 0)
							{
								newDepAmnt = mbSrv.getDeployableAmount() * chgAlloc.getIncAlloc() * .01;
							} else
							{
								newDepAmnt = 0;
							}

							PFSchema newPFSchema = new PFSchema(chgAlloc.getScCode(), chgAlloc.getSector(),
									chgAlloc.getIdealAlloc(), chgAlloc.getIncAlloc(), newDepAmnt);
							repoPFSchema.save(newPFSchema);
						}
					}

					break;

				default:
					break;
				}

			}

		}

	}

	@Override
	public List<IDS_SCBuyProposal> autoProcessBuyProposals() throws Exception
	{
		List<IDS_SCBuyProposal> buyProposals = this.getBuyProposals().getBuyP();
		if (buyProposals != null)
		{
			if (buyProposals.size() > 0)
			{
				for (IDS_SCBuyProposal buyProposal : buyProposals)
				{
					// Prepare HCI POJO
					HCI pfTxn = new HCI();
					pfTxn.setSccode(buyProposal.getScCode());
					pfTxn.setDate(UtilDurations.getTodaysDate());
					pfTxn.setTxntype(EnumTxnType.Buy);
					pfTxn.setUnits(buyProposal.getNumUnitsBuy());
					pfTxn.setTxnppu(buyProposal.getPpuBuy());
					pfTxn.setSmarank(buyProposal.getSmaBreach().ordinal());

					// Publish the Transaction
					this.processCorePFTxn(pfTxn);
				}

			}
		}
		return buyProposals;
	}

	@Override
	@Transactional
	public void removeScrip4mSchemaPF(String scCode, double sellPricePPU) throws Exception
	{
		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				if (repoPFSchema.findById(scCode).isPresent())
				{
					HCI exitTxn = new HCI();
					exitTxn.setDate(UtilDurations.getTodaysDate());
					exitTxn.setSccode(scCode);
					exitTxn.setTxnppu(sellPricePPU);
					exitTxn.setTxntype(EnumTxnType.Exit);

					Optional<HC> HCO = repoHC.findById(scCode);
					if (HCO.isPresent())
					{
						exitTxn.setUnits(HCO.get().getUnits());
					} else
					{
						exitTxn.setUnits(0); // Not in PF - Only Schema Removal
					}

					this.processCorePFTxn(exitTxn);
				}
			}
		}

	}

	@Override
	public IDS_SC_PL getRealizedPL4Scrip(String scCode) throws Exception
	{
		IDS_SC_PL scPL = null;
		if (scCode.trim().length() > 0 && repoHCI != null)
		{
			try
			{
				List<HCI> sellTxns = repoHCI.findAllByTxntypeAndSccode(EnumTxnType.Sell, scCode);
				if (sellTxns != null)
				{
					if (sellTxns.size() > 0)
					{
						scPL = new IDS_SC_PL();
						List<HCI> buyTxns = repoHCI.findAllByTxntypeAndSccode(EnumTxnType.Buy, scCode);
						// Loop through All Sell Txns.
						for (HCI sellTxn : sellTxns)
						{
							if (buyTxns != null && sellTxn.getUnits() > 0)
							{
								if (buyTxns.size() > 0)
								{
									// Get all Prior Buy Txns.
									List<HCI> buyTxnsPrio = buyTxns.stream()
											.filter(x -> x.getDate().before(sellTxn.getDate()))
											.collect(Collectors.toList());
									if (buyTxnsPrio != null)
									{
										if (buyTxnsPrio.size() > 0)
										{
											/*
											 * Determine Prior Purchase Txns. PPU
											 */
											double sumPrioPurchaseTxns = buyTxnsPrio.stream()
													.map(p -> p.getUnits() * (p.getTxnppu()))
													.collect(Collectors.summingDouble(Double::doubleValue));
											double sumUnits = buyTxnsPrio.stream().mapToInt(HCI::getUnits).sum();
											double ppu = 0;
											double realzPL = 0;
											if (sumUnits > 0)
											{
												ppu = sumPrioPurchaseTxns / sumUnits;
											}
											realzPL = Precision.round(sellTxn.getUnits() * (sellTxn.getTxnppu() - ppu),
													0);
											scPL.getPlItems().add(new IDS_SC_PL_Items(sellTxn.getDate(), realzPL));

										}
									}
								}
							}
						}

						// All Sales Processed- Consolidate Nett Realized PL
						if (scPL.getPlItems().size() > 0)
						{
							scPL.setNettPLAmount(Precision.round(
									scPL.getPlItems().stream().mapToDouble(IDS_SC_PL_Items::getPlAmount).sum(), 1));
						}
					}
				}
			} catch (Exception e)
			{
				// Do Nothing - Probably no Sell Txn.
			}
		}

		return scPL;
	}

	@Override
	public XIRRContainer calculateXIRRforPF() throws Exception
	{
		XIRRContainer xirrCont = null;

		if (repoHCI != null)
		{
			List<HCI> txns = repoHCI.findAll();

			if (txns != null)
			{
				if (txns.size() > 0)
				{
					xirrCont = new XIRRContainer();
					/**
					 * Get Unique Date Groups
					 */
					List<Date> DatesUQ = repoHCI.getUniqueTxnDates();

					// txns.stream().filter(w ->
					// DatesUQ.add(w.getDate())).distinct().collect(Collectors.toList());

					for (Date date : DatesUQ)
					{
						List<HCI> txnsOnDate = txns.stream().filter(e -> e.getDate().compareTo(date) == 0)
								.collect(Collectors.toList());
						if (txnsOnDate != null)
						{
							double dayAmount = 0;
							for (HCI txn : txnsOnDate)
							{
								switch (txn.getTxntype())
								{
								case Buy: // Negative Cash Flow = Buy
									dayAmount += txn.getTxnppu() * txn.getUnits() * -1;
									break;
								case Sell: // Positive Cash Flow = Sell
									dayAmount += txn.getTxnppu() * txn.getUnits();
									break;
								case Exit: // Positive Cash Flow = Exit
									dayAmount += txn.getTxnppu() * txn.getUnits();
									break;

								default:
									break;
								}
							}

							// consolidate for the Date
							if (dayAmount != 0) // Ignore other Then Buy/Sell/Exit
							{
								xirrCont.getTransactions().add(new DateAmount(date, dayAmount));
							}
						}
					}

					// Final Entry for PF Current Price as Positive Cash Flow Entry
					xirrCont.getTransactions()
							.add(new DateAmount(UtilDurations.getTodaysDateOnly(), this.getPFCurrVal()));

					if (xirrCont.getTransactions().size() > 0)
					{
						/*
						 * CAll the Utility to Calculate XIRR
						 */
						List<Transaction> txnColl = new ArrayList<Transaction>();
						txnColl = xirrCont.getTransactions().stream()
								.map(t -> new Transaction(t.getAmount(), t.getDate())).collect(Collectors.toList());

						double xirr;
						try
						{
							xirr = Precision.round(new Xirr(txnColl).xirr() * 100, 1);
							xirrCont.setXirr(xirr);
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}

		}

		return xirrCont;
	}

	/**
	 * --------------------------------------------------------------------------
	 * -------------------- PRIVATE SECTION ------------------
	 * -------------------------------------------------------------------------
	 */

	private void publishCorePFTxn(HCI txn)
	{
		// Create an Instance of Core PF Transaction Event
		EV_PFTxn evPF = new EV_PFTxn(this, txn);
		// Publish the Event using Injected Application Event Publisher
		applicationEventPublisher.publishEvent(evPF);

	}

	private IDS_SMAPreview getSMASortedforIDS(IDS_SMAPreview smaPvwO)
	{
		IDS_SMAPreview smaIDS = smaPvwO;

		double[] smaS = new double[4];
		int i = 0;

		if (smaPvwO.getSMAI1() > 0)
		{
			smaS[i] = smaPvwO.getSMAI1();
			i++;
		}

		if (smaPvwO.getSMAI2() > 0)
		{
			smaS[i] = smaPvwO.getSMAI2();
			i++;
		}

		if (smaPvwO.getSMAI3() > 0)
		{
			smaS[i] = smaPvwO.getSMAI3();
			i++;
		}

		if (smaPvwO.getSMAI4() > 0)
		{
			smaS[i] = smaPvwO.getSMAI4();
			i++;
		}

		Arrays.sort(smaS);

		smaIDS.setSMAI4(smaS[0]);
		smaIDS.setSMAI3(smaS[1]);
		smaIDS.setSMAI2(smaS[2]);
		smaIDS.setSMAI1(smaS[3]);

		return smaIDS;
	}

	/**
	 * Push the List of PF Transaction(s) after conducting at Broker Platform
	 * 
	 * 1. Capture any deviations ( Dep. Amnt - Txn. Amount) for each Scrip in terms
	 * of DEp Amnt and Curr Inv. 2. Update/Increment the Dep Amnts in Schema by
	 * deviations for scrips they occur 3. Perform the PF transactions 4. Decrement
	 * the Dep Amnts in Schema by deviations for scrips as captured in 1.
	 * 
	 * @param pfTxns - List<HCI>
	 * @throws Exception
	 */
	@Override
	@Transactional
	public void pushandSyncPFTxn(List<HCI> pfTxns) throws CorePFException
	{
		List<SectorAllocations> scDEviations = new ArrayList<SectorAllocations>();
		if (pfTxns != null)
		{
			if (pfTxns.size() > 0)
			{

				try
				{
					// Get Sum of All Purchase Transactions - (Sum of Units * PPU) for each element
					// in list
					double sumPurchaseTxns = pfTxns.stream().map(p -> p.getUnits() * (p.getTxnppu()))
							.collect(Collectors.summingDouble(Double::doubleValue));

					if (mbSrv.validateMassPurchaseTxnsAmount(sumPurchaseTxns))
					{

						List<PFSchema> schemaList = repoPFSchema.findAll();
						if (schemaList != null)
						{
							if (schemaList.size() > 0)
							{
								for (HCI txn : pfTxns)
								{
									// Get depAmnt from Schema
									double depAmnt = schemaList.stream()
											.filter(q -> q.getSccode().equals(txn.getSccode())).findFirst().get()
											.getDepamnt();
									double deviation = depAmnt - (txn.getTxnppu() * txn.getUnits());

									if (deviation < 0) // You have less than you need for Curr Txn.
									{
										// Add to Deviations Table
										scDEviations.add(new SectorAllocations(txn.getSccode(), deviation));

										// Update PFScema Temporarily for txn. to get through
										repoPFSchema.updateDeployableAmountforScrip(txn.getSccode(),
												depAmnt + (deviation * -1));
									}

									// Proceed with the txn.
									this.processCorePFTxn(txn);

									// Adjust back the deviation to Dep. Amnt. for Future adjustments
									if (deviation < 0)
									{
										// Need to get REfreshed Dep Amnt after above Update
										depAmnt = repoPFSchema.getDeployableAmountforScrip(txn.getSccode());
										repoPFSchema.updateDeployableAmountforScrip(txn.getSccode(),
												depAmnt + deviation);
									}

								}
							}
						}
					}
				} catch (Exception e)
				{
					CorePFException cpe = new CorePFException(e.getMessage());
					throw cpe;
				}

			}
		}
	}

	@Override
	public IDS_Scrip_Details getScripDetails4Scrip(String scCode) throws Exception
	{
		IDS_Scrip_Details scDetails = null;

		scDetails = populateDetails4Scrip(scCode);

		return scDetails;
	}

	@Override
	@Transactional
	public void adjustPF4StockSplit(IDS_SC_SplitIP scSplitIP) throws Exception
	{
		if (scSplitIP != null)
		{
			if (StringUtils.hasText(scSplitIP.getScCode()) && scSplitIP.getOneToSplitIntoSharesNum() > 1)
			{
				Optional<HC> hcO = repoHC.findById(scSplitIP.getScCode());
				List<HCI> hcItems = repoHCI.findAllBySccode(scSplitIP.getScCode());
				if (hcO.isPresent() && hcItems != null)
				{
					repoHC.updatePPUUnitsforScrip(scSplitIP.getScCode(),
							hcO.get().getUnits() * scSplitIP.getOneToSplitIntoSharesNum(),
							hcO.get().getPpu() / scSplitIP.getOneToSplitIntoSharesNum());

					// Update PPU and Units in Each Txn Item
					for (HCI hci : hcItems)
					{
						// No Split up for Bonus Sell Units - to avoid Fractions
						if (hci.getTxntype() != EnumTxnType.BonusSell)
						{
							repoHCI.updatePPUUnitsforItemTxn(hci.getTid(),
									hci.getUnits() * scSplitIP.getOneToSplitIntoSharesNum(),
									hci.getTxnppu() / scSplitIP.getOneToSplitIntoSharesNum());
						}
					}

				}
			}
		}

	}

	@Override
	@Transactional
	public void adjustPF4StockBonus(IDS_SC_BonusIP scBonusIP) throws Exception
	{

		if (scBonusIP != null)
		{
			if (StringUtils.hasText(scBonusIP.getScCode()) && scBonusIP.getForeveryNShares() > 1
					&& scBonusIP.getToGetSharesNum() > 1)
			{
				int unitsH = 0, unitsI = 0, unitsAdj, sumBuys = 0, sumSells = 0;
				HCI itemAdjusted = null;
				double amntAdj = 0;
				Optional<HC> hcO = repoHC.findById(scBonusIP.getScCode());
				List<HCI> hcItems = repoHCI.findAllBySccode(scBonusIP.getScCode());
				if (hcO.isPresent() && hcItems != null)
				{

					HC hc = hcO.get();
					double nettRatio = (double) scBonusIP.getToGetSharesNum() / scBonusIP.getForeveryNShares();
					int remainder = hc.getUnits() % scBonusIP.getForeveryNShares();

					if (remainder == 0)
					{

						unitsH = (int) (hcO.get().getUnits() * nettRatio);
					} else
					{
						unitsH = (int) ((hcO.get().getUnits() - remainder) * nettRatio);
					}

					// Get Items Units Total
					for (HCI hci : hcItems)
					{

						if (hci.getTxntype() == EnumTxnType.Buy)
						{
							sumBuys += hci.getUnits() * nettRatio;
						}

						if (hci.getTxntype() == EnumTxnType.Sell)
						{
							sumSells += hci.getUnits() * nettRatio;
						}

					}
					unitsI = sumBuys - sumSells;

					/**
					 * Establish Items Units Adjustments if any needed
					 */
					unitsAdj = unitsH - unitsI;
					if (unitsAdj > 0) // Add Adj Units to Buy Txn.
					{
						Optional<HCI> HCIO = hcItems.stream().filter(c -> c.getTxntype() == EnumTxnType.Buy)
								.findFirst();
						if (HCIO.isPresent())
						{
							itemAdjusted = HCIO.get();
							amntAdj = (itemAdjusted.getUnits() * itemAdjusted.getTxnppu())
									/ (itemAdjusted.getUnits() * nettRatio + unitsAdj);
							itemAdjusted.setUnits((int) (itemAdjusted.getUnits() * nettRatio + unitsAdj));
							itemAdjusted.setTxnppu(Precision.round(amntAdj, 2));

						}

					} else if (unitsAdj < 0) // Add Adj Units to Sell Txn.
					{
						Optional<HCI> HCIO = hcItems.stream().filter(c -> c.getTxntype() == EnumTxnType.Sell)
								.findFirst();
						if (HCIO.isPresent())
						{
							itemAdjusted = HCIO.get();
							amntAdj = (itemAdjusted.getUnits() * itemAdjusted.getTxnppu())
									/ (itemAdjusted.getUnits() * nettRatio + unitsAdj);
							itemAdjusted.setUnits((int) (itemAdjusted.getUnits() * nettRatio + unitsAdj));
							itemAdjusted.setTxnppu(Precision.round(amntAdj, 2));
						}

					}

					/**
					 * UPdates Start
					 */
					// HC Update
					repoHC.updatePPUUnitsforScrip(scBonusIP.getScCode(), unitsH, hcO.get().getPpu() / nettRatio);

					// Update PPU and Units in Each Txn Item
					for (HCI hci : hcItems)
					{
						if (hci.getTxntype() != EnumTxnType.BonusSell)
						{
							if (hci.getTid() != itemAdjusted.getTid())
							{
								int unitsItem = (int) (hci.getUnits() * nettRatio);
								repoHCI.updatePPUUnitsforItemTxn(hci.getTid(), unitsItem, hci.getTxnppu() / nettRatio);
							} else
							{
								repoHCI.save(hci);
							}
						}
					}

					if (remainder > 0)
					{
						// Create a Sell PF Txn for Fractional(remainder) units
						double cmp = StockPricesUtility.getQuoteforScrip(scBonusIP.getScCode()).getQuote().getPrice()
								.doubleValue() * nettRatio;

						this.processCorePFTxn(new HCI(0, scBonusIP.getScCode(), UtilDurations.getTodaysDateOnly(),
								EnumTxnType.BonusSell, remainder, cmp, 0));
					}

				}

			}

		}

	}

	@Override
	public IDS_PFTxn_UI getScripTxnDetails(String scCode) throws Exception
	{
		IDS_PFTxn_UI scDetails = null;

		scDetails = populateTxnDetails4Scrip(scCode);

		return scDetails;
	}

	/*
	 * --------------------------------------------------- ---------------------
	 * ------------------------------ PRIVATE SECTION -------------------------
	 * --------------------------------------------------- ------------------------
	 */

	private IDS_SCBuyProposal generateBuyProposal(PFSchema pfSchema, double buyAmnt, EnumSMABreach smaBreach)
			throws Exception
	{
		IDS_SCBuyProposal buyP = new IDS_SCBuyProposal();
		// Get Scrip CMP
		double cmp = Precision.round(
				StockPricesUtility.getQuoteforScrip(pfSchema.getSccode()).getQuote().getPrice().doubleValue(), 2);
		int units = (int) Precision.round((buyAmnt / cmp), 0);
		double currInv = Precision.round((units * cmp), 0);
		int totalUnits;
		double nppu, utilz, effect, depAvail = 0, totals, post, depAmnt;

		Optional<HC> currHoldingO = repoHC.findById(pfSchema.getSccode());
		if (currHoldingO.isPresent())
		{
			totalUnits = units + currHoldingO.get().getUnits();
			nppu = Precision
					.round((currInv + (currHoldingO.get().getPpu() * currHoldingO.get().getUnits())) / totalUnits, 2);
			utilz = Precision.round(((units * cmp) / pfSchema.getDepamnt()) * 100, 1);
			totals = pfSchema.getDepamnt() + currHoldingO.get().getUnits() * currHoldingO.get().getPpu();
			post = totals - (totalUnits * nppu);
			depAmnt = pfSchema.getDepamnt();
			depAvail = (post / totals) * 100;
			effect = UtilPercentages.getPercentageDelta(currHoldingO.get().getPpu(), nppu, 1);
		} else
		{
			totalUnits = units;
			nppu = cmp;
			utilz = Precision.round(((totalUnits * nppu) / pfSchema.getDepamnt()) * 100, 1);
			totals = pfSchema.getDepamnt();
			depAmnt = pfSchema.getDepamnt();
			post = totals - (units * cmp);
			depAvail = (post / totals) * 100;

			effect = 0;
		}

		buyP.setAmount(currInv);
		buyP.setAmountStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(currInv, 1));
		buyP.setEffectppu(effect);
		buyP.setNppu(nppu);
		buyP.setNumUnitsBuy(units);
		buyP.setPpuBuy(cmp);
		buyP.setScCode(pfSchema.getSccode());
		buyP.setSmaBreach(null);
		buyP.setDepAmnt(depAmnt);
		buyP.setDepAmntStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(depAmnt, 1));
		buyP.setSmaBreach(smaBreach);
		buyP.setTotalUnits(totalUnits);
		buyP.setUtilz(utilz);
		buyP.setPerDepAvail(Precision.round(depAvail, 1));
		buyP.setVp(repoPFVP.findById(pfSchema.getSccode()).get().getProfile());

		return buyP;
	}

	private void populateBuyProposalHeaderandSMA(IDS_BuyProposalBO buyP) throws Exception
	{
		List<IDS_SMAPreview> smaPWL = this.getPFSchemaSMAPreview();

		if (buyP.getBuyP().size() > 0)
		{
			if (smaPWL != null)
			{
				if (smaPWL.size() > 0)
				{

					for (IDS_SCBuyProposal proposal : buyP.getBuyP())
					{
						Optional<IDS_SMAPreview> smaPojoO = smaPWL.stream()
								.filter(s -> s.getScCode().equals(proposal.getScCode())).findFirst();

						if (smaPojoO.isPresent())
						{
							buyP.getSmaList().add(smaPojoO.get());
						}
					}
				}
			}

			if (repoPFSchema != null)
			{
				buyP.setBuyPHeader(new IDS_PF_BuyPHeader());
				buyP.getBuyPHeader().setDepAmnt(repoPFSchema.getSumDeploymentAmount());
				buyP.getBuyPHeader().setDepAmntStr(
						UtilDecimaltoMoneyString.getMoneyStringforDecimal(buyP.getBuyPHeader().getDepAmnt(), 2));

				double sumDayAmnt = Precision
						.round(buyP.getBuyP().stream().mapToDouble(IDS_SCBuyProposal::getAmount).sum(), 2);
				buyP.getBuyPHeader().setDayAmnt(sumDayAmnt);
				buyP.getBuyPHeader().setDayAmntStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(sumDayAmnt, 2));
				buyP.getBuyPHeader()
						.setUtlizDay(Precision.round((sumDayAmnt * 100) / buyP.getBuyPHeader().getDepAmnt(), 0));

				buyP.getBuyPHeader().setShortfall(
						Precision.round(buyP.getBuyPHeader().getDayAmnt() - buyP.getBuyPHeader().getDepAmnt(), 0));

			}
		}

	}

	private double getPFCurrVal() throws Exception
	{
		double value = 0;

		List<IDS_ScripUnits> scUnits = new ArrayList<IDS_ScripUnits>();
		List<HC> holdings = repoHC.findAll();
		if (holdings != null)
		{
			scUnits = holdings.stream().map(holding -> new IDS_ScripUnits(holding.getSccode(), holding.getUnits()))
					.collect(Collectors.toList());

			value = StockPricesUtility.getCurrentValueforScripsandUnits(scUnits);
		}

		return value;
	}

	private IDS_Scrip_Details populateDetails4Scrip(String scCode)
	{
		IDS_Scrip_Details scDetails = null;

		if (StringUtils.hasText(scCode))
		{
			if (repoHC != null)
			{
				Optional<HC> holdingO = repoHC.findById(scCode);
				if (holdingO.isPresent())
				{
					scDetails = new IDS_Scrip_Details();
					scDetails.getFormData().setScCode(scCode);
					scDetails.getFormData().setNumSharesTxn(holdingO.get().getUnits());
					scDetails.getFormData().setPpuTxn(holdingO.get().getPpu());
				}
			}
		}

		return scDetails;
	}

	private IDS_PFTxn_UI populateTxnDetails4Scrip(String scCode) throws Exception
	{
		IDS_PFTxn_UI scTxnDetails = null;

		if (StringUtils.hasText(scCode))
		{
			if (repoHC != null)
			{
				Optional<HC> holdingO = repoHC.findById(scCode);
				if (holdingO.isPresent())
				{
					scTxnDetails = new IDS_PFTxn_UI();
					scTxnDetails.setScCode(scCode);
					scTxnDetails.setNumSharesTxn(holdingO.get().getUnits());
					scTxnDetails.setPpuTxn(Precision
							.round(StockPricesUtility.getQuoteforScrip(scCode).getQuote().getPrice().doubleValue(), 2));
				}
			}
		}

		return scTxnDetails;
	}

}
