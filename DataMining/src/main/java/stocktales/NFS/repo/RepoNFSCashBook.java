package stocktales.NFS.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import stocktales.NFS.model.entity.NFSCashBook;

public interface RepoNFSCashBook extends JpaRepository<NFSCashBook, Integer>
{
	@Query("select c from NFSCashBook c where c.date = ( select max(cc.date) from NFSCashBook cc ) ")
	public Optional<NFSCashBook> getLatestEntry();

	@Query("select MAX(date) from NFSCashBook where txntype = stocktales.NFS.enums.EnumNFSTxnType.Deploy")
	public Date getLastBuyDate();

	@Query("select c from NFSCashBook c where c.date >= ?1"
			+ " AND ( c.txntype = stocktales.NFS.enums.EnumNFSTxnType.Dividend OR "
			+ " c.txntype = stocktales.NFS.enums.EnumNFSTxnType.SalePartial OR "
			+ " c.txntype = stocktales.NFS.enums.EnumNFSTxnType.Exit )")
	public List<NFSCashBook> getCashFlowPositiveTxnAfterDate(Date date);
}
