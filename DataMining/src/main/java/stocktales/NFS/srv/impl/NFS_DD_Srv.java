package stocktales.NFS.srv.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.pojo.NFSExitSMADelta;
import stocktales.NFS.model.pojo.NFSPFExitSMA;
import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.pojo.NFS_DD4ListScrips;
import stocktales.NFS.model.pojo.NFS_DD4ListScripsI;
import stocktales.NFS.model.pojo.ScripPPU;
import stocktales.NFS.model.pojo.ScripPPUUnitsRank;
import stocktales.NFS.model.ui.NFSPFExit;
import stocktales.NFS.srv.intf.INFS_DD_Srv;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import yahoofinance.Stock;

@Service
public class NFS_DD_Srv implements INFS_DD_Srv
{

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private SCPricesMode scPriceMode;

	@Override
	public NFS_DD4ListScrips getDDByScrips(List<String> SC_List) throws Exception
	{
		NFS_DD4ListScrips ddObj = null;
		double avgWt = 0;

		if (SC_List != null)
		{
			if (SC_List.size() > 0)
			{
				avgWt = Precision.round(100 / SC_List.size(), 1);
				ddObj = new NFS_DD4ListScrips();

				for (String scrip : SC_List)
				{
					NFSExitSMADelta smaCmp = null;
					if (scPriceMode.getScpricesDBMode() == 1)
					{
						Stock stock = StockPricesUtility.getQuoteforScrip(scrip);
						if (stock != null)
						{
							smaCmp = new NFSExitSMADelta(stock.getQuote().getPrice().doubleValue(),
									stock.getQuote().getPriceAvg50().doubleValue(),
									stock.getQuote().getChangeFromAvg50InPercent().doubleValue() * -1);
						}
					} else
					{
						smaCmp = StockPricesUtility.getDeltaSMAforDaysfromCMP(scrip, nfsConfig.getSmaExitDays());
					}
					if (smaCmp != null)
					{
						NFS_DD4ListScripsI itemDD = new NFS_DD4ListScripsI();
						itemDD.setCmp(Precision.round(smaCmp.getCmp(), 2));
						itemDD.setScCode(scrip);
						itemDD.setDelta(Precision.round(smaCmp.getDelta(), 2));
						itemDD.setSma(Precision.round(smaCmp.getSma(), 2));
						itemDD.setWtdPLPer(Precision.round(((itemDD.getDelta() * avgWt) / 100), 2));
						ddObj.getScripDDItems().add(itemDD);
					}

				}

				ddObj.setMaxPerLoss(Precision
						.round(ddObj.getScripDDItems().stream().mapToDouble(NFS_DD4ListScripsI::getWtdPLPer).sum(), 1));

			}
		}

		return ddObj;
	}

	@Override
	public NFS_DD4ListScrips getDDByScripsPPU(List<ScripPPU> SC_PPU_List) throws Exception
	{
		NFS_DD4ListScrips ddObj = null;
		double avgWt = 0;

		if (SC_PPU_List != null)
		{
			if (SC_PPU_List.size() > 0)
			{
				avgWt = Precision.round(100 / SC_PPU_List.size(), 1);
				ddObj = new NFS_DD4ListScrips();

				for (ScripPPU scrip : SC_PPU_List)
				{
					NFSExitSMADelta smaCmp = null;

					if (scPriceMode.getScpricesDBMode() == 1)
					{
						Stock stock = StockPricesUtility.getQuoteforScrip(scrip.getSccode());
						if (stock != null)
						{
							smaCmp = new NFSExitSMADelta(stock.getQuote().getPrice().doubleValue(),
									stock.getQuote().getPriceAvg50().doubleValue(),
									stock.getQuote().getChangeFromAvg50InPercent().doubleValue() * -1);
						}
					} else
					{
						smaCmp = StockPricesUtility.getDeltaSMAforDaysfromCMP(scrip.getSccode(),
								nfsConfig.getSmaExitDays());
					}
					if (smaCmp != null)
					{
						NFS_DD4ListScripsI itemDD = new NFS_DD4ListScripsI();
						itemDD.setCmp(Precision.round(scrip.getPpu(), 2));
						itemDD.setScCode(scrip.getSccode());
						itemDD.setDelta(UtilPercentages.getPercentageDelta(scrip.getPpu(), smaCmp.getSma(), 2));
						itemDD.setSma(Precision.round(smaCmp.getSma(), 2));
						itemDD.setWtdPLPer(Precision.round(((itemDD.getDelta() * avgWt) / 100), 2));
						ddObj.getScripDDItems().add(itemDD);
					}

				}

				ddObj.setMaxPerLoss(Precision
						.round(ddObj.getScripDDItems().stream().mapToDouble(NFS_DD4ListScripsI::getWtdPLPer).sum(), 1));

			}
		}

		return ddObj;
	}

	@Override
	public NFSPFExitSS getDDByScripsPPUUnits(List<ScripPPUUnitsRank> scHoldings) throws Exception
	{

		NFSPFExitSS nfsPFExitSS = null;
		List<NFSPFExitSMA> nfsPFExitSmaTab = null;
		double maxLoss = 0;
		if (scHoldings != null)
		{

			if (scHoldings.size() > 0)
			{
				nfsPFExitSS = new NFSPFExitSS();
				nfsPFExitSmaTab = new ArrayList<NFSPFExitSMA>();
				NFSExitSMADelta smaCmp = null;
				for (ScripPPUUnitsRank holding : scHoldings)
				{
					NFSPFExitSMA pfExitEnt = new NFSPFExitSMA();
					pfExitEnt.setScCode(holding.getSccode());
					pfExitEnt.setRank(holding.getRankCurr());
					pfExitEnt.setPriceIncl(holding.getPpu());

					if (scPriceMode.getScpricesDBMode() == 1)
					{
						Stock stock = StockPricesUtility.getQuoteforScrip(holding.getSccode());
						if (stock != null)
						{
							smaCmp = new NFSExitSMADelta(stock.getQuote().getPrice().doubleValue(),
									stock.getQuote().getPriceAvg50().doubleValue(),
									stock.getQuote().getChangeFromAvg50InPercent().doubleValue() * -1);
						}
					} else
					{

						if (holding.getRankCurr() >= nfsConfig.getNfsSlotMax())
						{
							/*
							 * Check with SMA Rank Lower - (lower one 38 days) In holding Rank = Rank Max
							 * when not found in Latest proposals from Re-balance
							 */
							smaCmp = StockPricesUtility.getDeltaSMAforDaysfromCMP(holding.getSccode(),
									nfsConfig.getSmaExitDays());

						} else
						{
							/*
							 * Rank Intact and Within Current Proposals Check with SMA Rank Exit Fail-
							 * (higher one 48 days - more breathing space) In holding Rank = Rank Max when
							 * not found in Latest proposals from Re-balance
							 */
							smaCmp = StockPricesUtility.getDeltaSMAforDaysfromCMP(holding.getSccode(),
									nfsConfig.getSmaExitRankFailDays());

						}
					}

					if (smaCmp != null)
					{
						pfExitEnt.setPriceCmp(smaCmp.getCmp());
						pfExitEnt.setPriceExit(Precision.round(smaCmp.getSma(), 1));
						pfExitEnt.setCmpExitDelta(smaCmp.getDelta());
						pfExitEnt.setPlExit(UtilPercentages.getPercentageDelta(holding.getPpu(), smaCmp.getSma(), 1));
						nfsPFExitSmaTab.add(pfExitEnt);

						maxLoss += holding.getUnits() * (pfExitEnt.getPriceExit() - pfExitEnt.getPriceIncl());

					}
				}

				nfsPFExitSS.setPfExitsSMAList(nfsPFExitSmaTab);

				if (nfsPFExitSmaTab != null)
				{
					List<NFSPFExitSMA> exitScrips = nfsPFExitSmaTab.stream().filter(x -> x.getCmpExitDelta() >= 0)
							.collect(Collectors.toList());

					for (NFSPFExitSMA exitScrip : exitScrips)
					{
						NFSPFExit nfsExit = new NFSPFExit();
						nfsExit.setScCode(exitScrip.getScCode());
						nfsExit.setPlPer(Precision.round((exitScrip.getPlExit() - exitScrip.getCmpExitDelta()), 1));

						Optional<ScripPPUUnitsRank> holdingO = scHoldings.stream()
								.filter(d -> d.getSccode().equals(exitScrip.getScCode())).findFirst();
						if (holdingO.isPresent())
						{
							double lossAmnt = holdingO.get().getUnits()
									* (exitScrip.getPriceCmp() - exitScrip.getPriceIncl());
							nfsExit.setPlAmnt(Precision.round(lossAmnt, 0));

						}

						nfsPFExitSS.getPfExitScrips().add(nfsExit);
					}

					nfsPFExitSS.setCurrInv(scHoldings.stream().map(p -> p.getUnits() * p.getPpu())
							.collect(Collectors.summingDouble(Double::doubleValue)));
					nfsPFExitSS.setCurrInvStr(
							UtilDecimaltoMoneyString.getMoneyStringforDecimal(nfsPFExitSS.getCurrInv(), 1));

					double totalPL = nfsPFExitSS.getPfExitScrips().stream().mapToDouble(NFSPFExit::getPlAmnt).sum();
					nfsPFExitSS.setPlExitAmnt(Precision.round(totalPL, 0));
					nfsPFExitSS.setPlExitPer(
							Precision.round(((nfsPFExitSS.getPlExitAmnt() * 100) / nfsPFExitSS.getCurrInv()), 1));

					nfsPFExitSS.setMaxLoss(Precision.round(maxLoss, 0));
					nfsPFExitSS.setMaxLossStr(
							UtilDecimaltoMoneyString.getMoneyStringforDecimal(nfsPFExitSS.getMaxLoss(), 1));

					nfsPFExitSS.setMaxLossPer(
							Precision.round(((nfsPFExitSS.getMaxLoss() * 100) / nfsPFExitSS.getCurrInv()), 1));
				}

			}

		}

		return nfsPFExitSS;

	}

}
