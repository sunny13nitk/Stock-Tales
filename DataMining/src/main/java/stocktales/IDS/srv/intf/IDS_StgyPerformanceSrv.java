package stocktales.IDS.srv.intf;
/**
 * 
 * Strategy Performance Service
 */

import java.util.List;

import stocktales.IDS.pojo.IDS_Stgy_ReturnsDetails;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StgyAlloc;

public interface IDS_StgyPerformanceSrv
{
	/**
	 * Get PF Schema Performance Details for Default 6M, 1Y, 2Y, 3Y, 5Y
	 * 
	 * @return - Strategy REturn Details {IDS_Stgy_ReturnsDetails}
	 */
	public IDS_Stgy_ReturnsDetails getPerformanceDetailsforPFScehma();

	/**
	 * Get Portfolio Performance Details for specified Intervals split on daily
	 * basis
	 * 
	 * @param scAllocs          - List of <ScripNames and Allocations> {StgyAllocs}
	 *                          : Sum Allocations must be 100
	 * @param intervalsInMonths - Interval Containers on which performance needs to
	 *                          be consolidated
	 * @return - Strategy REturn Details { IDS_Stgy_ReturnsDetails }
	 */
	public IDS_Stgy_ReturnsDetails getPerformanceDetailsforStrategy(List<StgyAlloc> scAllocs, int[] intervalsInMonths)
			throws Exception;

}
