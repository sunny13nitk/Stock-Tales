package stocktales.IDS.model.pf.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.MoneyBag;

@Repository
public interface RepoMoneyBag extends JpaRepository<MoneyBag, Integer>
{
	@Query("select SUM( amount ) from MoneyBag where type = stocktales.IDS.enums.EnumTxnType.Deposit")
	public double getDepositTxnTotal();

	@Query("select SUM( amount ) from MoneyBag where type = stocktales.IDS.enums.EnumTxnType.Withdraw")
	public double getWithdrawalTxnTotal();

	@Query("select SUM( amount ) from MoneyBag where type = stocktales.IDS.enums.EnumTxnType.Dividend")
	public double getDividendTxnTotal();
}
