package stocktales.historicalPrices.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.util.StringUtils;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;
import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_ScripUnits;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.pojo.NFSExitSMADelta;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;
import stocktales.NFS.model.pojo.StockHistoryCmpNFS;
import stocktales.NFS.model.pojo.StockHistoryNFS;
import stocktales.NFS.model.pojo.StockUnitsPPU;
import stocktales.NFS.repo.intfPOJO.ScripUnits;
import stocktales.basket.allocations.config.pojos.IntvPriceCAGR;
import stocktales.basket.allocations.config.pojos.ScripCMPHistReturns;
import stocktales.durations.UtilDurations;
import stocktales.exceptions.StockQuoteException;
import stocktales.historicalPrices.pojo.StockCurrQuote;
import stocktales.historicalPrices.pojo.StockHistory;
import stocktales.maths.UtilCAGRCalculation;
import stocktales.maths.UtilPercentages;
import stocktales.strategy.helperPOJO.SectorAllocations;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockPricesUtility
{

	/**
	 * Get Scrip Price History
	 * 
	 * @param scripCode - Scrip Code
	 * @param from      - Calendar From
	 * @param to        - Calendar To
	 * @param Interval  - yahoofinance.histquotes.Interval
	 * @param inclCMP   - true if with last row as last trade price desired
	 * @return - List<HistoricalQuote> with last row as last trade price
	 * @throws Exception
	 */
	public static List<HistoricalQuote> getHistory(String scripCode, Calendar from, Calendar to, Interval Interval,
			boolean inclCMP) throws Exception
	{
		List<HistoricalQuote> scHistory = new ArrayList<HistoricalQuote>();

		if (StringUtils.hasText(scripCode))
		{
			String nseScCode = scripCode + ".NS";

			String interval;

			if (from != null && to != null && to.after(from))
			{
				long fromDate = datechange(from).getTime() / 1000;

				long toDate = datechange(to).getTime() / 1000;

				switch (Interval)
				{
				case DAILY:
					interval = "1d";
					break;

				case MONTHLY:
					interval = "1mo";
					break;

				case WEEKLY:
					interval = "1wk";
					break;

				default:
					interval = "1d";
					break;
				}

				String link = "https://query1.finance.yahoo.com/v7/finance/download/" + nseScCode + "?period1="
						+ fromDate + "&period2=" + toDate + "&interval=" + interval + "&events=history";

				URL url = new URL(link);
				URLConnection urlConn = url.openConnection();
				if (urlConn != null)
				{
					InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
					BufferedReader buf = new BufferedReader(inStream);
					String line = buf.readLine();
					int i = 0;
					while (line != null)
					{
						line = buf.readLine();

						if (line != null && i >= 0)
						{
							String[] attrbs = line.split("\\,");
							if (attrbs.length > 0)
							{

								String[] coords = attrbs[0].split("-");

								Calendar cal = Calendar.getInstance();

								int year = new Integer(coords[0]).intValue();
								int mon = new Integer(coords[1]).intValue();
								int day = new Integer(coords[2]).intValue();
								cal.clear();
								cal.set(year, mon - 1, day); // Jan = 0

								if (Arrays.toString(attrbs).indexOf("null") > -1)
								{
									// Ignore BAd Data Dates
								} else
								{

									HistoricalQuote hQ = new HistoricalQuote(scripCode, cal, new BigDecimal(attrbs[1]),
											new BigDecimal(attrbs[3]), new BigDecimal(attrbs[2]),
											new BigDecimal(attrbs[4]), new BigDecimal(attrbs[5]),
											Long.parseLong(attrbs[6]));
									if (hQ != null)
									{
										scHistory.add(hQ);
									}
								}

							}
						}
						i++;
					}
				}

			}

		}

		if (inclCMP != true)
		{
			scHistory.remove(scHistory.size() - 1); // Take out Last Trade Price - CMP if Not needed
		}

		return scHistory;

	}

	public static List<DL_ScripPriceATH> getHistoricalClosePrices4Scrip(String scripCode, Calendar from, Calendar to,
			Interval Interval, boolean inclCMP) throws Exception
	{
		List<DL_ScripPriceATH> scHistory = new ArrayList<DL_ScripPriceATH>();

		if (StringUtils.hasText(scripCode))
		{
			String nseScCode = scripCode + ".NS";

			String interval;

			if (from != null && to != null && to.after(from))
			{
				long fromDate = datechange(from).getTime() / 1000;

				long toDate = datechange(to).getTime() / 1000;

				switch (Interval)
				{
				case DAILY:
					interval = "1d";
					break;

				case MONTHLY:
					interval = "1mo";
					break;

				case WEEKLY:
					interval = "1wk";
					break;

				default:
					interval = "1d";
					break;
				}

				String link = "https://query1.finance.yahoo.com/v7/finance/download/" + nseScCode + "?period1="
						+ fromDate + "&period2=" + toDate + "&interval=" + interval + "&events=history";

				URL url = new URL(link);
				URLConnection urlConn = url.openConnection();

				if (urlConn != null)
				{
					InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
					BufferedReader buf = new BufferedReader(inStream);
					String line = buf.readLine();
					int i = 0;
					while (line != null)
					{
						line = buf.readLine();

						if (line != null && i >= 0)
						{
							String[] attrbs = line.split("\\,");
							if (attrbs.length > 0)
							{

								String[] coords = attrbs[0].split("-");

								Calendar cal = Calendar.getInstance();

								int year = new Integer(coords[0]).intValue();
								int mon = new Integer(coords[1]).intValue();
								int day = new Integer(coords[2]).intValue();
								cal.clear();
								cal.set(year, mon - 1, day); // Jan = 0

								if (Arrays.toString(attrbs).indexOf("null") > -1)
								{
									// Ignore BAd Data Dates
								} else
								{

									DL_ScripPriceATH hQ = new DL_ScripPriceATH(scripCode, cal.getTime(),
											new BigDecimal(attrbs[5]).doubleValue());
									if (hQ != null)
									{
										scHistory.add(hQ);
									}
								}

							}
						}
						i++;
					}
				}

			}

		}

		if (inclCMP != true)
		{
			scHistory.remove(scHistory.size() - 1); // Take out Last Trade Price - CMP if Not needed
		}

		return scHistory;

	}

	public static List<DL_ScripPriceATH> getHistoricalClosePrices4ScripwithCookies(String scripCode, Calendar from,
			Calendar to, Interval Interval, boolean inclCMP) throws Exception
	{
		List<DL_ScripPriceATH> scHistory = new ArrayList<DL_ScripPriceATH>();

		if (StringUtils.hasText(scripCode))
		{
			String nseScCode = scripCode + ".NS";

			String interval;

			if (from != null && to != null && to.after(from))
			{
				long fromDate = datechange(from).getTime() / 1000;

				long toDate = datechange(to).getTime() / 1000;

				switch (Interval)
				{
				case DAILY:
					interval = "1d";
					break;

				case MONTHLY:
					interval = "1mo";
					break;

				case WEEKLY:
					interval = "1wk";
					break;

				default:
					interval = "1d";
					break;
				}

				// Hit the below URL to get the cookies and the crumb value to access the
				// finance API
				String mainURL = "https://finance.yahoo.com/quote/" + nseScCode + "/history";
				Map<String, List<String>> setCookies = setCookies(mainURL);

				String link = "https://query1.finance.yahoo.com/v7/finance/download/" + nseScCode + "?period1="
						+ fromDate + "&period2=" + toDate + "&interval=" + interval + "&events=history&crumb="
						+ searchCrumb(new URL(mainURL).openConnection());

				URL url = new URL(link);
				URLConnection urlConn = url.openConnection();
				// get the list of Set-Cookie cookies from response headers
				List<String> cookies = setCookies.get("Set-Cookie");
				if (cookies != null)
				{
					for (String c : cookies)
						urlConn.setRequestProperty("Cookie", c);
				}
				if (urlConn != null)
				{
					InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
					BufferedReader buf = new BufferedReader(inStream);
					String line = buf.readLine();
					int i = 0;
					while (line != null)
					{
						line = buf.readLine();

						if (line != null && i >= 0)
						{
							String[] attrbs = line.split("\\,");
							if (attrbs.length > 0)
							{

								String[] coords = attrbs[0].split("-");

								Calendar cal = Calendar.getInstance();

								int year = new Integer(coords[0]).intValue();
								int mon = new Integer(coords[1]).intValue();
								int day = new Integer(coords[2]).intValue();
								cal.clear();
								cal.set(year, mon - 1, day); // Jan = 0

								if (Arrays.toString(attrbs).indexOf("null") > -1)
								{
									// Ignore BAd Data Dates
								} else
								{

									DL_ScripPriceATH hQ = new DL_ScripPriceATH(scripCode, cal.getTime(),
											new BigDecimal(attrbs[5]).doubleValue());
									if (hQ != null)
									{
										scHistory.add(hQ);
									}
								}

							}
						}
						i++;
					}
				}

			}

		}

		if (inclCMP != true)
		{
			scHistory.remove(scHistory.size() - 1); // Take out Last Trade Price - CMP if Not needed
		}

		return scHistory;

	}

	// This method extracts the cookies from response headers and passes the same
	// con object to searchCrumb()
	// method to extract the crumb and set the crumb value in finalCrumb global
	// variable
	private static Map<String, List<String>> setCookies(String mainUrl) throws IOException
	{
		// "https://finance.yahoo.com/quote/SPY";
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		URL url = new URL(mainUrl);
		URLConnection con = url.openConnection();
//		String finalCrumb = searchCrumb(con);
		for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet())
		{
			if (entry.getKey() == null || !entry.getKey().equals("Set-Cookie"))
				continue;
			for (String s : entry.getValue())
			{
				map.put(entry.getKey(), entry.getValue());
				System.out.println(map);
			}
		}

		return map;

	}

	// This method extracts the crumb and is being called from setCookies() method
	private static String searchCrumb(URLConnection con) throws IOException
	{
		String crumb = null;
		InputStream inStream = con.getInputStream();
		InputStreamReader irdr = new InputStreamReader(inStream);
		BufferedReader rsv = new BufferedReader(irdr);

		Pattern crumbPattern = Pattern.compile(".*\"CrumbStore\":\\{\"crumb\":\"([^\"]+)\"\\}.*");

		String line = null;
		while (crumb == null && (line = rsv.readLine()) != null)
		{
			Matcher matcher = crumbPattern.matcher(line);
			if (matcher.matches())
				crumb = matcher.group(1);
		}
		rsv.close();
		System.out.println("Crumb is : " + crumb);
		return crumb;
	}

	public static Date datechange(Calendar cal) throws ParseException, java.text.ParseException
	{
		Date dateOne = cal.getTime();

		String a = dateOne.toString();
		String b[] = a.split(" ");
		String c = b[1] + " " + b[2] + " " + b[5];
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(sdf.parse(c));
		dateOne = cal.getTime();
		sdf.format(dateOne);
		return dateOne;

	}

	/**
	 * REturn the Scrip's CMP and its SMA Number of Days specified Price Delta in
	 * Percentage
	 * 
	 * @param scripCode  - NSE Scrip code
	 * @param numDaysSMA - Number of Days for which SMA is to be used for Comparison
	 * @return - CMP/SMA & delta Number of Days Percentage Difference
	 * @throws Exception
	 */
	public static NFSExitSMADelta getDeltaSMAforDaysfromCMP(String scripCode, int numDaysSMA) throws Exception
	{
		double delta = 0;
		double cmp, avgprice;
		NFSExitSMADelta smaexit = new NFSExitSMADelta();

		int numworkDaysAvgPerMonth = 20; // Constant

		int numMonthstoSeek = (numDaysSMA / numDaysSMA) + 1; // Months to Seek additional buffer of 1 month

		List<HistoricalQuote> topNHQ = null;
		List<HistoricalQuote> descHQ = null;

		// 1. Format the scrips symbols with exchange Info

		if (scripCode != null)
		{
			if (scripCode.trim().length() > 0)
			{

				scripCode = scripCode + ".NS";
			}

			Stock stock = YahooFinance.get(scripCode, true);
			if (stock != null)
			{
				cmp = stock.getQuote().getPrice().doubleValue();

				// Prepare the Duration
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();
				from.add(Calendar.MONTH, numMonthstoSeek * -1); // from Calendar.Interval Amounts ago

				List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, Interval.DAILY);
				if (HistQuotes != null)
				{
					if (HistQuotes.size() > 0)
					{
						// Got History - sort it in Descending Order by Date
						descHQ = HistQuotes.stream().filter(x -> x.getAdjClose() != null).collect(Collectors.toList());
						descHQ = descHQ.stream().sorted(Comparator.comparing(HistoricalQuote::getDate).reversed())
								.collect(Collectors.toList());
						topNHQ = descHQ.stream().limit(numDaysSMA).collect(Collectors.toList());

						if (topNHQ.size() > 0)
						{
							avgprice = topNHQ.stream().map(HistoricalQuote::getAdjClose)
									.reduce(BigDecimal.ZERO, BigDecimal::add)
									.divide(new BigDecimal(topNHQ.size()), RoundingMode.CEILING).doubleValue();

							delta = UtilPercentages.getPercentageDelta(cmp, avgprice, 1);

							smaexit.setCmp(cmp);
							smaexit.setSma(avgprice);
							smaexit.setDelta(delta);
						}

					}
				}

			}
		}

		return smaexit;
	}

	/**
	 * Get Stock Handle for Scrip Code - NSE
	 * 
	 * @param scCode - Scrip NSE Code
	 * @return - Yahoo Finance Stock Handle
	 * @throws Exception
	 */
	public static Stock getQuoteforScrip(String scCode) throws Exception
	{

		Stock stock = null;
		boolean connError = false;
		try
		{
			stock = YahooFinance.get(scCode + ".NS");
		} catch (IOException e) // Std Connection Refuse Exception- Yahoo Server
		{
			connError = true;

		}
		if (connError == true)
		{
			throw new StockQuoteException("Error Connecting to Yahoo Finance for Scrip - " + scCode);
		} else if (stock == null) // Custom Invalid Scrip Code Exception
		{
			throw new StockQuoteException("Invalid Scrip Code - " + scCode);

		}

		return stock;
	}

	/**
	 * Get Historical Prices for List of Scrips as per Choice of
	 * Interval(Year/Month/Week), Amount(number) , Frequency of Prices Lookout
	 * (Daily/Weekly/Monthly) within Interval
	 * 
	 * @param scrips    - Array Of Scrips Symbols - NSE
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - List of Stock symbols and their Historical Prices
	 * @throws Exception
	 */
	public static List<StockHistory> getHistoricalPricesforScrips(String[] scrips, int interval, int amount,
			Interval frequency) throws Exception
	{
		List<StockHistory> stocksPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrips != null)
		{
			if (scrips.length > 0)
			{
				for (int i = 0; i < scrips.length; i++)
				{
					scrips[i] = scrips[i] + ".NS";
				}

				Map<String, Stock> stocks = YahooFinance.get(scrips, true);
				if (stocks != null)
				{
					SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
					stocksPrices = new ArrayList<StockHistory>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								// New Stock History
								StockHistory stHist = new StockHistory();
								String symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stHist.setScCode(scNames[0]);
									}
								}

								// Prepare the Duration
								Calendar from = Calendar.getInstance();
								Calendar to = Calendar.getInstance();
								from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

								List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
								if (HistQuotes != null)
								{
									if (HistQuotes.size() > 0)
									{
										for (HistoricalQuote hQuote : HistQuotes)
										{
											if (hQuote != null)
											{
												if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
												{
													stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
															format1.format(hQuote.getDate().getTime()),
															hQuote.getDate().getTime(),
															Precision.round(hQuote.getAdjClose().doubleValue(), 1));
													stHist.getPriceHistory().add(newQuote);
												}
											}
										}
									}
								}
								stocksPrices.add(stHist);
							}

						}
					}
				}

			}
		}

		return stocksPrices;
	}

	/**
	 * Get Stock Codes and Std. Historical Quotes for
	 * 
	 * @param scrips    - Array Of Scrips Symbols - NSE
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - List of Stock symbols and their Historical Prices
	 * @throws Exception
	 */
	public static List<NFSStockHistoricalQuote> getHistoricalPricesforScripsStd(String[] scrips, int interval,
			int amount, Interval frequency) throws Exception
	{
		List<NFSStockHistoricalQuote> stocksHPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrips != null)
		{
			if (scrips.length > 0)
			{
				for (int i = 0; i < scrips.length; i++)
				{
					scrips[i] = scrips[i] + ".NS";
				}

				Map<String, Stock> stocks = YahooFinance.get(scrips, true);
				if (stocks != null)
				{

					stocksHPrices = new ArrayList<NFSStockHistoricalQuote>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								// New Stock History
								NFSStockHistoricalQuote stHQuote = new NFSStockHistoricalQuote();

								String symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stHQuote.setScCode(scNames[0]);
									}
								}

								// Prepare the Duration
								Calendar from = Calendar.getInstance();
								Calendar to = Calendar.getInstance();
								from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

								List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
								if (HistQuotes != null)
								{
									if (HistQuotes.size() > 0)
									{
										for (HistoricalQuote hQuote : HistQuotes)
										{
											if (hQuote != null)
											{
												if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
												{
													stHQuote.getQuotesH().add(hQuote);
												}
											}
										}
									}
								}
								stocksHPrices.add(stHQuote);
							}

						}
					}
				}

			}
		}

		return stocksHPrices;
	}

	/**
	 * Get CMP and the Historical REturns for the Scrip
	 * 
	 * @param scrip    - Scrip in Q
	 * @param interval - Interval Enum - e.g. Calendar.Year
	 * @param amount   [] - Interval Amounts arrays to traverse e.g. [3,5] - for
	 *                 last 3 , 5 yrs.
	 * @return - SCCode/CMP and a list of {interval|CMP in interval|CAGR w.r.t CMP }
	 * @throws Exception
	 */
	public static ScripCMPHistReturns getHistoricalCAGRforScrip(String scrip, int interval, int[] amount)
			throws Exception
	{

		ScripCMPHistReturns scReturns = new ScripCMPHistReturns();
		String intvSuffix;
		int factor;

		if (scrip != null)
		{
			if (scrip.trim().length() > 0)
			{

				scrip = scrip.trim() + ".NS";

				Stock stock = YahooFinance.get(scrip, true);
				if (stock != null)
				{
					scReturns.setSccode(scrip);
					scReturns.setCmp(Precision.round(stock.getQuote().getPrice().doubleValue(), 1));

					switch (interval)
					{
					case Calendar.YEAR:
						intvSuffix = "Yr.";
						factor = 12;
						break;

					case Calendar.MONTH:
						intvSuffix = "M";
						factor = 1;
						break;

					case Calendar.DAY_OF_MONTH:
						intvSuffix = "D";
						factor = 365;
						break;

					default:
						throw new Exception(
								"Invalid Interval Specified for Scrip Historical Price Check - " + interval);
					}

					// Prepare the Duration
					Calendar from = Calendar.getInstance();
					// Sort Durations
					Arrays.sort(amount);
					// Get Max Duration
					int maxDuration = amount[amount.length - 1];

					if (maxDuration > 0)
					{
						from.add(interval, maxDuration * -1); // from Calendar.Interval Amounts ago

						List<HistoricalQuote> HistQuotes = stock.getHistory(from);
						if (HistQuotes != null)
						{
							if (HistQuotes.size() > 0)
							{

								if (HistQuotes.size() < (maxDuration * factor))
								{
									throw new Exception("History Traverse to - " + maxDuration + intvSuffix
											+ " is not possible. Max History available for last - " + HistQuotes.size()
											+ " months only!");
								}
								for (int i = 0; i < amount.length; i++)
								{
									int index = (factor * maxDuration - factor * amount[i]);
									HistoricalQuote hQuote = HistQuotes.get(index);

									while (hQuote == null || hQuote.getAdjClose() == null)
									{

										hQuote = HistQuotes.get(index++);
									}

									if (hQuote != null)
									{

										scReturns.getReturns()
												.add(new IntvPriceCAGR(amount[i] + intvSuffix,
														Precision.round(hQuote.getAdjClose().doubleValue(), 2),
														Precision.round(UtilCAGRCalculation.calculateCAGR(
																Precision.round(hQuote.getAdjClose().doubleValue(), 2),
																scReturns.getCmp(), amount[i]), 2)));
									}

								}

							}
						}
					}

				}
			}
		}

		return scReturns;
	}

	/**
	 * Get Stock Codes and Std. Historical Quotes for
	 * 
	 * @param scrip     - Scrip Symbol - NSE
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - List of Stock symbols and their Historical Prices
	 * @throws Exception
	 */
	public static NFSStockHistoricalQuote getHistoricalPricesforScrips(String scrip, int interval, int amount,
			Interval frequency) throws Exception
	{
		NFSStockHistoricalQuote stockHPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.trim().length() > 0)
			{

				scrip = scrip + ".NS";
			}

			Stock stock = YahooFinance.get(scrip, true);
			if (stock != null)
			{

				stockHPrices = new NFSStockHistoricalQuote();

				String symbol = stock.getSymbol();
				if (symbol != null)
				{
					String[] scNames = symbol.split("\\.");
					if (scNames.length > 1)
					{
						stockHPrices.setScCode(scNames[0]);
					}
				}

				// Prepare the Duration
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();
				int penDur = amount * -1;
				from.add(interval, penDur); // from Calendar.Interval Amounts ago

				List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
				if (HistQuotes != null)
				{
					if (HistQuotes.size() > 0)
					{
						for (HistoricalQuote hQuote : HistQuotes)
						{
							if (hQuote != null)
							{
								if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
								{
									stockHPrices.getQuotesH().add(hQuote);
								}
							}
						}
					}
				}

			}

		}

		return stockHPrices;

	}

	/**
	 * Get Stock Codes and Std. Historical Quotes for
	 * 
	 * @param scrip     - Scrip Symbol - NSE
	 * @param from      - Calendar Instance From
	 * @param to        - Calendar Instance to
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - List of Stock symbols and their Historical Prices
	 * @throws Exception
	 */
	public static NFSStockHistoricalQuote getHistoricalPricesforScripsEndatPast(String scrip, Calendar from,
			Calendar to, Interval frequency) throws Exception
	{
		NFSStockHistoricalQuote stockHPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.trim().length() > 0)
			{

				scrip = scrip + ".NS";
			}

			Stock stock = YahooFinance.get(scrip, true);
			if (stock != null)
			{

				stockHPrices = new NFSStockHistoricalQuote();

				String symbol = stock.getSymbol();
				if (symbol != null)
				{
					String[] scNames = symbol.split("\\.");
					if (scNames.length > 1)
					{
						stockHPrices.setScCode(scNames[0]);
					}
				}

				// Prepare the Duration
				/*
				 * Calendar from = Calendar.getInstance(); Calendar to = Calendar.getInstance();
				 * to.add(interval, pastEndingInterval * -1); // To End On this Date
				 * from.add(interval, (amount + pastEndingInterval) * -1); // from
				 * Calendar.Interval Amounts ago
				 */
				List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
				if (HistQuotes != null)
				{
					if (HistQuotes.size() > 0)
					{
						for (HistoricalQuote hQuote : HistQuotes)
						{
							if (hQuote != null)
							{
								if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
								{
									stockHPrices.getQuotesH().add(hQuote);
								}
							}
						}
					}
				}

			}

		}

		return stockHPrices;

	}

	public static double getHistoricalPricesforScrip4Date(String scrip, Date date) throws Exception
	{
		double price = 0;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.trim().length() > 0)
			{

				scrip = scrip + ".NS";
			}

			Stock stock = YahooFinance.get(scrip, true);
			if (stock != null)
			{

				// Prepare the Duration

				Calendar dateCal = Calendar.getInstance();
				dateCal.setTime(date);

				Calendar fromDate = Calendar.getInstance();
				fromDate.setTime(date);
				fromDate.add(Calendar.DAY_OF_MONTH, -1);

				List<HistoricalQuote> HistQuotes = stock.getHistory(fromDate, dateCal, Interval.DAILY);
				if (HistQuotes != null)
				{
					if (HistQuotes.size() > 0)
					{
						HistoricalQuote hQuote = HistQuotes.get(HistQuotes.size() - 1);

						if (hQuote != null)
						{
							if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
							{
								price = hQuote.getAdjClose().doubleValue();
							}
						}

					}
				}

			}

		}

		return price;
	}

	/**
	 * Get Historical Prices for List of Scrips as per Choice of
	 * Interval(Year/Month/Week), Amount(number) , Frequency of Prices Lookout
	 * (Daily/Weekly/Monthly) within Interval
	 * 
	 * @param scrips    - Scrip Symbol (NSE)
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - Stock symbol, Its CMP, ITS 50 SMA and their Historical Prices
	 * @throws Exception
	 */
	public static StockHistoryCmpNFS getHistoricalPricesforScrip(String scrip, int interval, int amount,
			Interval frequency) throws Exception
	{
		StockHistoryCmpNFS stockHistory = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.trim().length() > 0)
			{

				scrip = scrip + ".NS";

				Stock stock = YahooFinance.get(scrip, true);
				if (stock != null)
				{
					SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

					// New Stock History
					stockHistory = new StockHistoryCmpNFS();
					String symbol = stock.getSymbol();
					if (symbol != null)
					{
						String[] scNames = symbol.split("\\.");
						if (scNames.length > 1)
						{
							stockHistory.setScCode(scNames[0]);
						}
					}

					stockHistory.setCmp(Precision.round((stock.getQuote().getPrice().doubleValue()), 0));
					stockHistory.setSma50(Precision.round((stock.getQuote().getPriceAvg50().doubleValue()), 0));

					// Prepare the Duration
					Calendar from = Calendar.getInstance();
					Calendar to = Calendar.getInstance();
					from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

					List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
					if (HistQuotes != null)
					{
						if (HistQuotes.size() > 0)
						{
							for (HistoricalQuote hQuote : HistQuotes)
							{
								if (hQuote != null)
								{
									if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
									{
										stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
												format1.format(hQuote.getDate().getTime()), hQuote.getDate().getTime(),
												Precision.round(hQuote.getAdjClose().doubleValue(), 1));
										stockHistory.getPriceHistory().add(newQuote);
									}
								}
							}
						}
					}
				}

			}
		}

		return stockHistory;
	}

	/**
	 * Get Current Prices for Scrips List
	 * 
	 * @param scrips - Array of Scrip Codes
	 * @return - Current Scrips Prices
	 * @throws Exception
	 */
	public static List<StockCurrQuote> getCurrentPricesforScrips(String[] scrips) throws Exception
	{
		List<StockCurrQuote> stocksPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrips != null)
		{
			if (scrips.length > 0)
			{
				for (int i = 0; i < scrips.length; i++)
				{
					scrips[i] = scrips[i] + ".NS";
				}

				Map<String, Stock> stocks = YahooFinance.get(scrips);
				if (stocks != null)
				{

					stocksPrices = new ArrayList<StockCurrQuote>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								// New Stock History
								StockCurrQuote stCP = new StockCurrQuote();
								String symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stCP.setScCode(scNames[0]);
									}

									stCP.setCurrPrice(Precision.round(stock.getQuote().getPrice().doubleValue(), 1));
									stocksPrices.add(stCP);
								}

							}

						}
					}
				}

			}
		}

		return stocksPrices;
	}

	/**
	 * Get Current Prices for Scrips List
	 * 
	 * @param scrips - List of Scrip Codes
	 * @return - Current Scrips Prices
	 * @throws Exception
	 */
	public static List<StockCurrQuote> getCurrentPricesforScrips(List<String> scripCodes) throws Exception
	{
		List<StockCurrQuote> stocksPrices = null;
		int i = 0;

		// 1. Format the scrips symbols with exchange Info

		if (scripCodes != null)
		{
			if (scripCodes.size() > 0)
			{
				String[] scrips = new String[scripCodes.size()];
				for (String sccode : scripCodes)
				{
					scrips[i] = sccode + ".NS";
					i++;
				}

				Map<String, Stock> stocks = YahooFinance.get(scrips);
				if (stocks != null)
				{

					stocksPrices = new ArrayList<StockCurrQuote>();
					if (stocks.size() > 0)
					{
						for (Map.Entry<String, Stock> stockset : stocks.entrySet())
						{
							Stock stock = stockset.getValue();
							if (stock != null)
							{
								// New Stock History
								StockCurrQuote stCP = new StockCurrQuote();
								String symbol = stock.getSymbol();
								if (symbol != null)
								{
									String[] scNames = symbol.split("\\.");
									if (scNames.length > 1)
									{
										stCP.setScCode(scNames[0]);
									}

									stCP.setCurrPrice(Precision.round(stock.getQuote().getPrice().doubleValue(), 1));
									stocksPrices.add(stCP);
								}

							}

						}
					}
				}

			}
		}

		return stocksPrices;
	}

	/**
	 * Get Historical Prices for Scrip as per Choice of Interval(Year/Month/Week),
	 * Amount(number) , Frequency of Prices Lookout (Daily/Weekly/Monthly) within
	 * Interval
	 * 
	 * @param scrip     - Scrip NSE Code
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @return - Stock symbol and its Historical Prices
	 * @throws Exception
	 */
	public static StockHistoryNFS getHistoricalPricesMcapforScrip(String scrip, int interval, int amount,
			Interval frequency) throws Exception
	{
		StockHistoryNFS stockPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.length() > 0)
			{
				scrip = scrip + ".NS";

				Stock stock = YahooFinance.get(scrip, true);

				SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

				if (stock != null)
				{
					// New Stock History
					stockPrices = new StockHistoryNFS();
					String symbol = stock.getSymbol();
					if (symbol != null)
					{
						String[] scNames = symbol.split("\\.");
						if (scNames.length > 1)
						{
							stockPrices.setScCode(scNames[0]);
						}
					}

					stockPrices.setMCap(Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0));

					// Prepare the Duration
					Calendar from = Calendar.getInstance();
					Calendar to = Calendar.getInstance();
					from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

					List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
					if (HistQuotes != null)
					{
						if (HistQuotes.size() > 0)
						{
							for (HistoricalQuote hQuote : HistQuotes)
							{
								if (hQuote != null)
								{
									if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
									{
										stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
												format1.format(hQuote.getDate().getTime()), hQuote.getDate().getTime(),
												Precision.round(hQuote.getAdjClose().doubleValue(), 1));
										stockPrices.getPriceHistory().add(newQuote);
									}
								}
							}
						}
					}

				}

			}

		}

		return stockPrices;
	}

	/**
	 * Get Historical Prices for Scrip as per Choice of Interval(Year/Month/Week),
	 * Amount(number) , Frequency of Prices Lookout (Daily/Weekly/Monthly) within
	 * Interval
	 * 
	 * @param scrip     - Scrip NSE Code
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @param McapMinm  - Do Not include History for Scrips having MCap less than
	 *                  This value in Cr.
	 * @return - Stock symbol and its Historical Prices
	 * @throws Exception
	 */
	public static StockHistoryNFS getHistoricalPricesforScripwithMcapFilter(String scrip, int interval, int amount,
			Interval frequency, double McapMinm) throws Exception
	{
		StockHistoryNFS stockPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.length() > 0)
			{
				scrip = scrip + ".NS";

				Stock stock = YahooFinance.get(scrip, true);

				SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

				if (stock != null)
				{
					// New Stock History
					stockPrices = new StockHistoryNFS();
					String symbol = stock.getSymbol();
					if (symbol != null)
					{
						String[] scNames = symbol.split("\\.");
						if (scNames.length > 1)
						{
							stockPrices.setScCode(scNames[0]);
						}
					}

					stockPrices.setMCap(Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0));

					if (stockPrices.getMCap() > McapMinm)
					{

						// Prepare the Duration
						Calendar from = Calendar.getInstance();
						Calendar to = Calendar.getInstance();
						from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

						List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
						if (HistQuotes != null)
						{
							if (HistQuotes.size() > 0)
							{
								for (HistoricalQuote hQuote : HistQuotes)
								{
									if (hQuote != null)
									{
										if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
										{
											stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
													format1.format(hQuote.getDate().getTime()),
													hQuote.getDate().getTime(),
													Precision.round(hQuote.getAdjClose().doubleValue(), 1));
											stockPrices.getPriceHistory().add(newQuote);
										}
									}
								}
							}
						}

					}

				}

			}

		}

		return stockPrices;
	}

	/**
	 * Get Historical Prices for Scrip as per Choice of Interval(Year/Month/Week),
	 * Amount(number) , Frequency of Prices Lookout (Daily/Weekly/Monthly) within
	 * Interval
	 * 
	 * @param scrip     - Scrip NSE Code
	 * @param interval  - Calendar ENum
	 * @param amount    - Amount of Intervals to Traverse Back
	 * @param frequency - Interval Enum from Yahoo.Finance
	 * @param McapMinm  - Do Not include History for Scrips having MCap less than
	 *                  This value in Cr.
	 * @param from      - Calendar Representation of Starting Date
	 * @return - Stock symbol and its Historical Prices
	 * @throws Exception
	 */
	public static StockHistoryNFS getHistoricalPricesforScripwithMcapFilter(String scrip, int interval, int amount,
			Interval frequency, double McapMinm, Calendar from) throws Exception
	{
		StockHistoryNFS stockPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.length() > 0)
			{
				scrip = scrip + ".NS";

				Stock stock = YahooFinance.get(scrip, true);

				SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

				if (stock != null)
				{
					// New Stock History
					stockPrices = new StockHistoryNFS();
					String symbol = stock.getSymbol();
					if (symbol != null)
					{
						String[] scNames = symbol.split("\\.");
						if (scNames.length > 1)
						{
							stockPrices.setScCode(scNames[0]);
						}
					}

					stockPrices.setMCap(Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0));

					if (stockPrices.getMCap() > McapMinm)
					{

						// Prepare the Duration

						Calendar to = from;
						from.add(interval, amount * -1); // from Calendar.Interval Amounts ago

						List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
						if (HistQuotes != null)
						{
							if (HistQuotes.size() > 0)
							{
								for (HistoricalQuote hQuote : HistQuotes)
								{
									if (hQuote != null)
									{
										if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
										{
											stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
													format1.format(hQuote.getDate().getTime()),
													hQuote.getDate().getTime(),
													Precision.round(hQuote.getAdjClose().doubleValue(), 1));
											stockPrices.getPriceHistory().add(newQuote);
										}
									}
								}
							}
						}

					}

				}

			}

		}

		return stockPrices;
	}

	/**
	 * Get Historical Prices for Scrip as per Choice of Interval(Year/Month/Week),
	 * Amount(number) , Frequency of Prices Lookout (Daily/Weekly/Monthly) within
	 * Interval
	 * 
	 * @param scrip             - Scrip NSE Code
	 * @param interval          - Calendar ENum
	 * @param amount            - Amount of Intervals to Traverse Back
	 * @param frequency         - Interval Enum from Yahoo.Finance
	 * @param McapMinm          - Do Not include History for Scrips having MCap less
	 *                          than This value in Cr.
	 * @param numYearsBeginFrom - Number of Years from where to Stop Search e.g. if
	 *                          you want to test a strategy from 2016 to 2018 and
	 *                          today is 2021 then amount field will be
	 *                          5(2021-2016), interval Years and numYearsEndUpto
	 *                          will be 3(2021-2018)
	 * @return - Stock symbol and its Historical Prices
	 * @throws Exception
	 */
	public static StockHistoryNFS getHistoricalPricesforScripwithMcapFilterBeginFrom(String scrip, int interval,
			int amount, Interval frequency, double McapMinm, int numYearsBeginFrom) throws Exception
	{
		StockHistoryNFS stockPrices = null;

		// 1. Format the scrips symbols with exchange Info

		if (scrip != null)
		{
			if (scrip.length() > 0)
			{
				scrip = scrip + ".NS";

				Stock stock = YahooFinance.get(scrip, true);

				SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

				if (stock != null)
				{
					// New Stock History
					stockPrices = new StockHistoryNFS();
					String symbol = stock.getSymbol();
					if (symbol != null)
					{
						String[] scNames = symbol.split("\\.");
						if (scNames.length > 1)
						{
							stockPrices.setScCode(scNames[0]);
						}
					}

					stockPrices.setMCap(Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0));

					if (stockPrices.getMCap() > McapMinm)
					{

						// Prepare the Duration
						Calendar from = Calendar.getInstance();
						Calendar to = Calendar.getInstance();
						from.add(interval, amount * -1); // from Calendar.Interval Amounts ago; 2016
						to.add(Calendar.YEAR, numYearsBeginFrom * -1); // to Calendar.Interval Amounts Starting Point:
																		// 2018

						List<HistoricalQuote> HistQuotes = stock.getHistory(from, to, frequency);
						if (HistQuotes != null)
						{
							if (HistQuotes.size() > 0)
							{
								for (HistoricalQuote hQuote : HistQuotes)
								{
									if (hQuote != null)
									{
										if (hQuote.getDate() != null && hQuote.getAdjClose() != null)
										{
											stocktales.historicalPrices.pojo.HistoricalQuote newQuote = new stocktales.historicalPrices.pojo.HistoricalQuote(
													format1.format(hQuote.getDate().getTime()),
													hQuote.getDate().getTime(),
													Precision.round(hQuote.getAdjClose().doubleValue(), 1));
											stockPrices.getPriceHistory().add(newQuote);
										}
									}
								}
							}
						}

					}

				}

			}

		}

		return stockPrices;
	}

	/**
	 * Get the minimum Amount needed for Portfolio Creation
	 * 
	 * @param scAllocs - List of Scrip Codes and Allocations
	 * @return - Amount that is minimum required to maintain allocations as
	 *         requested w.r.t CMP
	 * @throws Exception
	 */
	public static double getMinmAmntforPFCreation(List<SectorAllocations> scAllocs) throws Exception
	{
		double minAmnt = 0;
		if (scAllocs.size() > 0)
		{
			// 1. Get the CMp of all the stocks in Question
			List<String> scCodes = new ArrayList<String>();
			scAllocs.stream().filter(x -> scCodes.add(x.getSector())).distinct().collect(Collectors.toList());
			List<StockCurrQuote> scCMPList = StockPricesUtility.getCurrentPricesforScrips(scCodes);

			// 2. Get the Max CMP Scrip
			StockCurrQuote maxCMPScrip = Collections.max(scCMPList, Comparator.comparing(x -> x.getCurrPrice()));
			if (maxCMPScrip != null)
			{
				// 3. Get the % allocation for this Scrip
				Optional<SectorAllocations> maxCMPScAllocO = scAllocs.stream()
						.filter(w -> w.getSector().equals(maxCMPScrip.getScCode())).findFirst();
				if (maxCMPScAllocO.isPresent())
				{
					double alloc = maxCMPScAllocO.get().getAlloc();
					minAmnt = maxCMPScrip.getCurrPrice() / (alloc / 100);
				}

			}

		}

		return minAmnt;
	}

	/**
	 * Get Additional Stock Units and PPU and Nett. Balance After Purchase for
	 * Existing holding or Not if Any for Incremental/New Investment
	 * 
	 * @param scCode               - Scrip Code to be Purchased
	 * @param exisUnits            - Existing Units of Scrip that you hold
	 * @param exisPPU              - Existing Price Per Unit for your Holding of the
	 *                             scrip
	 * @param additionalInvestment - Additional Amount you would like to invest
	 * @return - POJO that contains Total Units/PPU and any balance Amount after
	 *         Purchase w.r.t additional Investment
	 * @throws Exception
	 */
	public static StockUnitsPPU getTotalUnitsPPU(String scCode, int exisUnits, double exisPPU,
			double additionalInvestment) throws Exception
	{
		StockUnitsPPU unitsPPU = new StockUnitsPPU();

		if (scCode != null && additionalInvestment > 0)
		{
			Stock stock = StockPricesUtility.getQuoteforScrip(scCode);
			if (stock != null)
			{
				double cmp = stock.getQuote().getPrice().doubleValue();
				if (cmp < additionalInvestment)
				{
					int units = (int) Math.round(additionalInvestment / cmp);
					double cost = units * cmp;

					unitsPPU.setUnits(exisUnits + units);
					unitsPPU.setPpu(Precision.round(((exisPPU * exisUnits) + cost) / unitsPPU.getUnits(), 2));
					unitsPPU.setBalAmnt(additionalInvestment - cost);

				} else
				{
					unitsPPU.setUnits(exisUnits);
					unitsPPU.setPpu(exisPPU);
					unitsPPU.setBalAmnt(additionalInvestment);
				}
			}
		}

		return unitsPPU;
	}

	/**
	 * Get Current Value of the Portfolio
	 * 
	 * @param scUnits - List of <Sccode, Units>
	 * @return - Current PF Value
	 * @throws Exception
	 */
	public static double getCurrentValueforScripsPF(List<ScripUnits> scUnits) throws Exception
	{
		double val = 0;

		for (ScripUnits scripUnit : scUnits)
		{
			if (scripUnit.Sccode() != null && scripUnit.Units() > 0)
			{
				if (scripUnit.Sccode().trim().length() > 0)
				{
					double cmp = StockPricesUtility.getQuoteforScrip(scripUnit.Sccode()).getQuote().getPrice()
							.doubleValue();
					val += (cmp * scripUnit.Units());
				}
			}
		}

		return val;
	}

	/**
	 * Get Current Value of the Portfolio
	 * 
	 * @param scUnits - List of <Sccode, Units>
	 * @return - Current PF Value
	 * @throws Exception
	 */
	public static double getCurrentValueforScripsandUnits(List<IDS_ScripUnits> scUnits) throws Exception
	{
		double val = 0;

		for (IDS_ScripUnits scripUnit : scUnits)
		{
			if (scripUnit.getScCode() != null && scripUnit.getUnits() > 0)
			{
				if (scripUnit.getScCode().trim().length() > 0)
				{
					double cmp = StockPricesUtility.getQuoteforScrip(scripUnit.getScCode()).getQuote().getPrice()
							.doubleValue();
					val += (cmp * scripUnit.getUnits());
				}
			}
		}

		return val;
	}

	/**
	 * Get Current Value of the Portfolio
	 * 
	 * @param scUnits - List of <Sccode, Units>
	 * @return - Current PF Value
	 * @throws Exception
	 */
	public static double getCurrentValueforScripsPFCmpl(List<NFSPF> scUnits) throws Exception
	{
		double val = 0;

		for (NFSPF scripUnit : scUnits)
		{
			if (scripUnit.getSccode() != null && scripUnit.getUnits() > 0)
			{
				if (scripUnit.getSccode().trim().length() > 0)
				{
					double cmp = StockPricesUtility.getQuoteforScrip(scripUnit.getSccode()).getQuote().getPrice()
							.doubleValue();
					val += (cmp * scripUnit.getUnits());
				}
			}
		}

		return val;
	}

	public static IDS_ScSMASpread getSMASpreadforScrip(String scrip, int[] smaIntervals, int interval, int amount,
			Interval frequency) throws Exception
	{
		IDS_ScSMASpread scSMASpread = null;
		List<Double> sma1 = new ArrayList<Double>();
		List<Double> sma2 = new ArrayList<Double>();
		List<Double> sma3 = new ArrayList<Double>();
		List<Double> sma4 = new ArrayList<Double>();

		if (scrip != null && smaIntervals.length > 0)
		{
			// 1. Get the Prices Simply for the said Duration, amount frequency
			NFSStockHistoricalQuote scHistoricaPrices = getHistoricalPricesforScrips(scrip, interval, amount,
					frequency);
			if (scHistoricaPrices != null)
			{
				List<HistoricalQuote> historicalPrices = scHistoricaPrices.getQuotesH();
				if (historicalPrices != null)
				{
					if (historicalPrices.size() > 0)
					{

						scSMASpread = new IDS_ScSMASpread();
						scSMASpread.setScCode(scrip);

						// 2. Determine eligibility for SMA Computations

						if (historicalPrices.size() < smaIntervals[smaIntervals.length - 1])
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
									if (historicalPrices.size() > (valdaysIntv - 1))
									{
										loopupto = valdaysIntv;

									} else
									{
										loopupto = historicalPrices.size();
										loopend = true;
									}
									List<HistoricalQuote> subList = historicalPrices.subList(daystart, loopupto);

									// Get Avg SMa Price for the sublist
									double SMA = Precision.round(subList.stream().map(HistoricalQuote::getAdjClose)
											.reduce(BigDecimal.ZERO, BigDecimal::add)
											.divide(new BigDecimal(subList.size()), RoundingMode.CEILING).doubleValue(),
											2);
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

							for (HistoricalQuote hQ : historicalPrices)
							{
								IDS_SMASpread smaSpread = new IDS_SMASpread();
								smaSpread.setDate(hQ.getDate().getTime());
								smaSpread.setClosePrice(Precision.round(hQ.getAdjClose().doubleValue(), 2));
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

		return scSMASpread;
	}

	public static IDS_ScSMASpread getSMASpread4Scrip4Durations(String scrip, int[] smaIntervals, Calendar from,
			Calendar to, Interval frequency) throws Exception
	{
		IDS_ScSMASpread scSMASpread = null;
		List<Double> sma1 = new ArrayList<Double>();
		List<Double> sma2 = new ArrayList<Double>();
		List<Double> sma3 = new ArrayList<Double>();
		List<Double> sma4 = new ArrayList<Double>();

		if (scrip != null && smaIntervals.length > 0)
		{
			// 1. Get the Prices Simply for the said Duration, amount frequency
			NFSStockHistoricalQuote scHistoricaPrices = getHistoricalPricesforScripsEndatPast(scrip, from, to,
					frequency);
			if (scHistoricaPrices != null)
			{
				List<HistoricalQuote> historicalPrices = scHistoricaPrices.getQuotesH();
				if (historicalPrices != null)
				{
					if (historicalPrices.size() > 0)
					{

						scSMASpread = new IDS_ScSMASpread();
						scSMASpread.setScCode(scrip);

						// 2. Determine eligibility for SMA Computations

						if (historicalPrices.size() < smaIntervals[smaIntervals.length - 1])
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
									if (historicalPrices.size() > (valdaysIntv - 1))
									{
										loopupto = valdaysIntv;

									} else
									{
										loopupto = historicalPrices.size();
										loopend = true;
									}
									List<HistoricalQuote> subList = historicalPrices.subList(daystart, loopupto);

									// Get Avg SMa Price for the sublist
									double SMA = Precision.round(subList.stream().map(HistoricalQuote::getAdjClose)
											.reduce(BigDecimal.ZERO, BigDecimal::add)
											.divide(new BigDecimal(subList.size()), RoundingMode.CEILING).doubleValue(),
											2);
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

							for (HistoricalQuote hQ : historicalPrices)
							{
								IDS_SMASpread smaSpread = new IDS_SMASpread();
								smaSpread.setDate(hQ.getDate().getTime());
								smaSpread.setClosePrice(Precision.round(hQ.getAdjClose().doubleValue(), 2));
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

		return scSMASpread;
	}

	public static IDS_ScSMASpread getSMASpread4Scrip4HistoricaPriceData(String scrip, int[] smaIntervals,
			NFSStockHistoricalQuote scHistoricaPrices) throws Exception
	{
		IDS_ScSMASpread scSMASpread = null;
		List<Double> sma1 = new ArrayList<Double>();
		List<Double> sma2 = new ArrayList<Double>();
		List<Double> sma3 = new ArrayList<Double>();
		List<Double> sma4 = new ArrayList<Double>();

		if (scrip != null && smaIntervals.length > 0 & scHistoricaPrices != null)
		{
			if (scHistoricaPrices.getQuotesH() != null)
			{
				List<HistoricalQuote> historicalPrices = scHistoricaPrices.getQuotesH();
				if (historicalPrices != null)
				{
					if (historicalPrices.size() > 0)
					{

						scSMASpread = new IDS_ScSMASpread();
						scSMASpread.setScCode(scrip);

						// 2. Determine eligibility for SMA Computations

						if (historicalPrices.size() < smaIntervals[smaIntervals.length - 1])
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
									if (historicalPrices.size() > (valdaysIntv - 1))
									{
										loopupto = valdaysIntv;

									} else
									{
										loopupto = historicalPrices.size();
										loopend = true;
									}
									List<HistoricalQuote> subList = historicalPrices.subList(daystart, loopupto);

									// Get Avg SMa Price for the sublist
									double SMA = Precision.round(subList.stream().map(HistoricalQuote::getAdjClose)
											.reduce(BigDecimal.ZERO, BigDecimal::add)
											.divide(new BigDecimal(subList.size()), RoundingMode.CEILING).doubleValue(),
											2);
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

							for (HistoricalQuote hQ : historicalPrices)
							{
								IDS_SMASpread smaSpread = new IDS_SMASpread();
								smaSpread.setDate(hQ.getDate().getTime());
								smaSpread.setClosePrice(Precision.round(hQ.getAdjClose().doubleValue(), 2));
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

		return scSMASpread;
	}

	public static SC_CMP_52wkPenultimatePrice_Delta getSCATHDataPool4Scrip(String scCode, Calendar startDate)
			throws Exception
	{

		SC_CMP_52wkPenultimatePrice_Delta athData = null;

		if (StringUtils.hasText(scCode) && startDate != null)
		{
			try
			{
				Calendar to = UtilDurations.getTodaysCalendarDateOnly();
				to.setTime(startDate.getTime());
				to.add(Calendar.YEAR, -1);

				List<HistoricalQuote> topN = null;

				List<HistoricalQuote> hqS = StockPricesUtility.getHistory(scCode, to, startDate, Interval.DAILY, true);
				if (hqS != null)
				{
					if (hqS.size() >= 100) // Minimum 100 Days data needed - Ignore Otherwise
					{
						// sort by Date Descending
						hqS.sort(Comparator.comparing(HistoricalQuote::getDate).reversed());

						athData = new SC_CMP_52wkPenultimatePrice_Delta();

						athData.setScCode(scCode);

						athData.setCmp(Precision.round(hqS.get(0).getAdjClose().doubleValue(), 2));
						athData.setLastYrPrice(Precision.round(hqS.get(hqS.size() - 1).getAdjClose().doubleValue(), 2));
						athData.setDelta(
								UtilPercentages.getPercentageDelta(athData.getLastYrPrice(), athData.getCmp(), 1));

						// top 20
						topN = hqS.stream().limit(20).collect(Collectors.toList());
						double sma20 = topN.stream().map(HistoricalQuote::getAdjClose)
								.reduce(BigDecimal.ZERO, BigDecimal::add)
								.divide(new BigDecimal(topN.size()), RoundingMode.CEILING).doubleValue();
						athData.setSma20(Precision.round(sma20, 2));
						athData.setSma20Delta(UtilPercentages.getPercentageDelta(sma20, athData.getCmp(), 1));

						// top 50
						topN = hqS.stream().limit(50).collect(Collectors.toList());
						double sma50 = topN.stream().map(HistoricalQuote::getAdjClose)
								.reduce(BigDecimal.ZERO, BigDecimal::add)
								.divide(new BigDecimal(topN.size()), RoundingMode.CEILING).doubleValue();
						athData.setSma50(Precision.round(sma50, 2));
						athData.setSma50Delta(UtilPercentages.getPercentageDelta(sma50, athData.getCmp(), 1));

						// top 100
						topN = hqS.stream().limit(100).collect(Collectors.toList());
						double sma100 = topN.stream().map(HistoricalQuote::getAdjClose)
								.reduce(BigDecimal.ZERO, BigDecimal::add)
								.divide(new BigDecimal(topN.size()), RoundingMode.CEILING).doubleValue();
						athData.setSma100(Precision.round(sma100, 2));
						athData.setSma100Delta(UtilPercentages.getPercentageDelta(sma100, athData.getCmp(), 1));

					}
				}
			} catch (FileNotFoundException e)
			{
				// Invalid Scrip Code - Do nothing
			}
		}

		return athData;

	}

}
