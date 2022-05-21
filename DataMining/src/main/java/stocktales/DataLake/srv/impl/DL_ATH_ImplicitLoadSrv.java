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
public class DL_ATH_ImplicitLoadSrv implements stocktales.DataLake.srv.intf.DL_ATH_ImplicitLoadSrv
{

	@Autowired
	private RepoATHScripPrices repoSCPrices;

	@Autowired
	private RepoBseData repoBseData;

	private final int pastYrs = 6;

	private List<DL_ScripPriceATH> scPrices = new ArrayList<DL_ScripPriceATH>();

	private List<String> errScrips = new ArrayList<>();

	private List<String> resolvedScrips = new ArrayList<>();

	private final int batchSize = 5000;

	private Calendar from;

	private Calendar to;

	@Override
	public CompletableFuture<UploadStats> performInitialLoad()
	{
		UploadStats stats = new UploadStats();
		if (repoSCPrices != null && repoBseData != null)
		{
			if (repoSCPrices.count() > 0 && repoBseData.count() > 0)
			{
				repoSCPrices.deleteAll();
			}

			this.scPrices.clear();
			this.errScrips.clear();

			long numScrips = 0;

			List<String> scrips = repoBseData.findAllNseCodes();
			if (scrips != null)
			{
				if (scrips.size() > 0)
				{
					to = UtilDurations.getTodaysCalendarDateOnly();
					from = UtilDurations.getTodaysCalendarDateOnly();

					from.add(Calendar.YEAR, -1 * pastYrs);

					for (String scrip : scrips)
					{
						try
						{
							List<DL_ScripPriceATH> hQ = StockPricesUtility.getHistoricalClosePrices4Scrip(scrip, from,
									to, Interval.DAILY, true);

							if (hQ != null)
							{
								System.out.println("Scrip : " + scrip);
								numScrips += 1;
								this.scPrices.addAll(hQ);

							}
						} catch (Exception e)
						{
							// Append to Error Scrips
							errScrips.add(scrip);

						}
					}

					if (this.scPrices.size() > 0)
					{

						int iterations = scPrices.size() / batchSize;
						int startIdx = 0;

						for (int i = 0; i < iterations; i++)
						{
							List<DL_ScripPriceATH> batchScrips;
							try
							{
								batchScrips = scPrices.subList(startIdx, batchSize - 1);
								if (batchScrips != null)
								{
									upload2DBBatch(batchScrips);
								}
								startIdx += batchSize;
							} catch (IndexOutOfBoundsException e)
							{
								// Do Nothing
							}

						}

						stats.setNumEntries((long) scPrices.size());
						stats.setNumScrips(numScrips);
						stats.setNumErrors((long) errScrips.size());
					}
				}
			}

		}
		return CompletableFuture.completedFuture(stats);

	}

	@Override
	public List<String> getErroredScrips()
	{

		return this.errScrips;
	}

	@Override
	public CompletableFuture<UploadStats> performDeltaLoad()
	{
		UploadStats stats = new UploadStats();
		List<String> scrips = repoBseData.findAllNseCodes();
		if (scrips != null)
		{
			this.scPrices.clear();
			if (scrips.size() > 0)
			{
				Calendar to = UtilDurations.getTodaysCalendarDateOnly();
				Calendar from = UtilDurations.getTodaysCalendarDateOnly();

				from.add(Calendar.YEAR, -1);
				long numScrips = 0;

				for (String scrip : scrips)
				{
					// Get SCrip Data from Repo
					try
					{
						Date latestEntryOn = repoSCPrices.getLatestEntryDate4Scrip(scrip);
						if (latestEntryOn != null)
						{
							from.setTime(latestEntryOn);
							from.add(Calendar.DAY_OF_MONTH, 1); // 1 day ahead of last saved price

							if (from.before(to)) // Not Updated with Latest
							{
								List<DL_ScripPriceATH> hQ = StockPricesUtility.getHistoricalClosePrices4Scrip(scrip,
										from, to, Interval.DAILY, true);
								if (hQ != null)
								{
									numScrips += 1;
									this.scPrices.addAll(hQ);

								}
							}

						}

						// Once Done for All Scrips - Save to DB
						if (this.scPrices.size() > 0)
						{

							stats.setNumEntries((long) repoSCPrices.saveAll(scPrices).size());
							stats.setNumScrips(numScrips);

						}
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return CompletableFuture.completedFuture(stats);

	}

	private boolean process4ErrorScrip(String errScrip)
	{

		boolean found = false;
		List<DL_ScripPriceATH> hQ;
		try
		{
			hQ = StockPricesUtility.getHistoricalClosePrices4Scrip(errScrip, from, to, Interval.DAILY, true);
			if (hQ != null)
			{
				found = true;
				this.scPrices.addAll(hQ);
				this.resolvedScrips.add(errScrip);

			}
		} catch (Exception e)
		{
			// Do Nothing let it remain in ErrScrips
		}

		return found;

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
