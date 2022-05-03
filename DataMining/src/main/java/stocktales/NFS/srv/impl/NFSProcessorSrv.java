package stocktales.NFS.srv.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;
import stocktales.NFS.enums.EnumNFSTxnType;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.entity.BseDataSet;
import stocktales.NFS.model.entity.NFSExitBook;
import stocktales.NFS.model.entity.NFSJournal;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.entity.NFSRunTmp;
import stocktales.NFS.model.pojo.NFSCB_IP;
import stocktales.NFS.model.pojo.NFSConsistency;
import stocktales.NFS.model.pojo.NFSContainer;
import stocktales.NFS.model.pojo.NFSExitSMADelta;
import stocktales.NFS.model.pojo.NFSPFExitSMA;
import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.pojo.NFSPriceManipulation;
import stocktales.NFS.model.pojo.NFSPriceManipulationItems;
import stocktales.NFS.model.pojo.NFSRealizations;
import stocktales.NFS.model.pojo.NFSScores;
import stocktales.NFS.model.pojo.NFSSmaCmp;
import stocktales.NFS.model.pojo.NFSStats;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;
import stocktales.NFS.model.pojo.StockHistoryCmpNFS;
import stocktales.NFS.model.pojo.StockHistoryNFS;
import stocktales.NFS.model.ui.NFSExitList;
import stocktales.NFS.model.ui.NFSPFExit;
import stocktales.NFS.model.ui.NFSPFExit_UISel;
import stocktales.NFS.model.ui.NFSRunTmpList;
import stocktales.NFS.model.ui.NFSRunTmp_UISel;
import stocktales.NFS.model.ui.NFS_UIRebalProposalContainer;
import stocktales.NFS.repo.RepoBseData;
import stocktales.NFS.repo.RepoNFSExitBook;
import stocktales.NFS.repo.RepoNFSJornal;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.repo.RepoNFSTmp;
import stocktales.NFS.srv.intf.INFSProcessor;
import stocktales.NFS.srv.intf.INFS_CashBookSrv;
import stocktales.NFS.srv.intf.INFS_DD_Srv;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.durations.UtilDurations;
import stocktales.historicalPrices.pojo.HistoricalQuote;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.strategy.helperPOJO.SectorAllocations;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Service
public class NFSProcessorSrv implements INFSProcessor
{
	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private RepoNFSTmp repoNFSTmp;

	@Autowired
	private RepoNFSPF repoNFSPF;

	@Autowired
	private INFS_CashBookSrv nfsCBSrv;

	@Autowired
	private RepoNFSJornal repoNFSJ;

	@Autowired
	private INFS_DD_Srv nfsDDSrv;

	@Autowired
	private RepoNFSExitBook repoExitBook;

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	private SCPricesMode scPriceMode;

	@Value("${nfs.minmAmntErr}")
	private final String errMinAmnt = "";

	private NFSContainer nfsContainer;

	/**
	 * Trigger NFS Portfolio Proposal Generation
	 * 
	 * @param updateDb - true: Update the DB with Proposal in Temporary Data Areas
	 * @return - NFS Container that Contains Complete Information about PF Creation
	 * @throws Exception
	 */
	@Override
	@Async

	public CompletableFuture<NFSContainer> generateProposal(boolean updateDb) throws Exception
	{

		try
		{
			/**
			 * 1. Get Basic Data Pool - Scrips and their Return Ratios for last '30' Months,
			 * Mcap and Number of Months Traded
			 */
			prepareDataPool();

			/**
			 * 2. Filter out where Min'n Traded Months < Configured
			 */

			filterforTradedDuration();

			/**
			 * 3. Get Top 'N' Scrips By Monthly Returns -Apply Consistency RR Filters
			 */
			prepareFilterforConsistency();

			/**
			 * 4. Prepare Daily Data Pool for Last 4 Months for Scrips Passing Consistency
			 * Filter
			 */
			prepareCons_Scrips_Price_Hist_4Months();

			/*
			 * 5. Prepare SMA Data for Price Trends Filteration
			 */
			prepareSMAData();

			/*
			 * 6. Filter for Momentum Trends
			 */
			filterforMomentum();

			/*
			 * 7. Calculate Consolidated Scores and Assign Ranks - Considering Price
			 * Manipulation and CMP {Filter}
			 */
			calculateScoresandRanks();

			/*
			 * If NFS Run Needs to be Persisted in Database
			 */
			if (updateDb == true && repoNFSTmp != null)
			{
				repoNFSTmp.deleteAll(); // Delete Temporary Run Data for previous run
				// Get Today's Date
				long millis = System.currentTimeMillis();
				java.util.Date date = new java.util.Date(millis);

				if (nfsContainer.getFinalSieveScores().size() > 0)
				{
					for (NFSScores nfsScore : nfsContainer.getFinalSieveScores())
					{
						NFSRunTmp nfstmp = new NFSRunTmp(nfsScore.getScCode(), nfsScore.getConsolidatedScore(),
								nfsScore.getRankCurr(), date);
						// Save Current Run REsults
						repoNFSTmp.save(nfstmp);
					}
				}

			}

		} catch (Exception e)
		{
			/*
			 * Will be handled Centrally by GlobalExcpetion controller
			 */
			throw new NotFoundException("NFS Scrip Pool could not be loaded!");
		}

		return CompletableFuture.completedFuture(this.nfsContainer);

	}

	/**
	 * Re-balance the Portfolio in the DB This re-balancing will run on last
	 * proposal of Momentum Scrips saved in the temporary memory area of DB For Best
	 * results- always generate proposal checking in Dbase Update Option before
	 * executing re-balance. This will also implicitly create a new PF for you in
	 * case one is not there. The Journal will also be updated on Each Re-balance
	 * that would implicitly record the trades
	 * 
	 * @throws Exception
	 */
	@Override
	public void rebalancePF_DB(double incrementalInvestment, boolean updateDb) throws Exception
	{
		if (nfsContainer == null)
		{
			this.nfsContainer = new NFSContainer();
		}
		if (!runRankScan(incrementalInvestment, updateDb)) // New PF if needed Implicitly created and Persisted
		{
			/**
			 * Only in CASE A NEW POrtfolio is NOT Created & it is a Re-balance For Either
			 * REplacements or Incremental Deployments
			 */
			if (this.nfsContainer.getRC().size() > 0)
			{
				// Seek Replacements as there are some exit Candidates
				seekReplacements();
			}

			// Update Journal
			updateJournal();

			// Save if REquested by Caller
			if (updateDb == true)
			{
				saveRebalance();
			}

		}

	}

	@Override
	public NFS_UIRebalProposalContainer rebalancePF_UI(double incrementalInvestment) throws Exception
	{
		NFS_UIRebalProposalContainer uiRebalContainer = null;

		this.nfsContainer = new NFSContainer();

		if (!runRankScanUI(incrementalInvestment, true)) // New PF if needed Implicitly created and Persisted
		{
			/**
			 * Only in CASE A NEW POrtfolio is NOT Created & it is a Re-balance For Either
			 * REplacements or Incremental Deployments
			 */

			// Initialize Portfolio in NFS container
			nfsContainer.setNFSPortfolio(repoNFSPF.findAll());

			if (repoNFSPF.count() == nfsConfig.getPfSize())
			{
				if (this.nfsContainer.getRC().size() > 0) // If sufficiently Diversifed
				{
					// Seek Replacements as there are some exit Candidates
					seekReplacementsUI();
				}
			} else
			{
				// If Not sufficiently diversified- seek proposals in case there are some new
				if (repoNFSTmp.count() > repoNFSPF.count())
				{
					seekReplacementsUI();
				}
			}

			uiRebalContainer = new NFS_UIRebalProposalContainer();
			uiRebalContainer.setNumCurrPFScrips((int) repoNFSPF.count());
			uiRebalContainer.setNumExits(nfsContainer.getRC().size());
			uiRebalContainer.setNumIdealScrips(nfsConfig.getPfSize());
			uiRebalContainer.setNumProposals(nfsContainer.getRT().size());
			uiRebalContainer.setInvAmnt(incrementalInvestment);
			uiRebalContainer.setInvAmntStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(incrementalInvestment, 2));
			uiRebalContainer.setCurrInvStr(
					UtilDecimaltoMoneyString.getMoneyStringforDecimal(repoNFSPF.getTotalInvestedValue(), 2));

			// Populate Exits
			if (uiRebalContainer.getNumExits() > 0)
			{

				uiRebalContainer.setExitsList(new NFSExitList());
				for (NFSPFExit exit : this.getPFExitSnapshot().getPfExitScrips())
				{
					NFSPFExit_UISel exitUI = new NFSPFExit_UISel();
					exitUI.setScCode(exit.getScCode());
					exitUI.setPlPer(exit.getPlPer());
					exitUI.setPlAmnt(exit.getPlAmnt());
					exitUI.setIsincluded(true);
					uiRebalContainer.getExitsList().getScExit().add(exitUI);
				}

			}

			// Populate Proposals
			if (uiRebalContainer.getNumProposals() > 0)
			{
				uiRebalContainer.setProposals(new NFSRunTmpList());

				// loop through proposals to generate UI Proposals
				for (NFSPF proposal : nfsContainer.getRT())
				{
					Optional<NFSRunTmp> nfsTmpO = repoNFSTmp.findBySccode(proposal.getSccode());
					if (nfsTmpO.isPresent())
					{
						NFSRunTmp_UISel proposalUI = new NFSRunTmp_UISel();
						proposalUI.setSccode(proposal.getSccode());
						proposalUI.setConsolscore(nfsTmpO.get().getConsolscore());
						proposalUI.setDate(nfsTmpO.get().getDate());
						proposalUI.setRank(nfsTmpO.get().getRank());
						proposalUI.setScreenerUrl(
								nfsConfig.getScreenerpf() + proposal.getSccode() + nfsConfig.getScreenersf());
						proposalUI.setIsincluded(false);

						uiRebalContainer.getProposals().getScSel().add(proposalUI);

					}
				}

			}

		}

		return uiRebalContainer;

	}

	/**
	 * Get the Current NFS Portfolio Exit SMA's and Percentages from CMP as they
	 * would get triggered
	 * 
	 * @return - NFS PF Exit Scenario Details for Current PF
	 * @throws Exception
	 */
	@Override
	public NFSPFExitSS getPFExitSnapshot() throws Exception
	{
		NFSPFExitSS nfsPFExitSS = null;
		List<NFSPFExitSMA> nfsPFExitSmaTab = null;
		double maxLoss = 0;
		if (repoNFSPF != null)
		{
			List<NFSPF> pf = repoNFSPF.findAll();
			if (pf.size() > 0)
			{
				nfsPFExitSS = new NFSPFExitSS();
				nfsPFExitSmaTab = new ArrayList<NFSPFExitSMA>();
				NFSExitSMADelta smaCmp = null;
				for (NFSPF holding : pf)
				{
					NFSPFExitSMA pfExitEnt = new NFSPFExitSMA();
					pfExitEnt.setScCode(holding.getSccode());
					pfExitEnt.setRank(holding.getRankcurr());
					pfExitEnt.setPriceIncl(holding.getPriceincl());

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

						if (holding.getRankcurr() >= nfsConfig.getNfsSlotMax())
						{
							/*
							 * Check with SMA Rank Lower - (lower one 35 days) In holding Rank = Rank Max
							 * when not found in Latest proposals from Re-balance
							 */
							smaCmp = StockPricesUtility.getDeltaSMAforDaysfromCMP(holding.getSccode(),
									nfsConfig.getSmaExitDays());

						} else
						{
							/*
							 * Rank Intact and Within Current Proposals Check with SMA Rank Exit Fail-
							 * (higher one 45 days - more breathing space) In holding Rank = Rank Max when
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
						pfExitEnt.setPlExit(
								UtilPercentages.getPercentageDelta(holding.getPriceincl(), smaCmp.getSma(), 1));
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
						Optional<NFSPF> holdingO = repoNFSPF.findBySccode(exitScrip.getScCode());
						if (holdingO.isPresent())
						{
							double lossAmnt = holdingO.get().getUnits()
									* (exitScrip.getPriceCmp() - exitScrip.getPriceIncl());
							nfsExit.setPlAmnt(Precision.round(lossAmnt, 0));

						}

						nfsPFExitSS.getPfExitScrips().add(nfsExit);
					}

					nfsPFExitSS.setCurrInv(repoNFSPF.getTotalInvestedValue());
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

	@Override
	public int getNumExitScrips() throws Exception
	{
		int numExits = 0;
		List<NFSPFExitSMA> scExitSS = this.getPFExitSnapshot().getPfExitsSMAList();
		if (scExitSS != null)
		{
			List<NFSPFExitSMA> exitScrips = scExitSS.stream().filter(x -> x.getCmpExitDelta() >= 0)
					.collect(Collectors.toList());
			numExits = exitScrips.size();
		}

		return numExits;
	}

	@Override
	public void createPF4mExistingProposalSelection(List<NFSPF> scripsSel, double invAmnt) throws Exception
	{

		this.nfsContainer = new NFSContainer();
		if (scripsSel.size() > 0 && invAmnt > 0)
		{

			List<NFSPF> tmpProposalsSortedbyRank = scripsSel.stream()
					.sorted(Comparator.comparingDouble(NFSPF::getRankincl)).collect(Collectors.toList());
			int x = 1;
			long millis = System.currentTimeMillis();
			java.util.Date dateToday = new java.util.Date(millis);
			List<String> scrips = new ArrayList<String>();

			double amountPerPosition = 0;
			int units = 0;
			double utilizedAmnt = 0;
			double perAlloc = 0;
			int loopCount = 0;

			if (scripsSel.size() > nfsConfig.getPfSize())
			{
				perAlloc = 100 / nfsConfig.getPfSize();
				amountPerPosition = invAmnt / nfsConfig.getPfSize();
				loopCount = nfsConfig.getPfSize();
			} else
			{
				perAlloc = 100 / scripsSel.size();
				amountPerPosition = invAmnt / scripsSel.size();
				loopCount = scripsSel.size();
			}

			// Create the PF
			for (NFSPF topn : tmpProposalsSortedbyRank)
			{
				if (x <= loopCount)
				{
					double cmp = Precision.round(
							StockPricesUtility.getQuoteforScrip(topn.getSccode()).getQuote().getPrice().doubleValue(),
							2);
					if (amountPerPosition > 0)
					{
						units = (int) Precision.round(amountPerPosition / cmp, 0);
						utilizedAmnt += (units * cmp);
					}

					NFSPF newPFEntity = new NFSPF(topn.getSccode(), topn.getRankincl(), cmp, topn.getRankincl(),
							dateToday, dateToday, units);

					this.nfsContainer.getNFSPortfolio().add(newPFEntity);
					scrips.add(topn.getSccode());

				}
				x++;

			}
			// Amount Left/Needed after PF Creation
			nfsContainer.setCashTxnBalance(invAmnt - utilizedAmnt);

			// Cash Book Entry
			nfsCBSrv.processCBTxn(utilizedAmnt, Precision.round(nfsDDSrv.getDDByScrips(scrips).getMaxPerLoss(), 1));

			repoNFSPF.saveAll(this.nfsContainer.getNFSPortfolio());

		}

	}

	@Override
	@Transactional
	public void massUpdatePF(List<NFSPF> updPF)
	{
		if (updPF != null)
		{
			if (updPF.size() > 0)
			{
				for (NFSPF nfspf : updPF)
				{
					Optional<NFSPF> exisPFO = repoNFSPF.findBySccode(nfspf.getSccode());
					if (exisPFO.isPresent())
					{
						// any changes in Units or PPU
						if (exisPFO.get().getUnits() != nfspf.getUnits()
								|| exisPFO.get().getPriceincl() != nfspf.getPriceincl())
						{
							if (nfspf.getUnits() == 0) // Position Exited
							{
								repoNFSPF.deleteById(nfspf.getSccode());
							}

							else
							{
								repoNFSPF.updateHoldingUnitsPPU(nfspf.getSccode(), nfspf.getPriceincl(),
										nfspf.getUnits());
							}
						}
					}
				}
			}
		}

	}

	@Override
	@Transactional
	public void exitPortfolio() throws Exception
	{
		/*
		 * Compute P&L for the PF and Exit
		 */
		double sumInv = 0;
		double sumCurrVal = 0;
		double plPer = 0;
		double sumplAmnt = 0;
		List<String> scrips = new ArrayList<String>();

		for (NFSPF nfspf : repoNFSPF.findAll())
		{
			double cmp = StockPricesUtility.getQuoteforScrip(nfspf.getSccode()).getQuote().getPrice().doubleValue();
			if (cmp > 0)
			{
				double invVal = nfspf.getPriceincl() * nfspf.getUnits();
				double currVal = cmp * nfspf.getUnits();
				double plAmnt = currVal - invVal;

				sumInv += invVal;
				sumCurrVal += currVal;
				sumplAmnt += plAmnt;
				scrips.add(nfspf.getSccode());
			}

			/**
			 * Persist in Exit Book
			 */
			this.postScripExit(nfspf.getSccode());

		}

		/**
		 * Trigger Cash Book Entry for Exit PF
		 */
		NFSCB_IP nfscbIP = new NFSCB_IP(EnumNFSTxnType.Exit, sumCurrVal);
		nfsCBSrv.processCBTxn(nfscbIP);

		plPer = (sumplAmnt / sumInv) * 100;

		/**
		 * Now Delete PF from DB
		 */

		repoNFSPF.deleteAll();

		/**
		 * Save Journal Entry
		 */

		NFSJournal nfsJ = new NFSJournal();
		nfsJ.setDate(UtilDurations.getTodaysDateOnly());
		nfsJ.setEntries(null);
		nfsJ.setExits(this.getScripsListasString(scrips));
		nfsJ.setNumexits(scrips.size());
		nfsJ.setNumscrips(0);
		nfsJ.setPerchurn(100);
		nfsJ.setRealpl(plPer);
		nfsJ.setRealplamnt(sumplAmnt);
		nfsJ.setUnrealpl(0);

		repoNFSJ.save(nfsJ);

	}

	@Override
	public void postScripExit(String scCode) throws Exception
	{
		if (StringUtils.hasText(scCode))
		{

			// Check if Scrip is in PF
			if (repoNFSPF.count() > 0 && repoExitBook != null)
			{
				Optional<NFSPF> holdingO = repoNFSPF.findBySccode(scCode);
				if (holdingO.isPresent())
				{
					// Initialize POJO
					NFSExitBook ebEntity = new NFSExitBook();
					ebEntity.setSccode(scCode);

					ebEntity.setDateincl(holdingO.get().getDateincl());

					if (scPriceMode.getScpricesDBMode() != 1)
					{

						ebEntity.setPpuincl(Precision.round(
								StockPricesUtility.getHistoricalPricesforScrip4Date(scCode, ebEntity.getDateincl()),
								2));
					}

					ebEntity.setPpuavg(holdingO.get().getPriceincl());

					ebEntity.setDateexit(UtilDurations.getTodaysDateOnly());

					ebEntity.setPpuexit(Precision
							.round(StockPricesUtility.getQuoteforScrip(scCode).getQuote().getPrice().doubleValue(), 2));

					ebEntity.setRealplper(
							UtilPercentages.getPercentageDelta(ebEntity.getPpuavg(), ebEntity.getPpuexit(), 2));

					if (scPriceMode.getScpricesDBMode() != 1)
					{

						ebEntity.setRealplperincl(
								UtilPercentages.getPercentageDelta(ebEntity.getPpuincl(), ebEntity.getPpuexit(), 2));
					}

					ebEntity.setNumdays(
							UtilDurations.getNumDaysbwSysDates(ebEntity.getDateincl(), ebEntity.getDateexit()));

					ebEntity.setRealzamnt(Precision
							.round((ebEntity.getPpuexit() - ebEntity.getPpuavg()) * holdingO.get().getUnits(), 1));

					ebEntity.setDatelastbuy(holdingO.get().getDatelasttxn());

					ebEntity.setPerpfexit(Precision.round(((holdingO.get().getUnits() * ebEntity.getPpuexit() * 100)
							/ repoNFSPF.getTotalInvestedValue()), 2));

					repoExitBook.save(ebEntity);

				}
			}
		}

	}

	/*
	 * -----------------------------------------------------------------------------
	 * ----------
	 * 
	 * PRIVATE SECTION
	 * -----------------------------------------------------------------------------
	 * ----------
	 */

	/**
	 * 1. Get Basic Data Pool - Scrips and their Return Ratios for last '30' Months,
	 * Mcap and Number of Months Traded
	 */
	private void prepareDataPool()
	{
		if (repoBseData != null)
		{
			List<String> scCodes = repoBseData.findAllNseCodes();
			StockHistoryNFS stocksHistory;
			List<StockHistoryNFS> stocksHistoryM = new ArrayList<StockHistoryNFS>();
			if (scCodes != null)
			{
				if (scCodes.size() > 0)
				{
					this.nfsContainer = new NFSContainer();
					this.nfsContainer.setNfsStats(new NFSStats());
					// int x = 0;

					for (String scCode : scCodes)
					{
						/*
						 * if (x < 50)
						 * 
						 * {
						 */

						try
						{
							stocksHistory = StockPricesUtility.getHistoricalPricesforScripwithMcapFilter(scCode,
									Calendar.MONTH, 24, Interval.MONTHLY, nfsConfig.getMinMCap());
							if (stocksHistory != null)
							{
								nfsContainer.getNfsStats()
										.setNumScripsDataAvail(nfsContainer.getNfsStats().getNumScripsDataAvail() + 1);
								if (stocksHistory.priceHistory.size() == 0)
								{
									// This is filtered Out Coz of Lower MCap - Capture in Stats
									nfsContainer.getNfsStats()
											.setNumMcapFltOut(nfsContainer.getNfsStats().getNumMcapFltOut() + 1);
								} else // Only Add the Ones for Monthly Run Rate Evaluation that pass Min'm Mcap
										// Criteria
								{
									stocksHistoryM.add(stocksHistory);
								}
							}

						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					/*
					 * x++; }
					 */

				}

			}
			if (stocksHistoryM.size() > 0)
			{

				this.nfsContainer.getNfsStats().setNumScripsTotal(scCodes.size());

				for (StockHistoryNFS stH : stocksHistoryM)
				{
					NFSConsistency nfsCons = new NFSConsistency();
					nfsCons.setScCode(stH.getScCode());
					nfsCons.setNumMonths(stH.getPriceHistory().size());
					nfsCons.setMCap(stH.getMCap());
					double inival, finalval, delta = 0;

					inival = stH.getPriceHistory().get(0).getClosePrice();
					finalval = stH.getPriceHistory().get(nfsCons.getNumMonths() - 1).getClosePrice();
					if (inival > 0)
					{
						delta = UtilPercentages.getPercentageDelta(inival, finalval, 2);

					}

					nfsCons.setMonthlyRR(Precision.round(delta / nfsCons.getNumMonths(), 1));
					nfsContainer.getBaseDataPool().add(nfsCons);
				}

			}

		}

	}

	/**
	 * 2. Filter out where Min'n Traded Months < Configured
	 * 
	 */
	private void filterforTradedDuration()
	{
		if (nfsContainer.getBaseDataPool().size() > 2)
		{
			if (nfsConfig.getMonthsMinTrade() > 0)
			{
				if (nfsContainer.getBaseDataPool()
						.removeIf(x -> x.getNumMonths() < nfsConfig.getMonthsMinTrade()) == true)
				{
					nfsContainer.getNfsStats().setNumDurationFltOut(nfsContainer.getNfsStats().getNumScripsDataAvail()
							- nfsContainer.getNfsStats().getNumMcapFltOut() - nfsContainer.getBaseDataPool().size());
					;
				}
			}
		}

	}

	/**
	 * 3. Get Top 'N' Scrips By Monthly Returns -Apply Consistency RR Filters
	 */
	private void prepareFilterforConsistency()
	{
		if (nfsConfig.getTopNDataSetPercRR() > 0 && nfsContainer.getBaseDataPool().size() > 2)
		{

			List<NFSConsistency> topNRR = null;
			List<NFSConsistency> sortedNRR = null;

			int topN = (int) Precision.round(
					(nfsContainer.getNfsStats().getNumScripsDataAvail() * (nfsConfig.getTopNDataSetPercRR() / 100)), 0);

			try
			{

				sortedNRR = nfsContainer.getBaseDataPool().stream()
						.sorted(Comparator.comparingDouble(NFSConsistency::getMonthlyRR).reversed())
						.collect(Collectors.toList());
				topNRR = sortedNRR.stream().limit(topN).collect(Collectors.toList());

				if (topNRR != null)
				{
					double avgRRTopN = topNRR.stream().mapToDouble(NFSConsistency::getMonthlyRR).average()
							.getAsDouble();
					if (avgRRTopN > 0)
					{

						nfsContainer.getNfsStats().setRRTopNAvg(avgRRTopN);
						nfsContainer.getNfsStats().setRRMin(avgRRTopN * nfsConfig.getRrThreholdEmerging());
					}

					// Filter the List for Consistency

					if (nfsContainer.getBaseDataPool()
							.removeIf(x -> x.getMonthlyRR() < nfsContainer.getNfsStats().getRRMin()) == true)
					{
						nfsContainer.getNfsStats()
								.setNumConsistencyFltOut(nfsContainer.getNfsStats().getNumScripsDataAvail()
										- nfsContainer.getNfsStats().getNumMcapFltOut()
										- nfsContainer.getNfsStats().getNumDurationFltOut()
										- nfsContainer.getBaseDataPool().size());

					}

					nfsContainer.getNfsStats()
							.setNumScripsTopN(nfsContainer.getNfsStats().getNumScripsDataAvail()
									- nfsContainer.getNfsStats().getNumMcapFltOut()
									- nfsContainer.getNfsStats().getNumDurationFltOut()
									- nfsContainer.getNfsStats().getNumConsistencyFltOut());

				}

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 4. Prepare Daily Data Pool for Last 4 Months for Scrips Passing Consistency
	 * Filter
	 */
	private void prepareCons_Scrips_Price_Hist_4Months()
	{
		if (nfsContainer.getBaseDataPool().size() > 0)
		{
			for (NFSConsistency nfsCons : nfsContainer.getBaseDataPool())
			{
				try
				{
					StockHistoryCmpNFS stHist = StockPricesUtility.getHistoricalPricesforScrip(nfsCons.getScCode(),
							Calendar.MONTH, 6, Interval.DAILY);
					if (stHist != null)
					{
						this.nfsContainer.getConsFltPassScrips_PriceHist().add(stHist);
					}
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/*
	 * 5. Prepare SMA Data for Price Trends Filtration
	 */
	private void prepareSMAData()
	{
		if (nfsContainer.getConsFltPassScrips_PriceHist().size() > 0)
		{
			for (StockHistoryCmpNFS stHist : nfsContainer.getConsFltPassScrips_PriceHist())
			{
				if (stHist.getPriceHistory() != null)
				{
					if (stHist.getPriceHistory().size() > 0)
					{
						List<HistoricalQuote> priceHistDescOrder = null;
						List<HistoricalQuote> priceHist100Days = null;
						List<HistoricalQuote> priceHist20Days = null;
						double sma20, sma100 = 0;

						priceHistDescOrder = stHist.getPriceHistory().stream()
								.sorted(Comparator.comparing(HistoricalQuote::getDateVal).reversed())
								.collect(Collectors.toList());

						priceHist100Days = priceHistDescOrder.stream().limit(100).collect(Collectors.toList());
						priceHist20Days = priceHistDescOrder.stream().limit(20).collect(Collectors.toList());

						sma100 = Precision.round(priceHist100Days.stream().mapToDouble(HistoricalQuote::getClosePrice)
								.average().getAsDouble(), 0);

						sma20 = Precision.round(priceHist20Days.stream().mapToDouble(HistoricalQuote::getClosePrice)
								.average().getAsDouble(), 0);

						NFSSmaCmp smacmpVals = new NFSSmaCmp();
						smacmpVals.setScCode(stHist.getScCode());
						smacmpVals.setCmp(stHist.getCmp());
						smacmpVals.setSma50(stHist.getSma50());
						smacmpVals.setSma100(sma100);
						smacmpVals.setSma20(sma20);
						smacmpVals.setCmpSma100Delta(UtilPercentages.getPercentageDelta(sma100, stHist.getCmp(), 1));
						smacmpVals.setCmpSma20Delta(UtilPercentages.getPercentageDelta(sma20, stHist.getCmp(), 1));

						nfsContainer.getPriceTrendsDataPool().add(smacmpVals);

					}
				}
			}
		}

	}

	/*
	 * 6. Filter for Momentum Trends
	 */
	private void filterforMomentum()
	{
		// TODO Auto-generated method stub

		if (nfsContainer.getPriceTrendsDataPool().size() > 0 && nfsConfig.getSma20DeltaIncl() > 0
				&& nfsConfig.getSma100DeltaIncl() > 0)
		{
			int sizeb4 = nfsContainer.getPriceTrendsDataPool().size();
			if (nfsContainer.getPriceTrendsDataPool()
					.removeIf(x -> x.getCmp() < x.getSma20() || x.getCmp() < x.getSma50() || x.getCmp() < x.getSma100()
							|| x.getSma20() < x.getSma50() || x.getSma20() < x.getSma100()
							|| x.getSma50() < x.getSma100() || x.getCmpSma20Delta() < nfsConfig.getSma20DeltaIncl()
							|| x.getCmpSma100Delta() < nfsConfig.getSma100DeltaIncl()
							|| x.getCmpSma20Delta() == x.getSma50() || x.getSma20() == x.getSma100()

					) == true)
			{
				nfsContainer.getNfsStats()
						.setNumSMACMP_Trends_FltOut(sizeb4 - nfsContainer.getPriceTrendsDataPool().size());

			} else
			{
				nfsContainer.getNfsStats().setNumSMACMP_Trends_FltOut(0);
			}
		}

	}

	/*
	 * 7. Calculate Consolidated Scores and Assign Ranks - Considering Price
	 * Manipulation
	 */
	private void calculateScoresandRanks() throws Exception
	{
		if (nfsContainer.getPriceTrendsDataPool().size() > 0 && nfsConfig.getWtAvgMR() > 0
				&& nfsConfig.getWt20SMADelta() > 0 && nfsConfig.getWt100SMADelta() > 0)
		{

			double t2tAllowed = Precision.round((nfsConfig.getT2tMaxPer() * nfsConfig.getPfSize()) / 100, 0);
			double t2tcount = 0;

			double maxCMPallowed = Precision.round(nfsConfig.getMaxpflotsize() / nfsConfig.getPfSize(), 0);
			for (NFSSmaCmp finalSieveScrip : nfsContainer.getPriceTrendsDataPool())
			{
				Optional<NFSConsistency> scBaseDataPoolEntityO = nfsContainer.getBaseDataPool().stream()
						.filter(w -> w.getScCode().equals(finalSieveScrip.getScCode())).findFirst();
				if (scBaseDataPoolEntityO.isPresent())
				{

					NFSConsistency nfsConsEnt = scBaseDataPoolEntityO.get();
					if (nfsConsEnt != null)
					{
						if (finalSieveScrip.getCmp() < maxCMPallowed)
						{
							NFSScores nfsScore = new NFSScores();
							nfsScore.setScCode(finalSieveScrip.getScCode());
							nfsScore.setMrScore(Precision.round(nfsConsEnt.getMonthlyRR() * nfsConfig.getWtAvgMR(), 1));
							nfsScore.setMomentumScore(
									Precision.round((finalSieveScrip.getCmpSma20Delta() * nfsConfig.getWt20SMADelta())
											+ (finalSieveScrip.getCmpSma100Delta() * nfsConfig.getWt100SMADelta()), 1));
							nfsScore.setConsolidatedScore(
									Precision.round(nfsScore.getMomentumScore() + nfsScore.getMrScore(), 1));
							nfsScore.setCmp(finalSieveScrip.getCmp());

							nfsScore.setScreenerUrl(
									nfsConfig.getScreenerpf() + nfsScore.getScCode() + nfsConfig.getScreenersf());

							if (nfsConsEnt.getMCap() > nfsConfig.getMCapLargeCap())
							{
								nfsScore.setMcapClassification(EnumMCapClassification.LargeCap);

							} else
							{
								if (nfsConsEnt.getMCap() < nfsConfig.getMCapSmallCap())
								{
									nfsScore.setMcapClassification(EnumMCapClassification.SmallCap);
								} else
								{
									nfsScore.setMcapClassification(EnumMCapClassification.MidCap);
								}
							}

							Optional<BseDataSet> scripConfigO = repoBseData.findByNsecode(nfsScore.getScCode());
							if (scripConfigO.isPresent())
							{
								BseDataSet scripConfig = scripConfigO.get();
								if (scripConfig != null)
								{
									nfsScore.setSeries(scripConfig.getSeries());
								}
							}

							if (!this.filterforPriceManipulation(nfsScore.getScCode()))
							{
								if (nfsScore.getSeries().equals("BE")) // T2T Scrip
								{
									if (t2tcount < t2tAllowed)
									{
										nfsContainer.getFinalSieveScores().add(nfsScore);
										t2tcount++;
									}
								} else
								{
									nfsContainer.getFinalSieveScores().add(nfsScore);
								}

							} else
							{
								nfsContainer.getNfsStats().setPriceManipulationFltOut(
										nfsContainer.getNfsStats().getPriceManipulationFltOut() + 1);
							}
						}

						else
						{
							nfsContainer.getNfsStats().setCMPFltOut(nfsContainer.getNfsStats().getCMPFltOut() + 1);
						}
					}

				}
			}

			/*
			 * Sort Final Sieve Scores by Consolidated Score Descending and Assign the Ranks
			 * accordingly
			 */
			List<NFSScores> finScoresSorted = nfsContainer.getFinalSieveScores().stream()
					.sorted(Comparator.comparingDouble(NFSScores::getConsolidatedScore).reversed())
					.collect(Collectors.toList());
			if (finScoresSorted.size() > 0)
			{
				int y = 1;
				for (NFSScores nfsScores : finScoresSorted)
				{
					nfsScores.setRankCurr(y);
					y++;
				}
			}

			/**
			 * Rest the Sorted List
			 */
			nfsContainer.getFinalSieveScores().clear();
			nfsContainer.setFinalSieveScores(finScoresSorted);
			nfsContainer.getNfsStats().setNumFinalScrips(finScoresSorted.size());

		}

	}

	/*
	 * 8. Filter for Scrip Price Manipulation
	 */
	private boolean filterforPriceManipulation(String scCode) throws Exception
	{
		boolean isExited = false;

		int numBreaches = 0;
		int numtopnBreaches = 0;
		int numNeg3PerOcc = 0;
		// 1. Get Historical Quotes for last 10 Working Days for final Sieve Scrips -ssh
		// Not final yet!!!

		NFSStockHistoricalQuote stHistQuotes = StockPricesUtility.getHistoricalPricesforScrips(scCode, Calendar.MONTH,
				1, Interval.DAILY);
		if (stHistQuotes != null)
		{
			List<yahoofinance.histquotes.HistoricalQuote> listHQ = null;
			List<yahoofinance.histquotes.HistoricalQuote> top7listHQ = null;
			listHQ = stHistQuotes.getQuotesH().stream()
					.sorted(Comparator.comparing(yahoofinance.histquotes.HistoricalQuote::getDate).reversed())
					.collect(Collectors.toList());

			top7listHQ = listHQ.stream().limit(7).collect(Collectors.toList());

			List<NFSPriceManipulationItems> pmItems = new ArrayList<NFSPriceManipulationItems>();
			if (top7listHQ != null)
			{
				if (top7listHQ.size() > 0)
				{

					for (yahoofinance.histquotes.HistoricalQuote quote : top7listHQ)
					{

						NFSPriceManipulationItems pMItem = new NFSPriceManipulationItems(quote.getDate().getTime(),
								quote.getOpen(), quote.getHigh(), quote.getLow(), quote.getAdjClose(),
								new BigDecimal(0), new BigDecimal(0));

						pMItem.setDeltaHL(new BigDecimal(UtilPercentages
								.getPercentageDelta(pMItem.getLow().doubleValue(), pMItem.getHigh().doubleValue(), 0)));

						pMItem.setDeltaOC(new BigDecimal(UtilPercentages.getPercentageDelta(
								pMItem.getOpen().doubleValue(), pMItem.getClose().doubleValue(), 0)));

						pmItems.add(pMItem);

					}
				}
			}

			int x = 0;
			// 2. We have the Items now sorted by Descending Dates
			for (NFSPriceManipulationItems pMItem : pmItems)
			{
				if ((pMItem.getDeltaHL().doubleValue() > 5 && pMItem.getDeltaOC().doubleValue() <= 0)
						|| (pMItem.getDeltaHL().doubleValue() == 0 && pMItem.getDeltaOC().doubleValue() == 0)

				)
				{
					if (pMItem.getDeltaOC().doubleValue() <= -3)
					{
						numNeg3PerOcc += 1;
					}

					numBreaches += 1;
					if (x < 2)
					{
						numtopnBreaches += 1;
					}

					if (numtopnBreaches == 2)
					{
						NFSPriceManipulation nfsPM = new NFSPriceManipulation();
						nfsPM.setScCode(scCode);
						nfsPM.setPriceItems(pmItems);
						nfsContainer.getManipulatedScrips().add(nfsPM);
						return true;
					}
					if (numBreaches == 3)
					{
						if (numNeg3PerOcc > 3)
						{
							NFSPriceManipulation nfsPM = new NFSPriceManipulation();
							nfsPM.setScCode(scCode);
							nfsPM.setPriceItems(pmItems);
							nfsContainer.getManipulatedScrips().add(nfsPM);
							return true;
						}
					}
					if (numBreaches > 3)
					{
						NFSPriceManipulation nfsPM = new NFSPriceManipulation();
						nfsPM.setScCode(scCode);
						nfsPM.setPriceItems(pmItems);
						nfsContainer.getManipulatedScrips().add(nfsPM);
						return true;
					}
				}

				x++;
			}
		}

		return isExited;
	}

	/**
	 * RUN Rank Scan - Compartmentalize Scrips to Exit
	 * 
	 * @throws Exception
	 */

	private boolean runRankScan(double incrementalInvestment, boolean updateDb) throws Exception
	{
		boolean createPF = false;
		if (repoNFSPF != null && repoNFSTmp != null)
		{
			// Get Holdings

			List<NFSPF> holdings = repoNFSPF.findAll();
			if (holdings.size() > 0)
			{
				// Get Per Holding Investment Size if any new incremental capital is
				// involved/otherwise
				double perPosInv = getPerPositionInvestment(incrementalInvestment);

				/*
				 * Set in Container for Replacements Seek out
				 */
				this.nfsContainer.setPerPosInvestment(perPosInv);

				// Get current Proposals
				List<NFSRunTmp> currProposals = repoNFSTmp.findAll();
				if (currProposals.size() > 0)
				{
					for (NFSPF holding : holdings)
					{
						Optional<NFSRunTmp> proposal4mCurrHolding = currProposals.stream()
								.filter(x -> x.getSccode().equals(holding.getSccode())).findFirst();
						if (proposal4mCurrHolding.isPresent()) // Update or Delete Based on Rank and Rank Fail SMA check
						{

							// Scan the Current Rank from Proposal for Current Holding
							if (proposal4mCurrHolding.get().getRank() > nfsConfig.getNfsSlotMax())
							{
								/*
								 * Rank Slipped than Max Allowed (> Max; e.g. 30) check for SMA configured (e.g.
								 * 45 - Rank Fail SMA)
								 */

								NFSExitSMADelta smaRankFailDelta = StockPricesUtility.getDeltaSMAforDaysfromCMP(
										holding.getSccode(), nfsConfig.getSmaExitRankFailDays());
								if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
								{
									/*
									 * Move to Replacement Container
									 */
									nfsContainer.getRC().add(holding);
								} else
								{
									/*
									 * We will still retain it - It can either improve it's rank or still maintain
									 * support above 45 SMA (configurable)
									 */
									holding.setRankcurr(proposal4mCurrHolding.get().getRank());
									update_holding(smaRankFailDelta.getCmp(), perPosInv, holding);
								}

							} else
							{
								/*
								 * Rank Intact - continue to Hold, update Rank curr, date txn, units, and price
								 * incl in Buffer
								 */

								Stock stock = YahooFinance.get(holding.getSccode() + ".NS");
								if (stock != null)
								{
									double cmp = Precision.round(stock.getQuote().getPrice().doubleValue(), 2);
									holding.setRankcurr(proposal4mCurrHolding.get().getRank());
									update_holding(cmp, perPosInv, holding);
								}

							}

						} else // Go for SMA Slag Limit Check to retain/hold
						{

							NFSExitSMADelta smaRankFailDelta = StockPricesUtility
									.getDeltaSMAforDaysfromCMP(holding.getSccode(), nfsConfig.getSmaExitDays());
							if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
							{
								/*
								 * Move to Replacement Container
								 */
								nfsContainer.getRC().add(holding);
							} else
							{
								/*
								 * We will still retain it - It can either improve it's rank or still maintain
								 * support above 35 SMA (configurable)
								 */
								if (proposal4mCurrHolding.isPresent())
								{
									holding.setRankcurr(proposal4mCurrHolding.get().getRank());
								} else
								{
									holding.setRankcurr(nfsConfig.getNfsSlotMax());
								}
								update_holding(smaRankFailDelta.getCmp(), perPosInv, holding);
							}

						}
					}
				}
			} else
			{
				create_pf(incrementalInvestment, updateDb); // In Case of No Holdings
				createPF = true;
			}
		}

		return createPF;
	}

	/**
	 * RUN Rank Scan - Compartmentalize Scrips to Exit
	 * 
	 * @throws Exception
	 */

	private boolean runRankScanUI(double incrementalInvestment, boolean updateDb) throws Exception
	{
		boolean createPF = false;
		if (repoNFSPF != null && repoNFSTmp != null)
		{
			// Get Holdings

			List<NFSPF> holdings = repoNFSPF.findAll();
			if (holdings.size() > 0)
			{

				// Get current Proposals
				List<NFSRunTmp> currProposals = repoNFSTmp.findAll();
				if (currProposals.size() > 0)
				{
					for (NFSPF holding : holdings)
					{
						Optional<NFSRunTmp> proposal4mCurrHolding = currProposals.stream()
								.filter(x -> x.getSccode().equals(holding.getSccode())).findFirst();
						if (proposal4mCurrHolding.isPresent()) // Update or Delete Based on Rank and Rank Fail SMA check
						{

							if (scPriceMode.getScpricesDBMode() == 1)
							{
								Stock stock = StockPricesUtility.getQuoteforScrip(holding.getSccode());
								if (stock != null)
								{
									NFSExitSMADelta smaRankFailDelta = new NFSExitSMADelta(
											stock.getQuote().getPrice().doubleValue(),
											stock.getQuote().getPriceAvg50().doubleValue(),
											stock.getQuote().getChangeFromAvg50InPercent().doubleValue() * -1);

									if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
									{
										/*
										 * Move to Replacement Container
										 */
										nfsContainer.getRC().add(holding);
									}
								}
							} else
							{

								// Scan the Current Rank from Proposal for Current Holding
								if (proposal4mCurrHolding.get().getRank() > nfsConfig.getNfsSlotMax())
								{
									/*
									 * Rank Slipped than Max Allowed (> Max; e.g. 30) check for SMA configured (e.g.
									 * 45 - Rank Fail SMA)
									 */

									NFSExitSMADelta smaRankFailDelta = StockPricesUtility.getDeltaSMAforDaysfromCMP(
											holding.getSccode(), nfsConfig.getSmaExitRankFailDays());
									if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
									{
										/*
										 * Move to Replacement Container
										 */
										nfsContainer.getRC().add(holding);
									}

								}
							}

						} else // Go for SMA Slag Limit Check to retain/hold
						{
							if (scPriceMode.getScpricesDBMode() == 1)
							{
								Stock stock = StockPricesUtility.getQuoteforScrip(holding.getSccode());
								if (stock != null)
								{
									NFSExitSMADelta smaRankFailDelta = new NFSExitSMADelta(
											stock.getQuote().getPrice().doubleValue(),
											stock.getQuote().getPriceAvg50().doubleValue(),
											stock.getQuote().getChangeFromAvg50InPercent().doubleValue() * -1);

									if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
									{
										/*
										 * Move to Replacement Container
										 */
										nfsContainer.getRC().add(holding);
									}
								}
							} else
							{

								NFSExitSMADelta smaRankFailDelta = StockPricesUtility
										.getDeltaSMAforDaysfromCMP(holding.getSccode(), nfsConfig.getSmaExitDays());
								if (smaRankFailDelta.getDelta() > 0) // Delta of SMA w.r.t CMP should be -ve : good
								{
									/*
									 * Move to Replacement Container
									 */
									nfsContainer.getRC().add(holding);
								}
							}
						}
					}
				}
			} else
			{
				create_pf(incrementalInvestment, updateDb); // In Case of No Holdings
				createPF = true;
			}
		}

		return createPF;
	}

	/*
	 * Update Holding using CMP, Per Position allowable Investment and Current
	 * Holding Entity
	 */
	private void update_holding(double cmp, double perPosInv, NFSPF holding)
	{
		double currInv = holding.getUnits() * holding.getPriceincl();
		double deltaInv = perPosInv - currInv;

		if (deltaInv > 0)
		{
			int incUnits = (int) Precision.round((deltaInv / cmp), 0);
			double ppu = Precision.round((currInv + (incUnits * cmp)) / (holding.getUnits() + incUnits), 2);

			NFSPF updatedHolding = new NFSPF();
			updatedHolding.setDateincl(holding.getDateincl());

			long millis = System.currentTimeMillis();
			java.util.Date dateToday = new java.util.Date(millis);

			updatedHolding.setDatelasttxn(dateToday);
			updatedHolding.setPriceincl(ppu);
			updatedHolding.setRankcurr(holding.getRankcurr());
			updatedHolding.setUnits(holding.getUnits() + incUnits);
			updatedHolding.setSccode(holding.getSccode());
			updatedHolding.setRankincl(holding.getRankincl());

			nfsContainer.setCashTxnBalance(nfsContainer.getCashTxnBalance() + deltaInv - (cmp * incUnits));

			nfsContainer.getNFSPortfolio().add(updatedHolding);

		}

		else
		{
			/// Rank current Already Updated - Just Append the holding
			nfsContainer.getNFSPortfolio().add(holding);

		}

	}

	/*
	 * Update Holding using CMP, Per Position allowable Investment and Current
	 * Holding Entity
	 */
	private NFSPF get_ReplacementHolding(double cmp, double perPosInv, NFSPF holding)
	{
		NFSPF updatedHolding = new NFSPF();

		int units = (int) Precision.round((perPosInv / cmp), 0);
		double ppu = cmp;

		updatedHolding.setDateincl(holding.getDateincl());

		long millis = System.currentTimeMillis();
		java.util.Date dateToday = new java.util.Date(millis);

		updatedHolding.setDatelasttxn(dateToday);
		updatedHolding.setPriceincl(ppu);
		updatedHolding.setRankcurr(holding.getRankcurr());
		updatedHolding.setUnits(units);
		updatedHolding.setSccode(holding.getSccode());
		updatedHolding.setRankincl(holding.getRankincl());

		/*
		 * nfsContainer.setCashTxnBalance(nfsContainer.getCashTxnBalance() + deltaInv -
		 * (cmp * incUnits));
		 * 
		 * nfsContainer.getNFSPortfolio().add(updatedHolding);
		 */

		return updatedHolding;
	}

	/**
	 * Create a New PF based on Top 'N' holdings
	 * 
	 * @throws Exception
	 */
	private void create_pf(double incrementalInvestment, boolean updateDb) throws Exception
	{
		if (nfsContainer.getFinalSieveScores().size() > 0)
		{
			int x = 1;
			long millis = System.currentTimeMillis();
			java.util.Date dateToday = new java.util.Date(millis);

			double amountPerPosition = 0;
			int units = 0;
			double utilizedAmnt = 0;
			double perAlloc = 0;
			int loopCount = 0;

			// 1. Check and validate for Minimum Amount Needed to Create PF
			List<SectorAllocations> scAllocs = new ArrayList<SectorAllocations>();
			if (nfsContainer.getFinalSieveScores().size() > nfsConfig.getPfSize())
			{
				perAlloc = 100 / nfsConfig.getPfSize();
				amountPerPosition = incrementalInvestment / nfsConfig.getPfSize();
				loopCount = nfsConfig.getPfSize();
			} else
			{
				perAlloc = 100 / nfsContainer.getFinalSieveScores().size();
				amountPerPosition = incrementalInvestment / nfsContainer.getFinalSieveScores().size();
				loopCount = nfsContainer.getFinalSieveScores().size();
			}

			for (NFSScores nfsScore : nfsContainer.getFinalSieveScores())
			{
				scAllocs.add(new SectorAllocations(nfsScore.getScCode(), perAlloc));
			}

			double minAmnt = StockPricesUtility.getMinmAmntforPFCreation(scAllocs);

			if (minAmnt > incrementalInvestment)
			{
				// FLAG An Exception and return
				throw new Exception(msgSrc.getMessage(errMinAmnt, new Object[]
				{ incrementalInvestment, minAmnt }, Locale.ENGLISH));
			} else
			{

				for (NFSScores topn : nfsContainer.getFinalSieveScores())
				{
					if (x <= loopCount)
					{
						if (amountPerPosition > 0)
						{
							units = (int) Precision.round(amountPerPosition / topn.getCmp(), 0);
							utilizedAmnt += (units * topn.getCmp());
						}

						NFSPF newPFEntity = new NFSPF(topn.getScCode(), topn.getRankCurr(), topn.getCmp(),
								topn.getRankCurr(), dateToday, dateToday, units);

						this.nfsContainer.getNFSPortfolio().add(newPFEntity);
						if (updateDb == true)
						{
							repoNFSPF.save(newPFEntity);
						}
					}
					x++;
				}
				// Amount Left/Needed after PF Creation
				nfsContainer.setCashTxnBalance(incrementalInvestment - utilizedAmnt);
			}

		} else // Create PF from NFSTMP
		{
			createPf_NFSTmp(incrementalInvestment, updateDb);
		}

	}

	/*
	 * Create NFS PF from NFS tmp Table
	 */
	private void createPf_NFSTmp(double incrementalInvestment, boolean updateDb) throws Exception
	{
		if (repoNFSTmp != null && incrementalInvestment > 0)
		{
			if (repoNFSTmp.count() > 0)
			{
				List<NFSRunTmp> tmpProposals = repoNFSTmp.findAll();
				if (tmpProposals.size() > 0)
				{
					List<NFSRunTmp> tmpProposalsSortedbyRank = tmpProposals.stream()
							.sorted(Comparator.comparingDouble(NFSRunTmp::getConsolscore).reversed())
							.collect(Collectors.toList());
					int x = 1;
					long millis = System.currentTimeMillis();
					java.util.Date dateToday = new java.util.Date(millis);

					double amountPerPosition = 0;
					int units = 0;
					double utilizedAmnt = 0;
					double perAlloc = 0;
					int loopCount = 0;

					// 1. Check and validate for Minimum Amount Needed to Create PF
					List<SectorAllocations> scAllocs = new ArrayList<SectorAllocations>();
					if (nfsContainer.getFinalSieveScores().size() > nfsConfig.getPfSize())
					{
						perAlloc = 100 / nfsConfig.getPfSize();
						amountPerPosition = incrementalInvestment / nfsConfig.getPfSize();
						loopCount = nfsConfig.getPfSize();
					} else
					{
						perAlloc = 100 / nfsContainer.getFinalSieveScores().size();
						amountPerPosition = incrementalInvestment / tmpProposalsSortedbyRank.size();
						loopCount = tmpProposalsSortedbyRank.size();
					}

					for (NFSRunTmp nfsTmp : tmpProposalsSortedbyRank)
					{
						scAllocs.add(new SectorAllocations(nfsTmp.getSccode(), perAlloc));
					}

					double minAmnt = StockPricesUtility.getMinmAmntforPFCreation(scAllocs);

					if (minAmnt > incrementalInvestment)
					{
						// FLAG An Exception and return
						throw new Exception(msgSrc.getMessage(errMinAmnt, new Object[]
						{ incrementalInvestment, minAmnt }, Locale.ENGLISH));
					} else
					{
						// Create the PF
						for (NFSRunTmp topn : tmpProposalsSortedbyRank)
						{
							if (x <= loopCount)
							{
								double cmp = Precision.round(StockPricesUtility.getQuoteforScrip(topn.getSccode())
										.getQuote().getPrice().doubleValue(), 2);
								if (amountPerPosition > 0)
								{
									units = (int) Precision.round(amountPerPosition / cmp, 0);
									utilizedAmnt += (units * cmp);
								}

								NFSPF newPFEntity = new NFSPF(topn.getSccode(), topn.getRank(), cmp, topn.getRank(),
										dateToday, dateToday, units);

								this.nfsContainer.getNFSPortfolio().add(newPFEntity);
								if (updateDb == true)
								{
									repoNFSPF.save(newPFEntity);
								}
							}
							x++;
						}
						// Amount Left/Needed after PF Creation
						nfsContainer.setCashTxnBalance(incrementalInvestment - utilizedAmnt);
					}

				}
			}
		}
	}

	/**
	 * Get Per Position Investment size based on Current NFS PF
	 */
	private double getPerPositionInvestment(double newCapital)
	{
		double posInv = 0;
		if (repoNFSPF != null)
		{
			posInv = repoNFSPF.getTotalInvestedValue() + newCapital;

			posInv = Precision.round(posInv / nfsConfig.getPfSize(), 0);
		}

		return posInv;
	}

	/**
	 * Seek Replacements as there are some Exits Marked
	 * 
	 * @throws Exception
	 */
	private void seekReplacements() throws Exception
	{

		// Get Last Run Proposal Results and Sort by Ranks Ascending

		List<NFSRunTmp> proposals = repoNFSTmp.findAll().stream().sorted(Comparator.comparingInt(NFSRunTmp::getRank))
				.collect(Collectors.toList());

		if (proposals.size() > 0)
		{
			int numExits = this.nfsContainer.getRC().size();
			int i = 1;

			// for Each Proposal
			for (NFSRunTmp proposal : proposals)
			{
				if (i <= numExits)
				{
					// Scan in Current Holdings - PF
					Optional<NFSPF> holdingO = this.nfsContainer.getNFSPortfolio().stream()
							.filter(x -> x.getSccode().equals(proposal.getSccode())).findFirst();
					if (holdingO.isPresent())
					{
						// Move to Next Current Proposal is already In PF
					} else
					{
						// Not In - Grab it IN

						NFSPF tgHolding = new NFSPF();
						tgHolding.setSccode(proposal.getSccode());
						long millis = System.currentTimeMillis();
						java.util.Date dateToday = new java.util.Date(millis);
						tgHolding.setDateincl(dateToday);
						double cmp = Precision.round(StockPricesUtility.getQuoteforScrip(tgHolding.getSccode())
								.getQuote().getPrice().doubleValue(), 2);
						tgHolding.setPriceincl(cmp);
						tgHolding.setRankincl(proposal.getRank());
						tgHolding.setRankcurr(proposal.getRank());

						update_holding(cmp, nfsContainer.getPerPosInvestment(), tgHolding);

						// Add to Replacement Target for Stats Updation
						nfsContainer.getRT().add(tgHolding);

						i++; // REplacement Found and Added
					}
				}
			}

		}

	}

	/**
	 * Seek Replacements as there are some Exits Marked - UI Behavior
	 * 
	 * @throws Exception
	 */
	private void seekReplacementsUI() throws Exception
	{

		// Get Last Run Proposal Results and Sort by Ranks Ascending

		List<NFSRunTmp> proposals = repoNFSTmp.findAll().stream().sorted(Comparator.comparingInt(NFSRunTmp::getRank))
				.collect(Collectors.toList());

		if (proposals.size() > 0)
		{
			nfsContainer.getRT().clear();
			// for Each Proposal
			for (NFSRunTmp proposal : proposals)
			{

				// Scan in Current Holdings - PF
				Optional<NFSPF> holdingO = this.nfsContainer.getNFSPortfolio().stream()
						.filter(x -> x.getSccode().equals(proposal.getSccode())).findFirst();
				if (holdingO.isPresent())
				{
					// Move to Next Current Proposal is already In PF
				} else
				{
					// Not In - Grab it IN

					NFSPF tgHolding = new NFSPF();
					tgHolding.setSccode(proposal.getSccode());

					double cmp = Precision.round(StockPricesUtility.getQuoteforScrip(tgHolding.getSccode()).getQuote()
							.getPrice().doubleValue(), 2);
					tgHolding.setPriceincl(cmp);
					tgHolding.setRankincl(proposal.getRank());
					tgHolding.setRankcurr(proposal.getRank());

					// Add to Replacement Target for Presenting to User
					nfsContainer.getRT().add(tgHolding);

				}

			}

		}

	}

	/**
	 * Get Unrealized P&L
	 * 
	 * @return - Amount of Unrealized P&L
	 * @throws Exception
	 */
	private double getUnrealizedPL() throws Exception
	{
		double unrealPl = 0;
		double sumdeltas = 0;

		double perscCont = 100 / nfsConfig.getPfSize();
		for (NFSPF holding : nfsContainer.getNFSPortfolio())
		{

			// Ignore all replacements

			Optional<NFSPF> isReplacedHolding = nfsContainer.getRT().stream()
					.filter(x -> x.getSccode().equals(holding.getSccode())).findFirst();

			if (isReplacedHolding.isPresent())
			{
				// Ignore this one and move to next
			} else
			{
				// Will Contribute to Unrealized Gains
				// Get CMp
				double cmp = StockPricesUtility.getQuoteforScrip(holding.getSccode()).getQuote().getPrice()
						.doubleValue();
				sumdeltas += (((UtilPercentages.getPercentageDelta(holding.getPriceincl(), cmp, 1)) * perscCont) / 100);

			}

		}

		unrealPl = sumdeltas;

		return unrealPl;

	}

	/**
	 * Get Realized P&L percentage and Absolute amount
	 * 
	 * @return
	 * @throws Exception
	 */
	private NFSRealizations getRealizedPL() throws Exception
	{
		NFSRealizations realzPl = new NFSRealizations();
		double sumdeltas = 0;
		double sumrealAmnt = 0;
		double sumContributions = 0;

		for (NFSPF exit : nfsContainer.getRC())
		{
			// Will Contribute to Realized Gains/Losses
			// Get CMp
			double cmp = StockPricesUtility.getQuoteforScrip(exit.getSccode()).getQuote().getPrice().doubleValue();

			sumdeltas += UtilPercentages.getPercentageDelta(exit.getPriceincl(), cmp, 1);

			sumrealAmnt += exit.getUnits() * (cmp - exit.getPriceincl());

			// For PErcentage PF contribution
			sumContributions += exit.getPriceincl() * exit.getUnits();

		}

		// Get % Contributions w.r.t Ideal Allocations

		double perExposureinExits = sumContributions
				/ (nfsContainer.getPerPosInvestment() * nfsContainer.getRC().size());

		double perscCont = 100 / nfsConfig.getPfSize();

		double realPl = (sumdeltas * perscCont * nfsContainer.getRC().size() * perExposureinExits) / 100;

		realzPl.setRealzAmount(sumrealAmnt);
		realzPl.setRealzPercent(realPl);

		// Also Update the Cash Txn Balance with Realized P&L
		nfsContainer.setCashTxnBalance(Precision.round(nfsContainer.getCashTxnBalance() + sumrealAmnt, 0));

		return realzPl;
	}

	/**
	 * Get a String Concatenated Representation of all the Exits
	 * 
	 * @return
	 */
	private String getExits()
	{
		String exits = new String();
		for (NFSPF exit : nfsContainer.getRC())
		{
			exits += exit.getSccode() + " | ";
		}

		return exits;
	}

	/**
	 * Get a String Concatenated Representation of all the Entries
	 * 
	 * @return
	 */
	private String getNewEntries()
	{
		String entries = new String();
		for (NFSPF entry : nfsContainer.getRT())
		{
			entries += entry.getSccode() + " | ";
		}

		return entries;
	}

	/**
	 * Update the NFS Trade Journal with Rebalancing
	 * 
	 * @throws Exception
	 */
	private void updateJournal() throws Exception
	{
		nfsContainer.setNFSJEntity(new NFSJournal());

		long millis = System.currentTimeMillis();
		java.util.Date dateToday = new java.util.Date(millis);

		nfsContainer.getNFSJEntity().setDate(dateToday);
		if (nfsContainer.getRC().size() > 0)
		{
			NFSRealizations realPL = getRealizedPL();
			nfsContainer.getNFSJEntity().setEntries(getNewEntries());
			nfsContainer.getNFSJEntity().setExits(getExits());
			nfsContainer.getNFSJEntity().setNumexits(nfsContainer.getRC().size());
			nfsContainer.getNFSJEntity().setPerchurn(Precision.round(
					(nfsContainer.getNFSJEntity().getNumexits() * 100 / nfsContainer.getNFSPortfolio().size()), 1));
			nfsContainer.getNFSJEntity().setRealpl(Precision.round(realPL.getRealzPercent(), 2));
			nfsContainer.getNFSJEntity().setRealplamnt(Precision.round(realPL.getRealzAmount(), 0));
		}
		nfsContainer.getNFSJEntity().setNumscrips(nfsContainer.getNFSPortfolio().size());

		nfsContainer.getNFSJEntity().setUnrealpl(Precision.round(getUnrealizedPL(), 2));
	}

	private void saveRebalance()
	{
		// first Update the NFS PF
		if (nfsContainer.getNFSPortfolio().size() > 0)
		{
			repoNFSPF.deleteAll(); // Clear PF Db Table

			for (NFSPF holding : nfsContainer.getNFSPortfolio())
			{
				repoNFSPF.save(holding);
			}
		}

		// Update the Journal
		if (nfsContainer.getNFSJEntity() != null)
		{
			repoNFSJ.save(nfsContainer.getNFSJEntity());
		}

	}

	private String getScripsListasString(List<String> scrips)
	{
		String concat = new String();
		for (String scrip : scrips)
		{
			concat += scrip + " | ";
		}

		return concat;
	}

}
