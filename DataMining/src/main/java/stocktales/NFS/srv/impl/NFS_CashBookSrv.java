package stocktales.NFS.srv.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import stocktales.NFS.enums.EnumNFSTxnType;
import stocktales.NFS.model.entity.NFSCashBook;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.pojo.NFSCB_IP;
import stocktales.NFS.model.pojo.ScripPPUUnitsRank;
import stocktales.NFS.repo.RepoNFSCashBook;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.srv.intf.INFSPFUISrv;
import stocktales.NFS.srv.intf.INFS_CashBookSrv;
import stocktales.NFS.srv.intf.INFS_DD_Srv;
import stocktales.durations.UtilDurations;

/**
 * Entry in the Cash Book Will Always Pre-ceed the Real PF Change to account for
 * actual unrealzP&L and DD Values
 *
 */
@Service
public class NFS_CashBookSrv implements INFS_CashBookSrv
{

	@Autowired
	private INFS_DD_Srv nfsDDSrv;

	@Autowired
	@Lazy
	private INFSPFUISrv nfsUISrv;

	@Autowired
	private RepoNFSPF repoNFSPF;

	@Autowired
	private RepoNFSCashBook repoCB;

	private final double minInv = 25000;

	@Override
	public void processCBTxn(NFSCB_IP cbTxn) throws Exception
	{
		if (cbTxn != null)
		{
			if (cbTxn.getAmount() > 0 && cbTxn.getTxntype() != null)
			{

				switch (cbTxn.getTxntype())
				{
				case Deploy:
					processDeploy(cbTxn);

					break;

				case Dividend:
					processDividend(cbTxn);
					break;

				case SalePartial:
					processSale(cbTxn);
					break;

				case Exit:
					processExit(cbTxn);
					break;

				default:
					break;
				}
			}
		}

	}

	@Override
	public void processCBTxn(double amountnewPFInv, double maxPerLoss) throws Exception
	{
		if (amountnewPFInv > minInv && maxPerLoss != 0 && repoCB != null)
		{
			if (repoNFSPF.count() == 0) // Only in case of a new PF
			{
				NFSCashBook cbTxn = new NFSCashBook();

				cbTxn.setAmount(amountnewPFInv);
				cbTxn.setCash(amountnewPFInv * -1);
				cbTxn.setDate(UtilDurations.getTodaysDateOnly());
				cbTxn.setDdmax(maxPerLoss);
				cbTxn.setTxntype(EnumNFSTxnType.Deploy);
				cbTxn.setUnrealzplper(0);

				repoCB.save(cbTxn);
			}

		}

	}

	@Override
	public double getDeployableBalance() throws Exception
	{
		double depAmnt = 0;

		if (repoCB != null)
		{
			Date lastBuyDate = repoCB.getLastBuyDate();
			if (lastBuyDate != null)
			{
				List<NFSCashBook> posCFEntries = repoCB.getCashFlowPositiveTxnAfterDate(lastBuyDate);
				if (posCFEntries != null)
				{
					// Sum up the +ve Cash Flows - This becomes your Deployable Amount (Return)
					depAmnt = posCFEntries.stream().mapToDouble(NFSCashBook::getAmount).sum();
				}
			}

		}

		return depAmnt;
	}

	/*******************************************************************************************
	 * *********************** PRIVATE SECTION -----------------------------------
	 *****************************************************************************************/

	private void processDeploy(NFSCB_IP cbTxnIP)
	{

		// Check B4 Deployment that it should not be a new PF Scenario
		try
		{
			if (repoNFSPF.count() > 0)
			{
				NFSCashBook cbTxn = new NFSCashBook();

				cbTxn.setAmount(cbTxnIP.getAmount());

				// Get Latest Cash From Cash Book Repository
				Optional<NFSCashBook> cbLastEntryO = repoCB.getLatestEntry();
				if (cbLastEntryO.isPresent())
				{
					// Set Cash Cumulatively as per Last Cash Balance
					cbTxn.setCash(cbLastEntryO.get().getCash() + (cbTxnIP.getAmount() * -1));

					cbTxn.setDate(UtilDurations.getTodaysDateOnly());

					// Get Max DD for Current PF
					cbTxn.setDdmax(Precision.round(getMaxDDforNFSPF(), 1));
					cbTxn.setTxntype(EnumNFSTxnType.Deploy);

					// Get UnrealZ P&L at time of fresh Deployment
					// cbTxn.setUnrealzplper(Precision.round(nfsUISrv.getPfSummary().getPortfolioPL().getPlPer(),
					// 1));

					repoCB.save(cbTxn);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processDividend(NFSCB_IP cbTxnIP)
	{

		// Check B4 Deployment that it should not be a new PF Scenario
		try
		{
			if (repoNFSPF.count() > 0)
			{
				NFSCashBook cbTxn = new NFSCashBook();

				cbTxn.setAmount(cbTxnIP.getAmount());

				// Get Latest Cash From Cash Book Repository
				Optional<NFSCashBook> cbLastEntryO = repoCB.getLatestEntry();
				if (cbLastEntryO.isPresent())
				{
					// Set Cash Cumulatively as per Last Cash Balance
					cbTxn.setCash(cbLastEntryO.get().getCash() + (cbTxnIP.getAmount()));

					cbTxn.setDate(UtilDurations.getTodaysDateOnly());

					// No need to set DD for Dividend Txn.
					cbTxn.setTxntype(EnumNFSTxnType.Dividend);

					// No need to set UnrealZ P&L

					repoCB.save(cbTxn);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processSale(NFSCB_IP cbTxnIP)
	{

		// Check B4 Deployment that it should not be a new PF Scenario
		try
		{
			if (repoNFSPF.count() > 0)
			{
				NFSCashBook cbTxn = new NFSCashBook();

				cbTxn.setAmount(cbTxnIP.getAmount());

				// Get Latest Cash From Cash Book Repository
				Optional<NFSCashBook> cbLastEntryO = repoCB.getLatestEntry();
				if (cbLastEntryO.isPresent())
				{
					// Set Cash Cumulatively as per Last Cash Balance
					cbTxn.setCash(cbLastEntryO.get().getCash() + (cbTxnIP.getAmount()));

					cbTxn.setDate(UtilDurations.getTodaysDateOnly());

					// Get Max DD for Current PF
					cbTxn.setDdmax(Precision.round(getMaxDDforNFSPF(), 1));
					cbTxn.setTxntype(EnumNFSTxnType.SalePartial);

					// Get UnrealZ P&L at time of fresh Deployment
					cbTxn.setUnrealzplper(Precision.round(nfsUISrv.getPfSummary().getPortfolioPL().getPlPer(), 1));

					repoCB.save(cbTxn);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processExit(NFSCB_IP cbTxnIP)
	{

		// Check B4 Deployment that it should not be a new PF Scenario
		try
		{
			if (repoNFSPF.count() > 0)
			{
				List<NFSCashBook> cbTxnExitList = new ArrayList<NFSCashBook>();

				NFSCashBook cbTxn = new NFSCashBook();

				cbTxn.setAmount(cbTxnIP.getAmount());

				// Get Latest Cash From Cash Book Repository
				Optional<NFSCashBook> cbLastEntryO = repoCB.getLatestEntry();
				if (cbLastEntryO.isPresent())
				{
					// Set Cash Cumulatively as per Last Cash Balance
					cbTxn.setCash(cbLastEntryO.get().getCash() + (cbTxnIP.getAmount()));

					cbTxn.setDate(UtilDurations.getTodaysDateOnly());

					// Get Max DD for Current PF
					cbTxn.setDdmax(Precision.round(getMaxDDforNFSPF(), 1));
					cbTxn.setTxntype(EnumNFSTxnType.Exit);

					// Get UnrealZ P&L at time of fresh Deployment
					cbTxn.setUnrealzplper(Precision.round(nfsUISrv.getPfSummary().getPortfolioPL().getPlPer(), 1));
					cbTxnExitList.add(cbTxn);

					NFSCashBook cbrealPLTxn = new NFSCashBook();
					// Exit Txn. cumulative Cash Flow becomes the RealP&L Amount
					cbrealPLTxn.setAmount(cbTxn.getCash());
					cbrealPLTxn.setCash(0); // Cash Set as Zero now - accounted as P&L Realized
					cbrealPLTxn.setDate(cbTxn.getDate());
					cbrealPLTxn.setDdmax(cbTxn.getDdmax());
					cbrealPLTxn.setTxntype(EnumNFSTxnType.RealzPl);
					cbrealPLTxn.setUnrealzplper(cbTxn.getUnrealzplper());
					cbTxnExitList.add(cbrealPLTxn);

					/*
					 * Commit Exit and Realized P&L Transactions together
					 */
					repoCB.saveAll(cbTxnExitList);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private double getMaxDDforNFSPF() throws Exception
	{
		double ddPer = 0;

		if (repoNFSPF != null)
		{
			if (repoNFSPF.count() > 0)
			{
				List<ScripPPUUnitsRank> scHoldings = new ArrayList<ScripPPUUnitsRank>();

				for (NFSPF holding : repoNFSPF.findAll())
				{
					ScripPPUUnitsRank item = new ScripPPUUnitsRank();
					item.setSccode(holding.getSccode());
					item.setPpu(holding.getPriceincl());
					item.setRankCurr(holding.getRankcurr());
					item.setUnits(holding.getUnits());

					scHoldings.add(item);

				}
				ddPer = Precision.round(nfsDDSrv.getDDByScripsPPUUnits(scHoldings).getMaxLossPer(), 1);
			}
		}

		return ddPer;
	}

}
