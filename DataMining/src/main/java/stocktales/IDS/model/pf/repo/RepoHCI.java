package stocktales.IDS.model.pf.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.HCI;
import stocktales.usersPF.enums.EnumTxnType;

@Repository
public interface RepoHCI extends JpaRepository<HCI, Integer>
{
	// Get last Buy txn date for Scrip Code
	@Query("select MAX(date) from HCI where sccode = ?1 AND txntype = stocktales.usersPF.enums.EnumTxnType.Buy")
	public Date getlastBuyTxnDateforScrip(String scCode);

	/*
	 * Get all Transactions by Txn Type TO Find The Last MAx Date Txn - use Date
	 * getlastBuyTxnDateforScrip(String scCode) in Conjunction to Filter above list
	 * by this Date
	 */
	public List<HCI> findAllByTxntypeAndSccode(EnumTxnType tType, String scCode);

	public List<HCI> findAllBySccode(String scCode);

	@Modifying
	@Query("update HCI h set  h.units = ?2, h.txnppu = ?3  where h.tid = ?1")
	public void updatePPUUnitsforItemTxn(int tid, int units, double ppu);

	// Get last Sell txn date for Scrip Code
	@Query("select MAX(date) from HCI where sccode = ?1 AND txntype = stocktales.usersPF.enums.EnumTxnType.Sell")
	public Date getlastSellTxnDateforScrip(String scCode);

	// Get Total Buy Amount for Scrip Code
	@Query("select SUM( units * txnppu) from HCI where sccode = ?1 AND txntype = stocktales.usersPF.enums.EnumTxnType.Buy")
	public Date getTotalPurcasheAmountforScrip(String scCode);

	// Get Total Sell Amount for Scrip Code
	@Query("select SUM( units * txnppu) from HCI where sccode = ?1 AND txntype = stocktales.usersPF.enums.EnumTxnType.Sell")
	public Date getTotalSaleAmountforScrip(String scCode);

	@Query("select Count(tid) from HCI where sccode = ?1 ")
	public int getNumTxnsforScrip(String scCode);

	@Modifying
	@Query("delete from HCI where sccode = ?1 ")
	public void removeScrip(String scCode);

	@Query("select distinct(date) from HCI")
	public List<Date> getUniqueTxnDates();

	@Query("select MIN(date) from HCI")
	public Date getPFInvSinceDate();

	@Query("select COUNT(tid) from HCI where txntype = stocktales.usersPF.enums.EnumTxnType.Buy")
	public int getCountBuyTxns();

	@Query("select COUNT(tid) from HCI where txntype = stocktales.usersPF.enums.EnumTxnType.Sell OR txntype = stocktales.usersPF.enums.EnumTxnType.Exit")
	public int getCountSellTxns();

}
