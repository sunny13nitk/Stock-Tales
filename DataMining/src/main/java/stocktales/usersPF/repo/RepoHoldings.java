package stocktales.usersPF.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.usersPF.model.Holding;
import stocktales.usersPF.repo.intf.IUserStgyNumbers;

@Repository
public interface RepoHoldings extends JpaRepository<Holding, Integer>
{
	
	//Get Total Allocation by Strategy User strategyID (! Strategy ID)
	@Query("Select SUM( units * ppu) from Holding where userStrategy.id = ?1")
	public double getTotalAllocation(
	        long userStgyId
	);
	
	//Get Total Dividends Realized by Strategy User strategyID 
	@Query("Select SUM( realzdiv) from Holding where userStrategy.id = ?1")
	public double getTotalDividends(
	        long userStgyId
	);
	
	//Get Total P&L Realized by Strategy User strategyID 
	@Query("Select SUM( realzpl) from Holding where userStrategy.id = ?1")
	public double getTotalPandL(
	        long userStgyId
	);
	
	/**
	 * Get User Strategy - Total Allocation, Total P&L, Total Dividends 
	 * @param userStgyId - User Strategy ID (ID with which User is associated to a Strategy)
	 * @return - User Strategy - Total Allocation, Total P&L, Total Dividends 
	 */
	
	@Query(
	    "select SUM( units * ppu ) as totalAllocation, SUM(realzdiv) as totalDiv, SUM(realzpl) as totalPL from Holding where userStrategy.id = ?1"
	)
	public IUserStgyNumbers getStgyNumbers(
	        long userStgyId
	);
	
}
