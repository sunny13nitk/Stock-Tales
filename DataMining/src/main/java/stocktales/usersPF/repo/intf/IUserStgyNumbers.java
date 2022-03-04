package stocktales.usersPF.repo.intf;

/**
 * 
 * To be Used in RepoHoldings to return User Strategy
 * Total Allocation, Total P&L and Total Dividends
 *
 */
public interface IUserStgyNumbers
{
	Double getTotalAllocation(
	);
	
	Double getTotalPL(
	);
	
	Double getTotalDiv(
	);
}
