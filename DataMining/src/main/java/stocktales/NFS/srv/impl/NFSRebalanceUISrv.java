package stocktales.NFS.srv.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumNFSTxnType;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.entity.NFSJournal;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.entity.NFSRunTmp;
import stocktales.NFS.model.pojo.NFSCB_IP;
import stocktales.NFS.model.pojo.NFSExitOnly;
import stocktales.NFS.model.pojo.StockUnitsPPU;
import stocktales.NFS.model.ui.NFSRebalStats;
import stocktales.NFS.repo.RepoNFSCashBook;
import stocktales.NFS.repo.RepoNFSJornal;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.repo.RepoNFSTmp;
import stocktales.NFS.srv.intf.INFSProcessor;
import stocktales.NFS.srv.intf.INFSRebalanceUISrv;
import stocktales.NFS.srv.intf.INFS_CashBookSrv;
import stocktales.durations.UtilDurations;
import stocktales.historicalPrices.pojo.StockCurrQuote;
import stocktales.historicalPrices.utility.StockPricesUtility;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class NFSRebalanceUISrv implements INFSRebalanceUISrv
{

	@Autowired
	private RepoNFSPF repoNFSPF;

	@Autowired
	private RepoNFSTmp repoNFSTmp;

	@Autowired
	private RepoNFSJornal repoNFSJournal;

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private INFS_CashBookSrv nfsCBSrv;;

	@Autowired
	private INFSProcessor nfsProcSrv;

	@Autowired
	private RepoNFSJornal repoNFSJ;

	@Autowired
	private RepoNFSCashBook repoNFSCB;

	private NFSRebalStats rebalStats;
	private List<NFSPF> portfolio = new ArrayList<NFSPF>();
	private NFSJournal nfsJ;

	@Override
	public void processRebalance(double incrementalAmnt, List<String> exits, List<String> entries) throws Exception
	{
		/*
		 * -------------- Re-balance Supports ------------------------------ Either an
		 * Incremental Investment has to be done to existing pf OR Either an exit -
		 * entry has to be coupled OR A new Entry with Incremental investment must come
		 * in -----------------------------------------------------------------
		 */

		clear();

		if (!scanforRiskandProcess(incrementalAmnt, exits, entries))
		{

			if (incrementalAmnt > 0 || (exits != null && entries != null) || (incrementalAmnt > 0 && entries != null))
			{
				/**
				 * No Risks - Adjust Cash Flow for Incremental Adjustments
				 */
				if (incrementalAmnt > 0)
				{
					NFSCB_IP nfscbIP = new NFSCB_IP(EnumNFSTxnType.Deploy, incrementalAmnt);
					nfsCBSrv.processCBTxn(nfscbIP);
				}

				// do Basic Numbers Around Re-balance - TAD, PPI, ARC etc
				doBasicNumbers(incrementalAmnt, exits, entries);

				// process current PF holdings for Incremental Investments
				processCurrHoldingsForIncrementalPurchases(incrementalAmnt, exits);

				// process new Inclusions if Any
				processNewInclusions(entries);

				// Create Journal Entity
				createJournallLog(exits, entries);

				// Persist PF and Journal
				presistRebalance();
			}
		}

	}

	@Transactional
	private boolean scanforRiskandProcess(double incrementalAmnt, List<String> exits, List<String> entries)
			throws Exception
	{
		boolean riskDetected = false;

		/**
		 * ONLY EXITS - No Entries
		 */
		if (entries == null && exits.size() > 0)
		{
			riskDetected = true;
			List<NFSExitOnly> exitsData = processExitsOnly(exits);
			if (exitsData != null)
			{
				if (exitsData.size() > 0)
				{
					// Get Sum Cash Val (SCV)
					double scv = exitsData.stream().mapToDouble(NFSExitOnly::getCurrVal).sum();
					// Get Sum P&L (SPL)
					double spl = exitsData.stream().mapToDouble(NFSExitOnly::getPL).sum();
					// Get Sum Invested Value (SIV)
					double siv = exitsData.stream().mapToDouble(NFSExitOnly::getInvAmnt).sum();

					// PRepare NFSCashBook Entry and Persist

					NFSCB_IP nfscbIP = new NFSCB_IP(EnumNFSTxnType.SalePartial, scv);
					nfsCBSrv.processCBTxn(nfscbIP);

					// Prepare SCJournal and PErsist
					NFSJournal nfsJ = getJournalLogforOnlyExits(exits, spl, siv);
					if (nfsJ != null)
					{
						repoNFSJournal.save(nfsJ);
					}

					// Record Each Exit Entry in NFSExitBook

				}
			}
		}

		/**
		 * Entries less than Exits
		 */
		if (entries.size() < exits.size())
		{
			riskDetected = true;

			/**
			 * Scan for Complete Exit - Curr PF Size + Entries - Exits is less than 60%
			 * ideal PF SIZE
			 */
			if ((repoNFSPF.count() + entries.size() - exits.size()) < nfsConfig.getPfSize() * .6)
			{
				// Exit All
				nfsProcSrv.exitPortfolio();

				// Process Entries in NFSExit Book
			}

			/**
			 * PF Should Exist
			 */

			else
			{
				// Process entries for Exits Only
				int i = 0;
				double sumpl = 0;
				double sumRepl = 0;
				double sumCurrVal = 0;
				List<String> exitsProc = new ArrayList<String>();
				int numScripsB4 = (int) repoNFSPF.count();
				double totalInvValue = repoNFSPF.getTotalInvestedValue();

				/**
				 * REplace Same Number of Exits by Entries
				 */
				for (String entry : entries)
				{
					NFSPF scExitH = repoNFSPF.findBySccode(exits.get(i)).get();
					if (scExitH != null)
					{
						double amntCurr = scExitH.getUnits() * StockPricesUtility.getQuoteforScrip(scExitH.getSccode())
								.getQuote().getPrice().doubleValue();

						double cmp = Precision.round(
								StockPricesUtility.getQuoteforScrip(entry).getQuote().getPrice().doubleValue(), 2);
						int unitsBuy = (int) (amntCurr / cmp);
						sumRepl += (cmp * unitsBuy);

						// Insert Entry
						repoNFSPF.save(new NFSPF(entry, scExitH.getRankcurr(), cmp, scExitH.getRankcurr(),
								UtilDurations.getTodaysDateOnly(), UtilDurations.getTodaysDateOnly(), unitsBuy));

					}
				}

				for (String exit : exits)
				{
					NFSPF scExitH = repoNFSPF.findBySccode(exits.get(i)).get();
					if (scExitH != null)
					{
						double amntCurr = scExitH.getUnits() * StockPricesUtility.getQuoteforScrip(scExitH.getSccode())
								.getQuote().getPrice().doubleValue();
						sumCurrVal += amntCurr;
						sumpl += amntCurr - (scExitH.getPriceincl() * scExitH.getUnits());
						exitsProc.add(exit);
						// Delete Exit Scrip from PF
						repoNFSPF.delete(scExitH);
						i++;
					}

					// Record in NFSExitBook

				}

				/**
				 * Create CashBook Entry for Current Sale Value
				 */
				NFSCB_IP nfscbIP = new NFSCB_IP(EnumNFSTxnType.SalePartial, sumCurrVal);
				nfsCBSrv.processCBTxn(nfscbIP);

				/**
				 * Save Journal Entry
				 */
				NFSJournal nfsJ = new NFSJournal();
				nfsJ.setDate(UtilDurations.getTodaysDateOnly());
				nfsJ.setEntries(this.getScripsListasString(entries));
				nfsJ.setExits(this.getScripsListasString(exits));
				nfsJ.setNumexits(exits.size());
				nfsJ.setNumscrips(numScripsB4 + entries.size() - exits.size());
				nfsJ.setPerchurn(((entries.size() + exits.size()) * 100) / nfsJ.getNumscrips());
				nfsJ.setRealpl(sumpl);
				nfsJ.setRealplamnt((sumpl * 100) / totalInvValue);
				nfsJ.setUnrealpl(0);

				repoNFSJ.save(nfsJ);

			}
		}

		return riskDetected;

	}

	/**
	 * ------------------------ PRIVATE SECTION -------------------
	 * 
	 */

	/*
	 * 1. DO Basic Numbers
	 */
	private void doBasicNumbers(double incrementalAmnt, List<String> exits, List<String> entries) throws Exception
	{
		rebalStats = new NFSRebalStats();
		if (repoNFSPF != null)
		{
			rebalStats.setNumCPS((int) repoNFSPF.count());
			if (entries != null)
			{
				rebalStats.setNumRT(entries.size());
			}

			if (exits != null)
			{
				rebalStats.setNumRC(exits.size());
			}

			rebalStats.setTotalScrips(rebalStats.getNumCPS() - rebalStats.getNumRC() + rebalStats.getNumRT());
			if (rebalStats.getTotalScrips() > nfsConfig.getPfSize())
			{
				throw new Exception("Total Scrips selected for Portfolio - " + rebalStats.getTotalScrips()
						+ "should be less than " + nfsConfig.getPfSize());
			}

			// Determine TAD & PPI
			getTAD(exits, incrementalAmnt);

		}

	}

	/*
	 * Clear Service Buffer
	 */
	private void clear()
	{
		this.rebalStats = new NFSRebalStats();
		this.portfolio.clear();
		this.nfsJ = new NFSJournal();
	}

	/*
	 * Calculate Total Amount at Disposal that needs to be distributed and Invested
	 */
	private void getTAD(List<String> exits, double incAmnt) throws Exception
	{

		// Get Selling Realizations

		double nettRealz = 0;
		if (exits != null)
		{
			if (exits.size() > 0)
			{

				for (String scCodeExit : exits)
				{
					Optional<NFSPF> exitHolding = repoNFSPF.findBySccode(scCodeExit);
					if (exitHolding.isPresent())
					{
						// Get Realization by selling this holding
						double cmpHolding = StockPricesUtility.getQuoteforScrip(scCodeExit).getQuote().getPrice()
								.doubleValue();
						double realz = (cmpHolding - exitHolding.get().getPriceincl()) * exitHolding.get().getUnits();

						nettRealz += realz;
					}
				}
				rebalStats.setArc(nettRealz);
			}
		}

		rebalStats.setTad(incAmnt + nettRealz + repoNFSPF.getTotalInvestedValue());
		rebalStats.setPpi(rebalStats.getTad() / rebalStats.getTotalScrips());
	}

	/*
	 * PROCESS for Current PF Holdings for Incremental Investment
	 */
	private void processCurrHoldingsForIncrementalPurchases(double incrementalAmnt, List<String> exits) throws Exception
	{
		List<NFSPF> currPF = repoNFSPF.findAll();

		boolean anyExits = false;
		boolean include = true;

		if (exits != null)
		{
			if (exits.size() > 0)
			{
				anyExits = true;
			}
		}

		for (NFSPF holding : currPF)
		{
			if (anyExits)
			{
				Optional<String> exitO = exits.stream().filter(x -> x.equals(holding.getSccode())).findFirst();
				if (exitO.isPresent())
				{
					include = false;
				}
			}

			if (include)
			{
				// Calculate Incremental Investment Possible
				double incInvHolding = rebalStats.getPpi() - (holding.getPriceincl() * holding.getUnits());
				if (incInvHolding > 0)
				{
					// Need to Invest More

					// Get Units , PPU and AMounts after Purchase
					StockUnitsPPU unitsPPU = StockPricesUtility.getTotalUnitsPPU(holding.getSccode(),
							holding.getUnits(), holding.getPriceincl(), incInvHolding);

					NFSPF updHolding = new NFSPF();
					updHolding.setSccode(holding.getSccode());
					updHolding.setDateincl(holding.getDateincl());
					long millis = System.currentTimeMillis();
					java.util.Date dateToday = new java.util.Date(millis);

					updHolding.setDatelasttxn(dateToday);
					updHolding.setPriceincl(unitsPPU.getPpu());
					updHolding.setUnits(unitsPPU.getUnits());

					Optional<NFSRunTmp> proposalO = repoNFSTmp.findBySccode(holding.getSccode());
					if (proposalO.isPresent())
					{
						updHolding.setRankcurr(proposalO.get().getRank());
					} else
					{
						updHolding.setRankcurr(nfsConfig.getPfSize()); /// Last Max Rank - Not in Proposals
					}

					updHolding.setRankincl(holding.getRankincl());

					this.portfolio.add(updHolding);

				} else
				{
					/*
					 * Update current Holding to PF and adjust running balance
					 * 
					 */
					this.portfolio.add(holding);

				}
			}

			include = true; // Re-Evaluate for each Holding
		}

	}

	/*
	 * PROCESS NEw Inclusions
	 */
	private void processNewInclusions(List<String> entries) throws Exception
	{
		if (entries != null)
		{
			if (entries.size() > 0)
			{
				// Get Total amount deployed till Now
				double amntInvCurrPF = portfolio.stream().mapToDouble(x -> x.getUnits() * x.getPriceincl()).sum();

				rebalStats.setRtia(rebalStats.getTad() - amntInvCurrPF);

				rebalStats.setPprt(rebalStats.getRtia() / entries.size());

				for (String newEntry : entries)
				{
					StockUnitsPPU unitsPPU = StockPricesUtility.getTotalUnitsPPU(newEntry, 0, 0, rebalStats.getPprt());
					if (unitsPPU != null)
					{
						if (unitsPPU.getUnits() > 0)
						{
							NFSPF updHolding = new NFSPF();
							updHolding.setSccode(newEntry);

							long millis = System.currentTimeMillis();
							java.util.Date dateToday = new java.util.Date(millis);

							updHolding.setDateincl(dateToday);
							updHolding.setDatelasttxn(dateToday);
							updHolding.setPriceincl(unitsPPU.getPpu());
							updHolding.setUnits(unitsPPU.getUnits());

							Optional<NFSRunTmp> proposalO = repoNFSTmp.findBySccode(newEntry);
							if (proposalO.isPresent())
							{
								updHolding.setRankcurr(proposalO.get().getRank());
							} else
							{
								updHolding.setRankcurr(nfsConfig.getPfSize()); /// Last Max Rank - Not in Proposals
							}

							updHolding.setRankincl(updHolding.getRankcurr());

							this.portfolio.add(updHolding);
						}

					}
				}

			}
		}

	}

	/*
	 * Create Journal Log
	 */
	private void createJournallLog(List<String> exits, List<String> entries) throws Exception
	{

		long millis = System.currentTimeMillis();
		java.util.Date dateToday = new java.util.Date(millis);

		nfsJ.setDate(dateToday);
		if (entries != null)
		{
			if (entries.size() > 0)
			{
				// NFSRealizations realPL = getRealizedPL();
				nfsJ.setEntries(getScripsListasString(entries));

			}
		}

		if (exits != null)
		{
			if (exits.size() > 0)
			{
				// NFSRealizations realPL = getRealizedPL();
				nfsJ.setExits(getScripsListasString(exits));
				nfsJ.setNumexits(exits.size());
				nfsJ.setPerchurn(Precision
						.round(((rebalStats.getNumRC() + rebalStats.getNumRT()) * 100 / rebalStats.getNumCPS()), 1));
				nfsJ.setRealplamnt(Precision.round(rebalStats.getArc(), 0));
				nfsJ.setRealpl(Precision.round((rebalStats.getArc() * 100) / repoNFSPF.getTotalInvestedValue(), 2));

			}
		}

		nfsJ.setNumscrips(rebalStats.getTotalScrips());

		// Get Current PF < ScripsCode, Units> List Removing Exits
		List<NFSPF> scUnits = repoNFSPF.findAll();
		if (scUnits != null)
		{
			if (scUnits.size() > 0)
			{
				if (rebalStats.getNumRC() > 0)
				{
					for (String exit : exits)
					{
						scUnits.removeIf(x -> x.getSccode().equals(exit));
					}
				}
			}
		}

		double invVal = repoNFSPF.getTotalInvestedValue();
		if (scUnits.size() > 0)
		{
			if (rebalStats.getNumRC() > 0)
			{
				for (String exit : exits)
				{
					Optional<NFSPF> exitHoldingO = repoNFSPF.findBySccode(exit);
					if (exitHoldingO.isPresent())
					{
						invVal = invVal - (exitHoldingO.get().getPriceincl() * exitHoldingO.get().getUnits());
					}
				}
			}
		}

		nfsJ.setUnrealpl(Precision
				.round((((StockPricesUtility.getCurrentValueforScripsPFCmpl(scUnits) - invVal) * 100) / invVal), 2));

	}

	private NFSJournal getJournalLogforOnlyExits(List<String> exits, double spl, double siv) throws Exception
	{
		NFSJournal nfsJ = new NFSJournal();
		nfsJ.setDate(UtilDurations.getTodaysDate());
		nfsJ.setNumscrips((int) repoNFSPF.count() - exits.size());
		nfsJ.setExits(getScripsListasString(exits));
		nfsJ.setNumexits(exits.size());
		nfsJ.setPerchurn(Precision.round(((exits.size() / (int) repoNFSPF.count())), 1));
		nfsJ.setRealplamnt(Precision.round(spl, 0));
		nfsJ.setRealpl(Precision.round((spl * 100) / repoNFSPF.getTotalInvestedValue(), 2));

		// Get Current PF < ScripsCode, Units> List Removing Exits
		List<NFSPF> scUnits = repoNFSPF.findAll();
		if (scUnits != null)
		{
			if (scUnits.size() > 0)
			{

				for (String exit : exits)
				{
					scUnits.removeIf(x -> x.getSccode().equals(exit));
				}

			}
		}

		double invVal = repoNFSPF.getTotalInvestedValue();
		invVal = invVal - siv; // Take Out Exits Invested Value

		nfsJ.setUnrealpl(Precision
				.round((((StockPricesUtility.getCurrentValueforScripsPFCmpl(scUnits) - invVal) * 100) / invVal), 2));

		return nfsJ;
	}

	/*
	 * Persists Re-balance- PF and Journal
	 */
	private void presistRebalance() throws Exception
	{

		/**
		 * After Cash Book Entry - Follow the Downstream Process of PF adjustments
		 */

		// Clear current PF
		repoNFSPF.deleteAll();

		// Save Current Re-balanced PF
		for (NFSPF nfspf : portfolio)
		{
			repoNFSPF.save(nfspf);
		}

		// Save Journal Entry
		repoNFSJournal.save(nfsJ);

	}

	/*
	 * GET EXITS ONLY Details
	 */

	private List<NFSExitOnly> processExitsOnly(List<String> exits) throws Exception
	{
		List<NFSExitOnly> exitsEval = new ArrayList<NFSExitOnly>();
		if (exits.size() > 0)
		{
			List<StockCurrQuote> exitsCMP = StockPricesUtility.getCurrentPricesforScrips(exits);
			if (exitsCMP != null)
			{
				for (String exitSc : exits)
				{
					// Get Current Holding Details
					Optional<NFSPF> exitHO = repoNFSPF.findBySccode(exitSc);
					if (exitHO.isPresent())
					{
						NFSExitOnly exitPos = new NFSExitOnly();
						exitPos.setScCode(exitSc);
						exitPos.setInvAmnt(exitHO.get().getPriceincl() * exitHO.get().getUnits());

						double cmp = exitsCMP.stream().filter(w -> w.getScCode().equals(exitSc)).findFirst().get()
								.getCurrPrice();

						if (cmp > 0)
						{
							exitPos.setPL((exitHO.get().getPriceincl() - cmp) * exitHO.get().getUnits());
							exitPos.setCurrVal(cmp * exitHO.get().getUnits());
							exitsEval.add(exitPos);
						}

					}
				}
			}
		}

		return exitsEval;
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
