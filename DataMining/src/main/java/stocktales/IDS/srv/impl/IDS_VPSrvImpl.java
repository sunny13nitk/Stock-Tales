package stocktales.IDS.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.DataLake.srv.intf.DL_HistoricalPricesSrv;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.model.cfg.entity.IDS_CF_SMAWts;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPRange;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.pojo.UI.IDS_VP_Dates;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.historicalPrices.utility.StockPricesUtility;
import yahoofinance.histquotes.Interval;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_VPSrvImpl implements IDS_VPSrv
{
	@Autowired
	private IDS_ConfigLoaderSrv idsCfgSrv;

	@Autowired
	@Qualifier("DL_HistoricalPricesSrv_IDS")
	private DL_HistoricalPricesSrv hpDBSrv;

	@Autowired
	private SCPricesMode scPriceModeDB;

	private int[] smaIntervals = new int[]
	{ 264, 396, 528, 660 };

	@Override
	public IDS_VPDetails getVolatilityProfileDetailsforScrip(String scCode) throws Exception
	{
		IDS_VPDetails vpDetails = null;

		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				IDS_ScSMASpread scSMASpread = null;
				// 1. Get the SMA Details for the Scrip
				if (scPriceModeDB.getScpricesDBMode() == 1)
				{
					scSMASpread = hpDBSrv.getSMASpreadforScrip(scCode, smaIntervals);

				} else
				{
					scSMASpread = StockPricesUtility.getSMASpreadforScrip(scCode, smaIntervals, Calendar.YEAR, 1,
							Interval.DAILY);
				}

				if (scSMASpread != null)
				{
					vpDetails = this.getVPDetailsforSMASpread(scCode, scSMASpread);
				}

			}
		}

		return vpDetails;
	}

	@Override
	public IDS_VPDetails getVolatilityProfileDetails4Scrip4Durations(String scCode, Calendar from, Calendar to)
			throws Exception
	{
		IDS_VPDetails vpDetails = null;

		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				// 1. Get the SMA Details for the Scrip

				IDS_ScSMASpread scSMASpread = StockPricesUtility.getSMASpread4Scrip4Durations(scCode, smaIntervals,
						from, to, Interval.DAILY);

				if (scSMASpread != null)
				{
					vpDetails = this.getVPDetailsforSMASpread(scCode, scSMASpread);
				} else
				{
					vpDetails = new IDS_VPDetails(scCode, 0, 0, 0, 0, 0, EnumVolatilityProfile.Default, null);
				}

			}
		}

		return vpDetails;
	}

	@Override
	public IDS_VPDetails getVolatilityProfileDetails4Scrip4PriceHistory(String scCode,
			NFSStockHistoricalQuote scHistoricaPrices) throws Exception
	{
		IDS_VPDetails vpDetails = null;

		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				// 1. Get the SMA Details for the Scrip

				IDS_ScSMASpread scSMASpread = StockPricesUtility.getSMASpread4Scrip4HistoricaPriceData(scCode,
						smaIntervals, scHistoricaPrices);

				if (scSMASpread != null)
				{
					vpDetails = this.getVPDetailsforSMASpread(scCode, scSMASpread);
				} else
				{
					vpDetails = new IDS_VPDetails(scCode, 0, 0, 0, 0, 0, EnumVolatilityProfile.Default, null);
				}

			}
		}

		return vpDetails;
	}

	/**
	 * ---------PRIVATE SECTION ------------------------------------------------
	 */

	private IDS_VPDetails getVPDetailsforSMASpread(String scCode, IDS_ScSMASpread scSMASpread) throws Exception
	{
		List<IDS_VP_Dates> breachDates = new ArrayList<IDS_VP_Dates>();
		IDS_VPDetails vpDetails = null;
		if (scSMASpread != null)
		{
			vpDetails = new IDS_VPDetails();
			vpDetails.setSccode(scCode);

			if (scSMASpread.isNotEligibleSMA()) // Default Volatility Profile
			{
				vpDetails.setVolprofile(EnumVolatilityProfile.Default);
				return vpDetails;
			} else // Compute the Volatility Profile
			{

				if (scSMASpread.getPrSMAList().size() > 0)
				{
					List<IDS_SMASpread> sortedList = refurbishSMAbyDates(scSMASpread.getPrSMAList());
					sortedList.sort(Comparator.comparing(IDS_SMASpread::getDate));

					int loopPass = 0; // 0 based Index
					// 2.Loop through the SMA Spread
					for (IDS_SMASpread smaSpread : sortedList)
					{
						if (smaSpread.getClosePrice() < smaSpread.getSMAI1())
						{
							// if loop pass != 0 go to previous sma Entry
							if (loopPass > 0)
							{
								int prevPass = loopPass - 1;
								IDS_SMASpread smaSpreadPrev = sortedList.get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI1())
									{
										// Increment breach counter
										vpDetails.setSma1breaches(vpDetails.getSma1breaches() + 1);
										breachDates.add(new IDS_VP_Dates(EnumSMABreach.sma1, smaSpread.getDate(),
												Precision.round(smaSpread.getClosePrice(), 2)));
									}
								}
							} else // 1st Iteration
							{
								// Ignore as previous trend cannot be established - For logical Completeness
							}
						}

						if (smaSpread.getClosePrice() < smaSpread.getSMAI2())
						{
							// if loop pass != 0 go to previous sma Entry
							if (loopPass > 0)
							{
								int prevPass = loopPass - 1;
								IDS_SMASpread smaSpreadPrev = sortedList.get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI2())
									{
										// Increment breach counter
										vpDetails.setSma2breaches(vpDetails.getSma2breaches() + 1);
										breachDates.add(new IDS_VP_Dates(EnumSMABreach.sma2, smaSpread.getDate(),
												Precision.round(smaSpread.getClosePrice(), 2)));
									}
								}
							} else // 1st Iteration
							{
								// Ignore as previous trend cannot be established - For logical Completeness
							}
						}

						if (smaSpread.getClosePrice() < smaSpread.getSMAI3())
						{
							// if loop pass != 0 go to previous sma Entry
							if (loopPass > 0)
							{
								int prevPass = loopPass - 1;
								IDS_SMASpread smaSpreadPrev = sortedList.get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI3())
									{
										// Increment breach counter
										vpDetails.setSma3breaches(vpDetails.getSma3breaches() + 1);
										breachDates.add(new IDS_VP_Dates(EnumSMABreach.sma3, smaSpread.getDate(),
												Precision.round(smaSpread.getClosePrice(), 2)));
									}
								}
							} else // 1st Iteration
							{
								// Ignore as previous trend cannot be established - For logical Completeness
							}
						}

						if (smaSpread.getClosePrice() < smaSpread.getSMAI4())
						{
							// if loop pass != 0 go to previous sma Entry
							if (loopPass > 0)
							{
								int prevPass = loopPass - 1;
								IDS_SMASpread smaSpreadPrev = sortedList.get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI4())
									{
										// Increment breach counter
										vpDetails.setSma4breaches(vpDetails.getSma4breaches() + 1);
										breachDates.add(new IDS_VP_Dates(EnumSMABreach.sma4, smaSpread.getDate(),
												Precision.round(smaSpread.getClosePrice(), 2)));
									}
								}
							} else // 1st Iteration
							{
								// Ignore as previous trend cannot be established - For logical Completeness
							}
						}

						loopPass++;
					}

					// Compute the Scores
					if (idsCfgSrv != null)
					{
						if (idsCfgSrv.getSMAWts() != null)
						{
							if (idsCfgSrv.getSMAWts().size() > 0)
							{
								IDS_CF_SMAWts smaWtsConfig = idsCfgSrv.getSMAWts().get(0);
								if (smaWtsConfig != null)
								{
									double score = Precision
											.round(vpDetails.getSma1breaches() * smaWtsConfig.getWtsma1()
													+ vpDetails.getSma2breaches() * smaWtsConfig.getWtsma2()
													+ vpDetails.getSma3breaches() * smaWtsConfig.getWtsma3()
													+ vpDetails.getSma4breaches() * smaWtsConfig.getWtsma4(), 2);

									// Compute the Volatility Profile
									List<IDS_CF_VPRange> vpRangeList = idsCfgSrv.getVPRange();

									if (vpRangeList != null)
									{
										if (vpRangeList.size() > 0)
										{
											Optional<IDS_CF_VPRange> vpRangeO = vpRangeList.stream().filter(

													x ->
													{

														if (score >= x.getScoremin() && score <= x.getScoremax())
														{
															return true;
														}
														return false;

													}).findFirst();
											if (vpRangeO.isPresent())
											{
												vpDetails.setVolprofile(vpRangeO.get().getProfile());
											}
										}
									}

									vpDetails.setVolscore(Precision.round(score, 3));
									vpDetails.setBrechDetails(breachDates);
								}
							}
						}
					}
				}

			}
		}

		return vpDetails;
	}

	private List<IDS_SMASpread> refurbishSMAbyDates(List<IDS_SMASpread> prSMAList)
	{
		List<IDS_SMASpread> sortedList = new ArrayList<IDS_SMASpread>();

		int currLooopPass = 0;

		for (IDS_SMASpread ids_SMASpread : prSMAList)
		{
			IDS_SMASpread sortedSMAEnt = new IDS_SMASpread();
			sortedSMAEnt.setDate(ids_SMASpread.getDate());
			sortedSMAEnt.setClosePrice(ids_SMASpread.getClosePrice());

			for (int i = 0; i < smaIntervals.length; i++)
			{
				int idxFetch = currLooopPass + smaIntervals[i];
				if (idxFetch < prSMAList.size())
				{
					if (i == 0)
					{
						sortedSMAEnt.setSMAI1(prSMAList.get(idxFetch).getSMAI1());
					}

					if (i == 1)
					{
						sortedSMAEnt.setSMAI2(prSMAList.get(idxFetch).getSMAI2());
					}

					if (i == 2)
					{
						sortedSMAEnt.setSMAI3(prSMAList.get(idxFetch).getSMAI3());
					}

					if (i == 3)
					{
						sortedSMAEnt.setSMAI4(prSMAList.get(idxFetch).getSMAI4());
					}

				}
			}
			currLooopPass++;
			sortedList.add(sortedSMAEnt);

		}

		return sortedList;
	}

}
