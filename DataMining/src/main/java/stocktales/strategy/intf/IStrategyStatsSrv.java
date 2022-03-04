package stocktales.strategy.intf;

import java.util.List;

import stocktales.strategy.helperPOJO.SectorAllocations;
import stocktales.strategy.helperPOJO.StgyStatsShortSummary;
import stocktales.strategy.helperPOJO.StgyStatsSummary;

public interface IStrategyStatsSrv
{
	/**
	 * Get Complete Statistics Summary for Strategy
	 * @param strategyId - Strategy ID (int)
	 * @return - Strategy Summary includes basic details, CAGR, Scrips and Sectoral Allocations
	 * @throws Exception
	 */
	public StgyStatsSummary getStatsforStrategy(
	        int strategyId
	) throws Exception;
	
	/**
	 * Get Shorthand Statistics Summary
	 * @param strategyId - Strategy ID (int)
	 * @return - Strategy Summary includes basic details, number of Scrips and Sectors
	 * @throws Exception
	 */
	public StgyStatsShortSummary getShortStatsforStrategy(
	        int strategyId
	) throws Exception;
	
	public List<SectorAllocations> getSectorSplitUpforStrategy(
	        int strategyId
	);
}
