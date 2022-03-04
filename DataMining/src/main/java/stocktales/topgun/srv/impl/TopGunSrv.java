package stocktales.topgun.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.pojo.StockHistoryCmpNFS;
import stocktales.historicalPrices.pojo.HistoricalQuote;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.topgun.model.entity.DataPool;
import stocktales.topgun.model.entity.TGJournal;
import stocktales.topgun.model.entity.TopGun;
import stocktales.topgun.model.pojo.IntervalReturns;
import stocktales.topgun.model.pojo.IntervalStats;
import stocktales.topgun.model.pojo.IntvScripRetRankConsol;
import stocktales.topgun.model.pojo.ScripIntvReturns;
import stocktales.topgun.model.pojo.ScripRetRank;
import stocktales.topgun.model.pojo.TopGunContainer;
import stocktales.topgun.repo.RepoDataPool;
import stocktales.topgun.repo.RepoTopGun;
import stocktales.topgun.repo.RepoTopGunJournal;
import stocktales.topgun.srv.intf.ITopGunSrv;
import yahoofinance.histquotes.Interval;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopGunSrv implements ITopGunSrv
{
	@Autowired
	private RepoTopGun repoTopGun;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private RepoDataPool repoDataPool;

	@Autowired
	private RepoTopGunJournal repoTopGunJ;

	@Autowired
	private NFSConfig nfsConfig;

	private TopGunContainer tgContainer;

	private double eqWtRet;

	@Override
	public TopGunContainer dryRun(int numMonthsinPast, int numWeeksinInterval) throws Exception
	{

		if (numMonthsinPast > 0 && numWeeksinInterval > 0)
		{
			// 1. Prepare the Data Pool
			prepareDataPool(numMonthsinPast);

			// 2. Prepare Scrip interval Returns
			prepareScripIntervalReturns(numWeeksinInterval);

			// 3. Consolidate by Intervals and Rank - For Each Interval for Every Scrip
			consolidateByIntervals();

			// 4. Assign Ranks and Compute Equi-weighted returns for each Interval
			assignRanksByIntervals();

			// 5. Do the Churn Babie
			dotheChurnBabie();
		}
		// TODO Auto-generated method stub
		return tgContainer;
	}

	public void createRebalance(int numWeeksinInterval) throws Exception
	{
		prepareDataPool(1);
		List<ScripRetRank> scRetRanks = getReturnsforLastNWeeksandRank(numWeeksinInterval);
		if (scRetRanks != null)
		{
			// Get Today's Date
			long millis = System.currentTimeMillis();
			java.util.Date today = new java.util.Date(millis);
			if (scRetRanks.size() > 0 && repoTopGun != null)
			{
				if (repoTopGun.count() == 0)
				{
					// Create a new Top Gun PF
					List<ScripRetRank> topNScrips = scRetRanks.stream().limit(nfsConfig.getTopGunPfSize())
							.collect(Collectors.toList());
					runChurn(topNScrips, null, today, eqWtRet, true);
				} else
				{
					// run Churn with Churning Happening
					List<ScripRetRank> topNScrips = scRetRanks.stream().limit(nfsConfig.getTopGunPfSize())
							.collect(Collectors.toList());

					List<String> prevScrips = new ArrayList<String>();
					List<TopGun> currTGPF = repoTopGun.findAll();
					if (!currTGPF.isEmpty())
					{
						for (TopGun topGun : currTGPF)
						{
							prevScrips.add(topGun.getNsecode());
						}

						if (prevScrips.size() > 0)
						{
							runChurn(topNScrips, prevScrips, today, eqWtRet, true);
						}
					}

				}

			}
		}
	}

	@Override
	public void refreshListFromCorePF() throws Exception
	{
		// TODO Auto-generated method stub

		if (repoPFSchema != null)
		{
			List<PFSchema> corePF = repoPFSchema.findAll();
			if (!corePF.isEmpty())
			{
				List<DataPool> dp = new ArrayList<DataPool>();
				for (PFSchema corePFH : corePF)
				{
					dp.add(new DataPool(corePFH.getSccode(), 0));
				}

				repoDataPool.deleteAll();

				repoDataPool.saveAll(dp);
			}
		}

	}

	/*
	 * 1 - Get Historical Data for Data Pool Scrips
	 */
	private void prepareDataPool(int numMonthsinPast) throws Exception
	{
		if (repoDataPool != null)
		{
			List<String> scCodes = repoDataPool.findAllNseCodes();
			if (scCodes.size() > 0)
			{
				this.tgContainer = new TopGunContainer();
				for (String scCode : scCodes)
				{
					tgContainer.getDataPool().add(StockPricesUtility.getHistoricalPricesforScrip(scCode, Calendar.MONTH,
							numMonthsinPast, Interval.DAILY));

				}
			}
		}
	}

	private List<ScripRetRank> getReturnsforLastNWeeksandRank(int numWeeksinInterval)
	{
		List<ScripRetRank> scRetRanks = new ArrayList<ScripRetRank>();

		for (StockHistoryCmpNFS scripData : tgContainer.getDataPool())
		{
			if (scripData.getPriceHistory().size() > numWeeksinInterval * 5)
			{
				ScripRetRank scRetRank = new ScripRetRank();
				List<HistoricalQuote> hQuotesSorted = scripData.getPriceHistory().stream()
						.sorted(Comparator.comparing(HistoricalQuote::getDateVal).reversed())
						.collect(Collectors.toList());
				List<HistoricalQuote> topNDaysHQ = hQuotesSorted.stream().limit(numWeeksinInterval * 5)
						.collect(Collectors.toList());

				if (topNDaysHQ.size() > 0)
				{

					scRetRank.setScCode(scripData.getScCode());

					double priceBegin = topNDaysHQ.get(topNDaysHQ.size() - 1).getClosePrice(); // 0 based index
					double priceEnd = topNDaysHQ.get(0).getClosePrice(); // Sorted Descending Top is Latest

					scRetRank.setReturns(UtilPercentages.getPercentageDelta(priceBegin, priceEnd, 1));

				}

				scRetRanks.add(scRetRank);
			}
		}

		if (scRetRanks.size() > 1)
		{
			// Equi - weighted Returns for Journal

			// this.eqWtRet = Precision.round(getPL(scRetRanks), 1);

			List<ScripRetRank> scRankSorted = scRetRanks.stream()
					.sorted(Comparator.comparing(ScripRetRank::getReturns).reversed()).collect(Collectors.toList());

			scRetRanks.clear();

			int i = 1;
			for (ScripRetRank scripRetRank : scRankSorted)
			{
				scripRetRank.setRank(i);
				scRetRanks.add(scripRetRank);
				i++;
			}

		}
		return scRetRanks;
	}

	// 2. Prepare Scrip interval Returns
	private void prepareScripIntervalReturns(int numWeeksinInterval)
	{

		int intvsize = numWeeksinInterval * 5; // average of 5 trading sessions in a week

		for (StockHistoryCmpNFS stHist : tgContainer.getDataPool())
		{
			int currIntv = 0;
			int numIntv = 0;
			int currRow = 0;
			ScripIntvReturns scIntRet = new ScripIntvReturns();
			if (stHist.getPriceHistory().size() > 0)
			{
				numIntv = stHist.getPriceHistory().size() / intvsize;
				while (currIntv < numIntv)
				{
					scIntRet.setSccode(stHist.getScCode());
					HistoricalQuote beginintvQuote = stHist.getPriceHistory().get(currRow);
					HistoricalQuote endintvQuote = stHist.getPriceHistory().get(currRow + intvsize - 1); // 0 based
																											// index

					if (beginintvQuote != null && endintvQuote != null)
					{
						scIntRet.getIntvReturns()
								.add(new IntervalReturns(currIntv + 1, endintvQuote.getDateVal(),
										beginintvQuote.getClosePrice(), endintvQuote.getClosePrice(),
										UtilPercentages.getPercentageDelta(beginintvQuote.getClosePrice(),
												endintvQuote.getClosePrice(), 1)));

						if (tgContainer.getIntervalsEndDates().size() == 0)
						{
							tgContainer.getIntervalsEndDates().add(endintvQuote.getDateVal());

						} else
						{
							// check if the same interval Already exists
							Optional<Date> dateexisO = tgContainer.getIntervalsEndDates().stream()
									.filter(e -> e.equals(endintvQuote.getDateVal())).findAny();

							if (!dateexisO.isPresent())
							{
								// If Not add to Intervals
								tgContainer.getIntervalsEndDates().add(endintvQuote.getDateVal());
							}
						}

					}

					currIntv++;

					currRow = currRow + intvsize;
				}
				tgContainer.getIntvReturnsPool().add(scIntRet);

			}
		}

	}

	// 3. Consolidate by Intervals and Rank - For Each Interval for Every Scrip
	private void consolidateByIntervals()
	{
		if (this.tgContainer.getIntvReturnsPool().size() > 0 && tgContainer.getIntervalsEndDates().size() > 0)
		{
			for (Date endDate : tgContainer.getIntervalsEndDates())
			{
				IntvScripRetRankConsol intvScripRetConsol = new IntvScripRetRankConsol();
				intvScripRetConsol.setEndDate(endDate);
				for (ScripIntvReturns scIntvRet : tgContainer.getIntvReturnsPool())
				{
					Optional<IntervalReturns> intvRet = scIntvRet.getIntvReturns().stream()
							.filter(x -> x.getEndDate().equals(endDate)).findFirst();
					if (intvRet.isPresent())
					{
						ScripRetRank scretRank = new ScripRetRank();
						scretRank.setScCode(scIntvRet.getSccode());
						scretRank.setReturns(intvRet.get().getReturns());
						intvScripRetConsol.getScripsRank().add(scretRank);
					}
				}
				tgContainer.getIntvReturnConsol().add(intvScripRetConsol);
			}
		}

	}

	// 4. Assign Ranks and Compute Equi-weighted returns for each Interval
	private void assignRanksByIntervals()
	{
		if (tgContainer.getIntvReturnConsol().size() > 0)
		{
			// For Each Interval - Do the Scrips Ranking by Returns and Compute
			// Equi-weighted Returns
			for (IntvScripRetRankConsol intvContainer : tgContainer.getIntvReturnConsol())
			{
				List<ScripRetRank> intvScripsRank = intvContainer.getScripsRank().stream()
						.sorted(Comparator.comparingDouble(ScripRetRank::getReturns).reversed())
						.collect(Collectors.toList());

				// Clear Existing List- We have a copy already sorted Now - We'll work with that
				intvContainer.getScripsRank().clear();

				double eqWtRet = 0;
				int i = 1;
				// this is the Sorted List - WE can assign the ranks in this list now
				for (ScripRetRank scripRetRank : intvScripsRank)
				{
					eqWtRet += scripRetRank.getReturns();
					scripRetRank.setRank(i);
					i++;
					intvContainer.getScripsRank().add(scripRetRank); // Add to Collection With Rank for The Interval
				}

				intvContainer.setEqWtRet(Precision.round(eqWtRet / tgContainer.getDataPool().size(), 1)); // Save
																											// Equi-weighted
																											// Returns
																											// for
																											// Intervals
			}
		}

	}

	// 5. DO portfolio Churn Simulation
	private void dotheChurnBabie() throws Exception
	{
		for (int i = 0; i < tgContainer.getIntvReturnConsol().size(); i++)
		{
			if (i == 0)
			{
				tgContainer.getIntvStats().add(runChurn(tgContainer.getIntvReturnConsol().get(i).getScripsRank(), null,
						tgContainer.getIntvReturnConsol().get(i).getEndDate(), 0, false));
			} else
			{
				List<String> prevTopN = tgContainer.getIntvStats().get(i - 1).getScripsPresent();
				if (prevTopN != null)
				{
					tgContainer.getIntvStats()
							.add(runChurn(tgContainer.getIntvReturnConsol().get(i).getScripsRank(), prevTopN,
									tgContainer.getIntvReturnConsol().get(i).getEndDate(),
									tgContainer.getIntvReturnConsol().get(i).getEqWtRet(), false));
				}
			}
		}

	}

	// 5. Top Gun Portfolio Churn
	private IntervalStats runChurn(List<ScripRetRank> currTopNAll, List<String> prevHoldings, Date endDate,
			double eqWtReturns, boolean updateDB) throws Exception
	{
		IntervalStats tgJournal = new IntervalStats();
		double realzRet = 0;
		double unrealzRet = 0;

		// - Scanning for 1st Entry

		if (prevHoldings == null)
		{
			if (updateDB == true)
			{
				// Update in Repo Top Gun if Update Db is on
				for (ScripRetRank scRetRank : currTopNAll)
				{
					// Get the Cmp and Check for Delta
					double cmp = StockPricesUtility.getQuoteforScrip(scRetRank.getScCode()).getQuote().getPrice()
							.doubleValue();
					TopGun tgEnt = new TopGun(scRetRank.getScCode(), scRetRank.getRank(), scRetRank.getRank(), cmp);
					repoTopGun.save(tgEnt);
				}
				// also Create a Journal Entity
				TGJournal tgJournalPF = new TGJournal();
				tgJournalPF.setAllequwret(eqWtRet);
				tgJournalPF.setDate(endDate);
				tgJournalPF.setScripspresent(getScripsConcat(currTopNAll, nfsConfig.getTopGunPfSize()));
				repoTopGunJ.save(tgJournalPF);

			} else
			{
				tgJournal.setEndDate(endDate);
				tgJournal.setAllEquwRet(eqWtReturns);

				tgJournal.setScripsPresent(getScripsList(currTopNAll, nfsConfig.getTopGunPfSize()));
			}

			return tgJournal;
		}

		else // Churn Will Happen Now - Probably
		{
			tgJournal.setEndDate(endDate);
			tgJournal.setAllEquwRet(eqWtReturns);

			List<String> prevTopNCp = new ArrayList<String>();
			prevTopNCp.addAll(prevHoldings);
			List<ScripRetRank> CurrTopNMax = currTopNAll.stream()
					.filter(u -> u.getRank() <= (nfsConfig.getTopGunPfSlotMax())).collect(Collectors.toList());
			if (CurrTopNMax.size() > 0)
			{
				// For Each Previous Holding
				for (String prevHolding : prevHoldings)
				{
					// Check & find in TOpN Max Current
					Optional<ScripRetRank> foundInCurr = CurrTopNMax.stream()
							.filter(w -> w.getScCode().equals(prevHolding)).findFirst();
					if (foundInCurr.isPresent())
					{
						// Previous Holding Found in Current Upto TopN Max Limit
						// Need Not be replaced - Append in Scrips Present
						tgJournal.getScripsPresent().add(prevHolding);
						unrealzRet += foundInCurr.get().getReturns(); // Add Current REturns to Unrealized
					} else // Previous Holding not in Current ToP 'N' - Max
					{
						// Check if in Loss
						/**
						 * For Real Time - Will be checked from Repo w.r.t CMP
						 */
						Optional<TopGun> dbPfHolding = repoTopGun.findByNsecode(prevHolding);
						if (dbPfHolding.isPresent())
						{
							// Get the Cmp and Check for Delta
							double cmpPrevHolding = StockPricesUtility.getQuoteforScrip(prevHolding).getQuote()
									.getPrice().doubleValue();
							if (cmpPrevHolding > dbPfHolding.get().getPriceincl())
							{
								// This Guy needs to be replaced
								ScripRetRank repl = seekReplacement(CurrTopNMax, prevTopNCp);
								if (repl != null)
								{
									tgJournal.setNumExits(tgJournal.getNumExits() + 1);
									tgJournal.getScripsExited().add(prevHolding);
									tgJournal.getScripsPresent().add(repl.getScCode());
									realzRet += (UtilPercentages.getPercentageDelta(dbPfHolding.get().getPriceincl(),
											cmpPrevHolding, 1));

									// Update DB Too
									repoTopGun.delete(dbPfHolding.get());
									repoTopGun.save(new TopGun(repl.getScCode(), repl.getRank(), repl.getRank(),
											Precision.round(StockPricesUtility.getQuoteforScrip(repl.getScCode())
													.getQuote().getPrice().doubleValue(), 1)));

									/*
									 * For Next Loop Pass -so the Same Scrip is Not brought in as replacement Twice
									 * in PF
									 */
									prevTopNCp.removeIf(x -> x.equals(prevHolding));
									prevTopNCp.add(repl.getScCode());
								}
							} else
							{
								// HQ Holdings not to be sold in Loss
								// Need Not be replaced - Append in Scrips Present
								tgJournal.getScripsPresent().add(prevHolding);
								unrealzRet += foundInCurr.get().getReturns(); // Add Current REturns to Unrealized
							}
						} else
						{

							/**
							 * For Simulation - Will be checked from Current Run REturns if they are +ve We
							 * will trigger Replace
							 */
							// Check & find in TOpN Max Current
							Optional<ScripRetRank> foundInCurrAll = currTopNAll.stream()
									.filter(w -> w.getScCode().equals(prevHolding)).findFirst();
							if (foundInCurrAll.isPresent())
							{
								if (foundInCurrAll.get().getReturns() > 0)
								{

									// This Guy needs to be replaced
									ScripRetRank repl = seekReplacement(CurrTopNMax, prevTopNCp);
									if (repl != null)
									{
										tgJournal.setNumExits(tgJournal.getNumExits() + 1);
										tgJournal.getScripsExited().add(prevHolding);
										tgJournal.getScripsPresent().add(repl.getScCode());
										realzRet += Precision.round(foundInCurrAll.get().getReturns(), 1);

										/*
										 * For Next Loop Pass -so the Same Scrip is Not brought in as replacement Twice
										 * in PF
										 */
										prevTopNCp.removeIf(x -> x.equals(prevHolding));
										prevTopNCp.add(repl.getScCode());

									}
								} else
								{
									// HQ Holdings not to be sold in Loss
									// Need Not be replaced - Append in Scrips Present
									tgJournal.getScripsPresent().add(prevHolding);
									unrealzRet += foundInCurrAll.get().getReturns(); // Add Current REturns to
																						// Unrealized
								}
							}

						}
					}
				}

			}

			/*
			 * Prepare TG Journal Returns- in case of Churn
			 */
			tgJournal.setTopNRealized(
					Precision.round(((realzRet * tgJournal.getNumExits()) / nfsConfig.getTopGunPfSize()), 1));
			tgJournal.setTopNUnrealized(
					Precision.round((unrealzRet / (nfsConfig.getTopGunPfSize() - tgJournal.getNumExits())), 1));
			tgJournal.setPerChurn(Precision.round((tgJournal.getNumExits() * 100 / nfsConfig.getTopGunPfSize()), 1));
		}

		return tgJournal;
	}

	private ScripRetRank seekReplacement(List<ScripRetRank> currTopNAll, List<String> prevTopN)
	{
		ScripRetRank replacement = null;

		for (ScripRetRank scripRetRank : currTopNAll)
		{
			Optional<String> inPrevO = prevTopN.stream().filter(x -> x.equals(scripRetRank.getScCode())).findFirst();
			if (!inPrevO.isPresent())
			{
				return scripRetRank; // REturn and Exit the Loop Completely
			}
		}

		return replacement;

	}

	private String getScripsConcat(List<ScripRetRank> scrips, int uptoRank)
	{
		String scripsConcat = null;
		if (scrips != null && scrips.size() > 0)
		{
			scripsConcat = new String();

			for (int i = 0; i < uptoRank; i++)
			{
				scripsConcat += scrips.get(i).getScCode() + " | ";
			}
		}

		return scripsConcat;
	}

	private List<String> getScripsList(List<ScripRetRank> scrips, int uptoRank)
	{
		List<String> scripsList = null;
		if (scrips != null && scrips.size() > 0 && (scrips.size() - 1) >= uptoRank)
		{
			scripsList = new ArrayList<String>();

			for (int i = 0; i < uptoRank; i++)
			{
				scripsList.add(scrips.get(i).getScCode());
			}
		}

		return scripsList;
	}

	private double getPL(List<ScripRetRank> tgPF)
	{
		double ret = 0;

		if (tgPF.size() > 0)
		{
			ret = (tgPF.stream().mapToDouble(ScripRetRank::getReturns).sum()) / tgPF.size();
		}

		return ret;
	}

}
