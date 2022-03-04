package stocktales.IDS.srv.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.model.cfg.entity.IDS_CF_SMAWts;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPRange;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;
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

	private int[] smaIntervals = new int[]
	{ 18, 40, 70, 170 };

	@Override
	public IDS_VPDetails getVolatilityProfileDetailsforScrip(String scCode) throws Exception
	{
		IDS_VPDetails vpDetails = null;

		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				// 1. Get the SMA Details for the Scrip
				IDS_ScSMASpread scSMASpread = StockPricesUtility.getSMASpreadforScrip(scCode, smaIntervals,
						Calendar.YEAR, 1, Interval.DAILY);

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
					vpDetails = new IDS_VPDetails(scCode, 0, 0, 0, 0, 0, EnumVolatilityProfile.Default);
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
					vpDetails = new IDS_VPDetails(scCode, 0, 0, 0, 0, 0, EnumVolatilityProfile.Default);
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
					int loopPass = 0; // 0 based Index
					// 2.Loop through the SMA Spread
					for (IDS_SMASpread smaSpread : scSMASpread.getPrSMAList())
					{
						if (smaSpread.getClosePrice() < smaSpread.getSMAI1())
						{
							// if loop pass != 0 go to previous sma Entry
							if (loopPass > 0)
							{
								int prevPass = loopPass - 1;
								IDS_SMASpread smaSpreadPrev = scSMASpread.getPrSMAList().get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI1())
									{
										// Increment breach counter
										vpDetails.setSma1breaches(vpDetails.getSma1breaches() + 1);
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
								IDS_SMASpread smaSpreadPrev = scSMASpread.getPrSMAList().get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI2())
									{
										// Increment breach counter
										vpDetails.setSma2breaches(vpDetails.getSma2breaches() + 1);
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
								IDS_SMASpread smaSpreadPrev = scSMASpread.getPrSMAList().get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI3())
									{
										// Increment breach counter
										vpDetails.setSma3breaches(vpDetails.getSma3breaches() + 1);
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
								IDS_SMASpread smaSpreadPrev = scSMASpread.getPrSMAList().get(prevPass);
								if (smaSpreadPrev != null)
								{
									if (smaSpreadPrev.getClosePrice() > smaSpread.getSMAI4())
									{
										// Increment breach counter
										vpDetails.setSma4breaches(vpDetails.getSma4breaches() + 1);
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

								}
							}
						}
					}
				}

			}
		}

		return vpDetails;
	}

}
