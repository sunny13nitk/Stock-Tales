package stocktales.NFS.srv.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.entity.NFSRunTmp;
import stocktales.NFS.model.ui.NFSGainersLosers;
import stocktales.NFS.model.ui.NFSMCapClass;
import stocktales.NFS.model.ui.NFSNewPF_PREUI;
import stocktales.NFS.model.ui.NFSPFPL;
import stocktales.NFS.model.ui.NFSPFSummary;
import stocktales.NFS.model.ui.NFSPFTable;
import stocktales.NFS.model.ui.NFSRunTmp_UISel;
import stocktales.NFS.repo.RepoNFSJornal;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.repo.RepoNFSTmp;
import stocktales.NFS.srv.intf.INFSPFUISrv;
import stocktales.NFS.srv.intf.INFSProcessor;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.strategy.helperPOJO.SectorAllocations;
import yahoofinance.Stock;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Service
public class NFSPFUISrv implements INFSPFUISrv
{
	@Autowired
	private RepoNFSPF repoNFSPf;

	@Autowired
	private RepoNFSTmp repoNFSTmp;

	@Autowired
	private RepoNFSJornal repoNFSJ;

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private INFSProcessor nfsProcSrv;

	private List<NFSRunTmp_UISel> userSelScrips = new ArrayList<NFSRunTmp_UISel>();

	@Override
	public NFSPFSummary getPfSummary() throws Exception
	{
		NFSPFSummary pfSummary = null;
		if (repoNFSPf != null)
		{

			try
			{
				List<NFSPF> holdings = repoNFSPf.findAll();
				if (holdings.size() > 0)
				{
					pfSummary = new NFSPFSummary();

					// Prepare NFS PF Table
					pfSummary.setPfTable(prepareHoldingsTable(holdings));

					// Prepare Summary of P&L
					pfSummary.setPortfolioPL(prepareHoldingsPL(pfSummary.getPfTable()));

					// Prepare MCAP Classification Table from PfTable of PfSummary
					pfSummary.setMcapClass(prepareMCapClassification(pfSummary.getPfTable()));

					// Prepare Gainers Losers
					pfSummary.setPfGainersLosers(prepareGainersLosers(pfSummary.getPfTable()));
				}
			} catch (Exception e)
			{
				/*
				 * Will be handled Centrally by GlobalExcpetion controller
				 */
				throw new NotFoundException("NFS UI Service Error! " + e.getMessage());
			}
		}
		return pfSummary;
	}

	@Override
	public List<NFSRunTmp_UISel> getScripsForSelectionFromSavedProposal()
	{
		List<NFSRunTmp_UISel> scSel = null;

		if (repoNFSTmp != null)
		{
			if (repoNFSTmp.count() > 0)
			{
				Date propDate = repoNFSTmp.getProposalDate();
				scSel = new ArrayList<NFSRunTmp_UISel>();

				for (NFSRunTmp scCan : repoNFSTmp.findAllByOrderByRank())
				{
					NFSRunTmp_UISel uiSel = new NFSRunTmp_UISel();

					uiSel.setSccode(scCan.getSccode());
					uiSel.setConsolscore(scCan.getConsolscore());
					uiSel.setDate(propDate);
					uiSel.setIsincluded(true);
					uiSel.setRank(scCan.getRank());
					uiSel.setScreenerUrl(nfsConfig.getScreenerpf() + scCan.getSccode() + nfsConfig.getScreenersf());
					scSel.add(uiSel);
				}
			}
		}

		return scSel;
	}

	@Override
	public void saveSelScripsinBuffer(List<NFSRunTmp_UISel> scripsUserSelected)
	{
		if (scripsUserSelected != null)
		{
			if (scripsUserSelected.size() > 0)
			{
				this.userSelScrips.clear();
				this.userSelScrips.addAll(scripsUserSelected);
			}
		}

	}

	@Override
	public NFSNewPF_PREUI getNewPF_PreCreateDetails() throws Exception
	{
		NFSNewPF_PREUI newPfDet = null;
		int numScrips = this.userSelScrips.size();
		boolean rebalance = false;
		if (numScrips == 0) // Triggered from re-balance
		{
			rebalance = true;
			numScrips = (int) repoNFSPf.count();
		}
		double perAlloc = Precision.round((100 / numScrips), 1);

		if (numScrips > 0)
		{
			newPfDet = new NFSNewPF_PREUI();
			newPfDet.setNumScrips(numScrips);
			newPfDet.setPerAlloc(perAlloc);

			List<SectorAllocations> scAllocs = new ArrayList<>();

			if (rebalance == false)
			{
				for (NFSRunTmp_UISel nfsRunTmp_UISel : userSelScrips)
				{
					scAllocs.add(new SectorAllocations(nfsRunTmp_UISel.getSccode(), perAlloc));
				}
			} else
			{
				for (NFSPF nfspf : repoNFSPf.findAll())
				{
					scAllocs.add(new SectorAllocations(nfspf.getSccode(), perAlloc));
				}

				newPfDet.setCurrInv(repoNFSPf.getTotalInvestedValue());
				newPfDet.setCurrInvStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(newPfDet.getCurrInv(), 2));
			}

			newPfDet.setMinInv(StockPricesUtility.getMinmAmntforPFCreation(scAllocs));
			newPfDet.setMinInvStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(newPfDet.getMinInv(), 2));
		}

		return newPfDet;
	}

	@Override
	public void createPF4mExistingProposalSelection(double invAmnt) throws Exception
	{
		if (invAmnt > 0)
		{
			if (this.userSelScrips.size() > 0 && nfsProcSrv != null)
			{
				List<NFSPF> scSaveList = new ArrayList<NFSPF>();
				for (NFSRunTmp_UISel selScrip : userSelScrips)
				{
					NFSPF scEnt = new NFSPF();
					scEnt.setSccode(selScrip.getSccode());
					scEnt.setRankincl(selScrip.getRank());
					scEnt.setRankcurr(selScrip.getRank());

					scSaveList.add(scEnt);
				}

				nfsProcSrv.createPF4mExistingProposalSelection(scSaveList, invAmnt);
				this.userSelScrips.clear();
			}
		}

	}

	/*
	 * -------------------------------------------------------------------------
	 * ----------------------- PRIVATE SECTION----------------------------------
	 * --------------------------------------------------------------------------
	 */

	private NFSGainersLosers prepareGainersLosers(List<NFSPFTable> pfTable)
	{
		NFSGainersLosers nfsGL = null;
		if (pfTable.size() > 0)
		{
			nfsGL = new NFSGainersLosers();

			// Get Max Gainer
			NFSPFTable maxGainer = Collections.max(pfTable, Comparator.comparing(x -> x.getPlper()));
			if (maxGainer != null)
			{
				nfsGL.setMaxGainer(new SectorAllocations(maxGainer.getSccode(), maxGainer.getPlper()));
			}

			// Get Min Gainer
			NFSPFTable minGainer = Collections.min(pfTable, Comparator.comparing(x -> x.getPlper()));
			if (minGainer != null)
			{
				nfsGL.setMaxLoser(new SectorAllocations(minGainer.getSccode(), minGainer.getPlper()));
			}

			nfsGL.setNumPfGainers((int) pfTable.stream().filter(x -> x.getPlper() >= 0).count());
			nfsGL.setNumPfLosers((int) pfTable.stream().filter(x -> x.getPlper() < 0).count());

			nfsGL.setNumDayGainers((int) pfTable.stream().filter(x -> x.getDaysChangePer() >= 0).count());
			nfsGL.setNumDayLosers((int) pfTable.stream().filter(x -> x.getDaysChangePer() < 0).count());

		}

		return nfsGL;
	}

	private List<NFSMCapClass> prepareMCapClassification(List<NFSPFTable> pfTable)
	{
		List<NFSMCapClass> mcapTable = new ArrayList<NFSMCapClass>();
		List<NFSMCapClass> mcapTableRet = new ArrayList<NFSMCapClass>();

		for (NFSPFTable nfspfTable : pfTable)
		{
			NFSMCapClass mcapInv = new NFSMCapClass(nfspfTable.getMcapClass().name(), 0, nfspfTable.getInvAmnt(), 0);
			mcapTable.add(mcapInv);
		}

		// Grouping and Showing Group Key and Corresponding Entities in each Group
		Map<String, List<NFSMCapClass>> allocsPerMcap = mcapTable.stream()
				.collect(Collectors.groupingBy(NFSMCapClass::getMcapCatgName));

		// Grouping and Summing BY
		Map<String, Double> allocsPerMCapSorted = mcapTable.stream().collect(Collectors
				.groupingBy(NFSMCapClass::getMcapCatgName, Collectors.summingDouble(NFSMCapClass::getInvestment)));

		// Converting Map to List
		if (allocsPerMCapSorted.size() > 0)
		{
			List<SectorAllocations> secAllocP = new ArrayList<SectorAllocations>();
			allocsPerMCapSorted.forEach((k, v) -> secAllocP.add(new SectorAllocations(k, v)));

			if (secAllocP.size() > 0)
			{
				double totalInv = pfTable.stream().mapToDouble(NFSPFTable::getInvAmnt).sum();
				for (SectorAllocations secAlloc : secAllocP)
				{
					if (secAlloc.getSector().trim().length() > 0)
					{

						NFSMCapClass mcapEnt = new NFSMCapClass();
						mcapEnt.setMcapCatgName(secAlloc.getSector());
						mcapEnt.setPercentage(Precision.round((secAlloc.getAlloc() * 100 / totalInv), 1));

						int numScrips = (int) mcapTable.stream()
								.filter(x -> x.getMcapCatgName().equals(secAlloc.getSector())).count();
						mcapEnt.setNumScrips(numScrips);
						numScrips = 0;

						mcapTableRet.add(mcapEnt);
					}
				}
			}

		}

		return mcapTableRet;

	}

	private NFSPFPL prepareHoldingsPL(List<NFSPFTable> pfTable)
	{
		NFSPFPL pfPL = new NFSPFPL();

		if (repoNFSPf != null)
		{
			double sumInvested = repoNFSPf.getTotalInvestedValue();
			if (sumInvested > 0)
			{
				double totalPL = pfTable.stream().mapToDouble(NFSPFTable::getPlamnt).sum();

				if (repoNFSJ != null)
				{
					double realzPl = 0;
					if (repoNFSJ.count() > 0)
					{
						realzPl = repoNFSJ.getRealzPl();
						pfPL.setRealzPl(Precision.round(realzPl, 0));
						pfPL.setRealzPLStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfPL.getRealzPl(), 1));
					}

				}

				pfPL.setCurrVal(UtilDecimaltoMoneyString.getMoneyStringforDecimal((sumInvested + totalPL), 2));
				pfPL.setPl(UtilDecimaltoMoneyString.getMoneyStringforDecimal(totalPL + pfPL.getRealzPl(), 1));
				pfPL.setPlPer(Precision.round((((totalPL + pfPL.getRealzPl()) / sumInvested) * 100), 1));

				double daysGain = pfTable.stream().mapToDouble(NFSPFTable::getDaysChangeAmnt).sum();
				pfPL.setDaysGain(Precision.round(daysGain, 0));

				double ydayValue = (sumInvested + totalPL) - daysGain;
				double daysGainPer = (daysGain / ydayValue) * 100;
				pfPL.setDaysGainPer(Precision.round(daysGainPer, 1));
			}
		}

		return pfPL;
	}

	private List<NFSPFTable> prepareHoldingsTable(List<NFSPF> holdings) throws Exception
	{
		List<NFSPFTable> pfTable = new ArrayList<NFSPFTable>();
		for (NFSPF holding : holdings)
		{
			// Get Quote for Each Holding
			Stock quote = StockPricesUtility.getQuoteforScrip(holding.getSccode());
			if (quote != null)
			{
				// Prepare Table Entity
				NFSPFTable pfTabRow = new NFSPFTable();
				pfTabRow.setSccode(holding.getSccode());
				pfTabRow.setRankincl(holding.getRankincl());
				pfTabRow.setRankcurr(holding.getRankcurr());
				pfTabRow.setPriceincl(Precision.round(holding.getPriceincl(), 0));
				pfTabRow.setCmp(Precision.round(quote.getQuote().getPrice().doubleValue(), 0));
				pfTabRow.setUnits(holding.getUnits());
				pfTabRow.setInvAmnt(Precision.round((holding.getUnits() * holding.getPriceincl()), 1));
				pfTabRow.setPlper(UtilPercentages.getPercentageDelta(holding.getPriceincl(), pfTabRow.getCmp(), 1));
				pfTabRow.setPlamnt(
						Precision.round(((pfTabRow.getCmp() - pfTabRow.getPriceincl()) * pfTabRow.getUnits()), 0));
				pfTabRow.setDaysChangePer(
						UtilPercentages.getPercentageDelta(quote.getQuote().getPreviousClose().doubleValue(),
								quote.getQuote().getPrice().doubleValue(), 1));

				pfTabRow.setDaysChangeAmnt(Precision.round(
						(pfTabRow.getDaysChangePer() * (pfTabRow.getUnits() * pfTabRow.getPriceincl()) / 100), 0));
				pfTabRow.setDateincl(holding.getDateincl());
				pfTabRow.setDatelasttxn(holding.getDatelasttxn());

				double Mcap = Precision.round((quote.getStats().getMarketCap().doubleValue() / 10000000), 0);
				pfTabRow.setMcapClass(this.getMcapClassificationForMCapKCr(Mcap));

				pfTable.add(pfTabRow);
			}
		}

		return pfTable;
	}

	private EnumMCapClassification getMcapClassificationForMCapKCr(double Mcap)
	{
		EnumMCapClassification mcapEnum = null;

		if (Mcap > nfsConfig.getMCapLargeCap())
		{
			mcapEnum = EnumMCapClassification.LargeCap;

		} else
		{
			if (Mcap < nfsConfig.getMCapSmallCap())
			{
				mcapEnum = EnumMCapClassification.SmallCap;
			} else
			{
				mcapEnum = EnumMCapClassification.MidCap;
			}
		}

		return mcapEnum;
	}

}