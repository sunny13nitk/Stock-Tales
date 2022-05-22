package stocktales.DataLake.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.DataLake.model.pojo.UploadStats;
import stocktales.DataLake.model.repo.RepoATHScripPrices;
import stocktales.NFS.repo.RepoBseData;
import stocktales.durations.UtilDurations;
import stocktales.historicalPrices.utility.StockPricesUtility;
import yahoofinance.histquotes.Interval;

@Service
@Getter
public class DL_ATH_DataRefreshSrv implements stocktales.DataLake.srv.intf.DL_ATH_DataRefreshSrv
{

	@Autowired
	private RepoATHScripPrices repoSCPrices;

	@Autowired
	private RepoBseData repoBseData;

	private final int pastYrs = 6;

	private final int pastMonths = 2;

	private List<DL_ScripPriceATH> scPrices = new ArrayList<DL_ScripPriceATH>();

	private List<String> errScrips = new ArrayList<>();

	private List<String> resolvedScrips = new ArrayList<>();

	private final int batchSize = 5000;

	private Calendar from;

	private Calendar to;

	@Override
	public CompletableFuture<UploadStats> refreshDataLake()
	{
		UploadStats stats = new UploadStats();
		this.scPrices.clear();
		this.errScrips.clear();
		long numScrips = 0;
		long numError = 0;

		boolean isDatainATHDL = false;

		List<String> scrips = repoBseData.findAllNseCodes();
		if (scrips != null)
		{
			if (scrips.size() > 0)
			{
				to = UtilDurations.getTodaysCalendarDateOnly();
				from = UtilDurations.getTodaysCalendarDateOnly();

				from.add(Calendar.YEAR, -1 * pastYrs);
			}

			if (repoSCPrices.count() > 0)
			{
				isDatainATHDL = true;
			}

			for (String scrip : scrips)
			{
				boolean isExisting = false;
				if (isDatainATHDL)
				{
					// Check For Scrip in ATH DL
					try
					{
						Date scDateLastEntry = repoSCPrices.getLatestEntryDate4Scrip(scrip);
						if (scDateLastEntry != null)
						{
							isExisting = true;
							// Process Update Inserts from Last Entry Date to Current

							if (scDateLastEntry.before(UtilDurations.getTodaysDate()))
							{
								try
								{
									Calendar lastUpdate = UtilDurations.getTodaysCalendarDateOnly();
									lastUpdate.setTime(scDateLastEntry);

									List<DL_ScripPriceATH> hQ = StockPricesUtility.getHistoricalClosePrices4Scrip(scrip,
											lastUpdate, to, Interval.DAILY, true);

									if (hQ != null)
									{
										numScrips += 1;
										System.out.println("Count: " + numScrips + "-- Scrip : " + scrip);
										this.scPrices.addAll(hQ);

									}
								} catch (Exception e)
								{
									// Append to Error Scrips
									numError += 1;
									errScrips.add(scrip);
									System.out.println("Error Count: " + numError + "-- Error Scrip : " + scrip);

								}
							}

						}
					} catch (Exception e)
					{
						// Not Found in ATH DL - DO Nothing
					}
				}

				// Scrip Not Found in DL
				if (!isExisting)
				{
					try
					{
						List<DL_ScripPriceATH> hQ = StockPricesUtility.getHistoricalClosePrices4Scrip(scrip, from, to,
								Interval.DAILY, true);

						if (hQ != null)
						{
							numScrips += 1;
							System.out.println("Count: " + numScrips + "-- Scrip : " + scrip);
							this.scPrices.addAll(hQ);

						}
					} catch (Exception e)
					{
						// Append to Error Scrips
						numError += 1;
						errScrips.add(scrip);
						System.out.println("Error Count: " + numError + "-- Error Scrip : " + scrip);

					}
				}

			}

			System.out.println("Records to Insert: " + scPrices.size());

			/**
			 * Process Updates
			 */
			if (this.scPrices.size() > 0)
			{

				int iterations = scPrices.size() / batchSize;
				int startIdx = 0;

				for (int i = 1; i <= iterations; i++)
				{
					List<DL_ScripPriceATH> batchScrips = null;
					if (i < iterations) // last but one
					{
						try
						{
							batchScrips = scPrices.subList(startIdx, (startIdx + batchSize));
							startIdx += batchSize;
						} catch (IndexOutOfBoundsException e)
						{
							batchScrips = scPrices.subList(startIdx, scPrices.size() - 1);
						}

					} else
					{
						batchScrips = scPrices.subList(startIdx, scPrices.size() - 1);
					}

					if (batchScrips != null)
					{
						upload2DBBatch(batchScrips);
					}

				}

				stats.setNumEntries((long) scPrices.size());
				stats.setNumScrips(numScrips);
				stats.setNumErrors((long) errScrips.size());
			}

		}

		return CompletableFuture.completedFuture(stats);

	}

	@Transactional
	private void upload2DBBatch(List<DL_ScripPriceATH> scrips2Save)
	{
		if (scrips2Save != null)
		{
			if (scrips2Save.size() > 0)
			{
				repoSCPrices.saveAll(scrips2Save);
			}
		}
	}

}
