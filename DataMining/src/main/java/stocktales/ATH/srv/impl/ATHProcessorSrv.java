package stocktales.ATH.srv.impl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.ATH.model.pojo.ATHContainer;
import stocktales.ATH.model.pojo.ATHPool;
import stocktales.ATH.model.pojo.ATHStats;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.entity.NFSRunTmp;
import stocktales.NFS.repo.RepoBseData;
import stocktales.NFS.repo.RepoNFSTmp;
import stocktales.NFS.repo.intfPOJO.IScCodeSeries;
import stocktales.durations.UtilDurations;
import stocktales.exceptions.StockQuoteException;
import stocktales.historicalPrices.utility.StockPricesUtility;
import yahoofinance.Stock;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Service
public class ATHProcessorSrv implements stocktales.ATH.srv.intf.ATHProcessorSrv
{
	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private RepoNFSTmp repoNFSTmp;

	private ATHContainer athContainer;

	@Async
	@Override
	public CompletableFuture<ATHContainer> generateProposal(boolean updateDb) throws Exception
	{

		try
		{

			prepareDataPool();

			sieve4McapTadeDate();

			sieve4Momentum();

			sortRankSieve4T2T();

			if (updateDb == true)
			{
				persist2DB();
			}

		} catch (Exception e)
		{
			/*
			 * Will be handled Centrally by GlobalExcpetion controller
			 */
			throw new NotFoundException("NFS Scrip Pool could not be loaded!");
		}

		return CompletableFuture.completedFuture(this.athContainer);
	}

	@Transactional
	private void persist2DB()
	{

		if (athContainer.getProposals().size() >= nfsConfig.getPfSize())
		{
			repoNFSTmp.deleteAll();

			repoNFSTmp.saveAll(athContainer.getProposals());
		}
	}

	private void sortRankSieve4T2T()
	{
		if (athContainer.getAthPool().size() >= nfsConfig.getPfSize())
		{
			athContainer.getAthPool().sort(Comparator.comparingDouble(ATHPool::getYearHighDelta).reversed());

			int t2tPossible = (int) (nfsConfig.getT2tMaxPer() * nfsConfig.getPfSize() * .01);
			int t2tFound = 0;
			int rank = 1;
			Date today = UtilDurations.getTodaysDateOnly();
			/**
			 * Loop through and Pick 1.5 x PF size max
			 */
			for (ATHPool athPoolEntity : athContainer.getAthPool())
			{
				if (rank < (1.5 * nfsConfig.getPfSize()))
				{
					if (athPoolEntity.getSeries().equals("BE"))
					{
						t2tFound++;
						if (t2tFound < t2tPossible)
						{
							NFSRunTmp nfsTmp = new NFSRunTmp(athPoolEntity.getSccode(),
									athPoolEntity.getYearHighDelta(), rank, today);
							athContainer.getProposals().add(nfsTmp);
							rank++;
						}

					} else
					{
						NFSRunTmp nfsTmp = new NFSRunTmp(athPoolEntity.getSccode(), athPoolEntity.getYearHighDelta(),
								rank, today);
						athContainer.getProposals().add(nfsTmp);
						rank++;
					}
				}
			}

			athContainer.getAthStats().setNumFinalScrips(athContainer.getProposals().size());
			athContainer.getAthStats().setDataError(athContainer.getInvalidScrips().size());

		} else
		{
			athContainer.setMessage("Number of Scrips found - " + athContainer.getAthPool().size()
					+ "less than reqd. PF Size - " + nfsConfig.getPfSize());
		}

	}

	private void sieve4Momentum()
	{
		if (athContainer.getAthPool().size() > 0)
		{
			athContainer.getAthPool()
					.removeIf(u -> u.getSma50Delta() < nfsConfig.getSma50DeltaIncl()
							|| u.getSma200Delta() < nfsConfig.getSma200DeltaIncl() || u.getCmp() < u.getSma50()
							|| u.getCmp() < u.getSma200() || u.getSma50() < u.getSma200());

		}

		athContainer.getAthStats().setMomentumRemain(athContainer.getAthPool().size());

	}

	private void prepareDataPool() throws Exception
	{
		if (repoBseData != null)
		{
			List<IScCodeSeries> scCodeSeries = repoBseData.getAllNseCodesSeries();
			if (scCodeSeries != null)
			{
				if (scCodeSeries.size() > 0)
				{

					this.athContainer = new ATHContainer();
					this.athContainer.setAthStats(new ATHStats());
					this.getAthContainer().getAthStats().setTotalScrips(scCodeSeries.size());

					for (IScCodeSeries scCodeSer : scCodeSeries)
					{
						try
						{
							Stock quote = StockPricesUtility.getQuoteforScrip(scCodeSer.getNsecode());
							if (quote != null)
							{
								ATHPool poolEntity = getPoolEntityfromStockQuote(quote, scCodeSer);
								if (poolEntity != null)
								{
									athContainer.getAthPool().add(poolEntity);
								}
							}
						} catch (StockQuoteException e)
						{
							// Do Nothing - Leave this Scrip and Move Ahead
							athContainer.getInvalidScrips().add(scCodeSer.getNsecode());
						}
					}
				}
			}
		}

	}

	/**
	 * Sieve for Minim'm Market Cap and Last Trde date not before 7 days from today
	 */
	private void sieve4McapTadeDate()
	{
		Calendar todaypen7Days = UtilDurations.getTodaysCalendarDateOnly();
		todaypen7Days.add(Calendar.DAY_OF_MONTH, -7);

		if (athContainer.getAthPool().size() > 0)
		{
			athContainer.getAthPool().removeIf(
					x -> x.getMCapCr() < nfsConfig.getMinMCap() || x.getLastTradeDate().before(todaypen7Days.getTime())

			);

		}

		athContainer.getAthStats().setMcapFltRemain(athContainer.getAthPool().size());

	}

	private ATHPool getPoolEntityfromStockQuote(Stock stock, IScCodeSeries scCodeSer)
	{
		ATHPool ent = null;
		if (stock != null && stock.getStats() != null)
		{
			if (stock.getStats().getMarketCap() != null && stock.getQuote().getPriceAvg50() != null
					&& stock.getQuote().getPriceAvg200() != null && stock.getQuote().getYearHigh() != null
					&& stock.getQuote().getLastTradeTime() != null)
			{
				ent = new ATHPool(scCodeSer.getNsecode(), Precision.round(stock.getQuote().getPrice().doubleValue(), 2),
						Precision.round(stock.getQuote().getPriceAvg50().doubleValue(), 2),
						Precision.round(stock.getQuote().getChangeFromAvg50InPercent().doubleValue(), 2),
						Precision.round(stock.getQuote().getPriceAvg200().doubleValue(), 2),
						Precision.round(stock.getQuote().getChangeFromAvg200InPercent().doubleValue(), 2),
						Precision.round(stock.getQuote().getYearHigh().doubleValue(), 2),
						Precision.round(stock.getQuote().getChangeFromYearHighInPercent().doubleValue(), 2),
						Precision.round(stock.getQuote().getYearLow().doubleValue(), 2),
						Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0),
						UtilDurations.getDateOnly4mCalendar(stock.getQuote().getLastTradeTime()),
						scCodeSer.getSeries());
			}
		}

		return ent;
	}

}
