package stocktales.DataLake.srv.impl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import stocktales.DataLake.model.entity.DL_ScripPrice;
import stocktales.DataLake.model.repo.RepoScripPrices;
import stocktales.DataLake.model.repo.intf.IDL_IDSStats;
import stocktales.DataLake.model.repo.intf.IScMaxDate;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.durations.UtilDurations;
import stocktales.historicalPrices.pojo.HistoricalQuote;
import stocktales.historicalPrices.pojo.StockHistory;
import stocktales.historicalPrices.utility.StockPricesUtility;
import yahoofinance.Stock;

/**
 * 
 * IDS specific Session Scoped Implementation of DL_HistoricalPricesSrv
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DL_HistoricalPricesSrv_IDS implements stocktales.DataLake.srv.intf.DL_HistoricalPricesSrv
{

	@Autowired
	private SCPricesMode scPricesMode;

	@Autowired
	private RepoPFSchema repoPfSchema;

	@Autowired
	private RepoScripPrices repoSCPrices;

	private final int defaultPastDurationAmount = 1;
	private final int defaultPastDurationUnit = Calendar.YEAR;

	private List<DL_ScripPrice> pricesHistoryContainer = new ArrayList<DL_ScripPrice>();

	@Override
	public List<DL_ScripPrice> getHistoricalPricesByScripBetweenDates(String scCode, Date from, Date to)
	{
		List<DL_ScripPrice> result = null;

		if (StringUtils.hasText(scCode) && from != null && to != null && this.pricesHistoryContainer.size() > 0)
		{
			result = this.pricesHistoryContainer.stream().filter(

					x ->
					{
						if (x.getSccode().equals(scCode) && (x.getDate().after(from) && x.getDate().before(to))

					)
						{
							return true;
						}
						return false;
					}).collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public List<DL_ScripPrice> getHistoricalPricesByScripPast1Yr(String scCode)
	{
		List<DL_ScripPrice> result = null;

		if (StringUtils.hasText(scCode) && this.pricesHistoryContainer.size() > 0)
		{

			result = this.pricesHistoryContainer.stream().filter(x -> x.getSccode().equals(scCode))
					.collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public List<DL_ScripPrice> getPricesHistoryContainer()
	{
		return this.pricesHistoryContainer;
	}

	@Override
	public List<StockHistory> getStocksHistory4mContainer(Date from, Date to)
	{
		List<StockHistory> stocksHistory = null;

		if (this.pricesHistoryContainer != null)
		{
			List<String> scrips = repoPfSchema.getPFScripCodes();
			if (scrips != null)
			{
				if (scrips.size() > 0)
				{
					stocksHistory = new ArrayList<StockHistory>();
					SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
					for (String scrip : scrips)
					{
						StockHistory sHist = new StockHistory();
						sHist.setScCode(scrip);

						List<DL_ScripPrice> scPrices = this.getHistoricalPricesByScripBetweenDates(scrip, from, to);
						if (scPrices != null)
						{
							if (scPrices.size() > 0)
							{
								for (DL_ScripPrice scPrice : scPrices)
								{
									HistoricalQuote hq = new HistoricalQuote(format1.format(scPrice.getDate()),
											scPrice.getDate(), scPrice.getCloseprice());
									sHist.getPriceHistory().add(hq);

								}
							}
						}
						stocksHistory.add(sHist);
					}

				}
			}
		}

		return stocksHistory;
	}

	public List<StockHistory> getStocksHistory4mRepo(Date from, Date to)
	{
		List<StockHistory> stocksHistory = null;

		if (this.pricesHistoryContainer != null)
		{
			List<String> scrips = repoPfSchema.getPFScripCodes();
			if (scrips != null)
			{
				if (scrips.size() > 0)
				{
					stocksHistory = new ArrayList<StockHistory>();
					SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
					for (String scrip : scrips)
					{
						StockHistory sHist = new StockHistory();
						sHist.setScCode(scrip);

						List<DL_ScripPrice> scPrices = repoSCPrices.findAllBySccodeAndDateBetweenOrderByDateDesc(scrip,
								from, to);
						if (scPrices != null)
						{
							if (scPrices.size() > 0)
							{
								for (DL_ScripPrice scPrice : scPrices)
								{
									HistoricalQuote hq = new HistoricalQuote(format1.format(scPrice.getDate()),
											scPrice.getDate(), scPrice.getCloseprice());
									sHist.getPriceHistory().add(hq);

								}
							}
						}
						stocksHistory.add(sHist);
					}

				}
			}
		}

		return stocksHistory;
	}

	@Override
	public IDS_ScSMASpread getSMASpreadforScrip(String scrip, int[] smaIntervals)
	{
		IDS_ScSMASpread scSMASpread = null;
		if (StringUtils.hasText(scrip))
		{

			List<Double> sma1 = new ArrayList<Double>();
			List<Double> sma2 = new ArrayList<Double>();
			List<Double> sma3 = new ArrayList<Double>();
			List<Double> sma4 = new ArrayList<Double>();

			if (scrip != null && smaIntervals.length > 0)
			{
				// 1. Get the Prices Simply for the said Duration, amount frequency
				List<DL_ScripPrice> scPrices = this.getHistoricalPricesByScripPast1Yr(scrip);
				if (scPrices != null)
				{

					if (scPrices.size() > 0)
					{
						if (scPrices.size() > 0)
						{

							scSMASpread = new IDS_ScSMASpread();
							scSMASpread.setScCode(scrip);

							// 2. Determine eligibility for SMA Computations

							if (scPrices.size() < smaIntervals[smaIntervals.length - 1])
							{
								scSMASpread.setNotEligibleSMA(true);
							}

							if (scSMASpread.isNotEligibleSMA() != true)
							{
								// Can Proceed with SMA computations

								for (int i = 0; i < smaIntervals.length; i++)
								{
									int valdaysIntv = smaIntervals[i]; // e.g 18 days
									int loopupto = 0;
									int loopbegin = 0;
									boolean loopend = false;

									for (int daystart = loopbegin; daystart < valdaysIntv; daystart++)
									{
										// Create Sublist of Historical Prices from 0-17
										if (scPrices.size() > (valdaysIntv - 1))
										{
											loopupto = valdaysIntv;

										} else
										{
											loopupto = scPrices.size();
											loopend = true;
										}
										List<DL_ScripPrice> subList = scPrices.subList(daystart, loopupto);

										// Get Avg SMa Price for the sublist
										double SMA = Precision.round(subList.stream().map(DL_ScripPrice::getCloseprice)
												.reduce(0.0, Double::sum), 2);
										SMA = SMA / subList.size();

										if (i == 0)
										{
											sma1.add(SMA);
										}
										if (i == 1)
										{
											sma2.add(SMA);
										}
										if (i == 2)
										{
											sma3.add(SMA);
										}
										if (i == 3)
										{
											sma4.add(SMA);
										}

										if (!loopend)
										{
											valdaysIntv++;
											loopbegin++;
										} else
										{
											// Terminate Loop for The Current SMA Interval
											break;
										}
									}

								}

								int loopPass = 1;
								int sma1pass = 0;
								int sma2pass = 0;
								int sma3pass = 0;
								int sma4pass = 0;

								for (DL_ScripPrice hQ : scPrices)
								{
									IDS_SMASpread smaSpread = new IDS_SMASpread();
									smaSpread.setDate(hQ.getDate());
									smaSpread.setClosePrice(Precision.round(hQ.getCloseprice(), 2));
									for (int i = 0; i < smaIntervals.length; i++)
									{
										if (loopPass > smaIntervals[i])
										{
											if (i == 0)
											{
												smaSpread.setSMAI1(sma1.get(sma1pass));
												sma1pass++;
											}
											if (i == 1)
											{
												smaSpread.setSMAI2(sma2.get(sma2pass));
												sma2pass++;
											}
											if (i == 2)
											{
												smaSpread.setSMAI3(sma3.get(sma3pass));
												sma3pass++;
											}
											if (i == 3)
											{
												smaSpread.setSMAI4(sma4.get(sma4pass));
												sma4pass++;
											}
										}
									}

									loopPass++;
									scSMASpread.getPrSMAList().add(smaSpread);
								}
							}

						}
					}
				}
			}

		}
		return scSMASpread;
	}

	@Override
	public List<IDL_IDSStats> getStats()
	{
		List<IDL_IDSStats> stats = null;
		if (repoSCPrices != null)
		{
			stats = repoSCPrices.getIDSDataHubStats();
		}
		return stats;
	}

	@Override
	@Transactional
	public void updateDailyPrices() throws Exception
	{
		// Get Latest Prices Maintain Dates from Data Lake for PF Schema Scrips
		List<IScMaxDate> scMaxDatesList = repoSCPrices.getIDSDataHubLatestSripDate();
		if (scMaxDatesList != null)
		{
			if (scMaxDatesList.size() > 0)
			{
				List<DL_ScripPrice> scPricesInsert = new ArrayList<DL_ScripPrice>();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
						Locale.ENGLISH);

				for (IScMaxDate dlEntry : scMaxDatesList)
				{
					Stock curr = StockPricesUtility.getQuoteforScrip(dlEntry.getSccode());

					Date lastTradedDate = UtilDurations.getDateOnly4mCalendar(curr.getQuote().getLastTradeTime());

					if (lastTradedDate.equals(dlEntry.getMaxdate()))
					{
						// Same Day Entry Found - Check for Amount difference

						double savedClosedPrice = repoSCPrices.getClosePricebyId(dlEntry.getId());

						// If Amount Difference Found - Only then Trigger an Update
						if (savedClosedPrice != Precision.round(curr.getQuote().getPrice().doubleValue(), 2))
						{

							repoSCPrices.updateClosePrice(dlEntry.getId(),
									Precision.round(curr.getQuote().getPrice().doubleValue(), 2));
						}

					} else // No entry found for Last Traded day
					{
						if (lastTradedDate.after(dlEntry.getMaxdate()))
						{
							// Last Entry in Data Lake before the last Traded Day Entry - INSERT
							// Build the Collection
							DL_ScripPrice insertEntity = new DL_ScripPrice();
							insertEntity.setSccode(dlEntry.getSccode());
							insertEntity.setCloseprice(Precision.round(curr.getQuote().getPrice().doubleValue(), 2));
							insertEntity.setDate(curr.getQuote().getLastTradeTime().getTime());

							scPricesInsert.add(insertEntity);
						}
					}
				}

				// Perform All Inserts
				if (scPricesInsert != null)
				{
					if (scPricesInsert.size() > 0)
					{
						repoSCPrices.saveAll(scPricesInsert);

					}
				}
			}
		}

	}

	@PostConstruct
	public void initializeBean()
	{
		if (scPricesMode != null && repoPfSchema != null && repoSCPrices != null)
		{
			// Scrips History by DB and not Yahoo API
			// Check application.properties attribute scpricesDBMode
			if (scPricesMode.getScpricesDBMode() == 1)
			{
				if (repoPfSchema.count() > 0 && repoSCPrices.count() > 0)
				{
					List<String> scrips = repoPfSchema.getPFScripCodes();
					if (scrips != null)
					{
						if (scrips.size() > 0)
						{

							Calendar to = UtilDurations.getTodaysCalendarDateOnly();

							Calendar from = UtilDurations.getTodaysCalendarDateOnly();
							from.add(defaultPastDurationUnit, defaultPastDurationAmount * -1);

							for (String scrip : scrips)
							{
								List<DL_ScripPrice> scPrices = repoSCPrices
										.findAllBySccodeAndDateBetweenOrderByDateDesc(scrip, from.getTime(),
												to.getTime());
								if (scPrices != null)
								{
									this.pricesHistoryContainer.addAll(scPrices);
								}
							}
						}
					}
				}
			}
		}
	}

}
