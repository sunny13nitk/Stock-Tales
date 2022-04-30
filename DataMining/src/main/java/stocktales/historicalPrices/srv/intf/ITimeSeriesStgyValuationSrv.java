package stocktales.historicalPrices.srv.intf;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.strategy.pojos.StgyAlloc;
import stocktales.historicalPrices.enums.EnumInterval;
import stocktales.historicalPrices.pojo.StgyRelValuation;

public interface ITimeSeriesStgyValuationSrv
{
	/**
	 * Get Relative Valuations on Per Day basis based to 100 from Starting Day for a
	 * Strategy and a predefined Interval
	 * 
	 * @param StgyId   - Strategy Id
	 * @param interval - Interval EnumInterval
	 * @return - <Date,Valuation> pair based from Start Date to 100
	 * @throws Exception
	 */
	public List<StgyRelValuation> getValuationsforStrategy(int StgyId, EnumInterval interval) throws Exception;

	/**
	 * Get Relative Valuations on Per Day basis based to 100 from Starting Day for
	 * PF Schema Ideal Allocation and a predefined Interval
	 * 
	 * @param interval - Interval EnumInterval
	 * @return - <Date,Valuation> pair based from Start Date to 100
	 * @throws Exception
	 */
	public List<StgyRelValuation> getValuationsforSchema(EnumInterval interval) throws Exception;

	/**
	 * Get Relative Valuations on Per Day basis based to 100 from Starting Day for
	 * List of Scrips and Allocation(s) and a predefined Interval
	 * 
	 * @param interval - Interval EnumInterval
	 * @return - <Date,Valuation> pair based from Start Date to 100
	 * @throws Exception
	 */
	public List<StgyRelValuation> getValuationsforScripsAllocList(EnumInterval interval, List<StgyAlloc> scAllocs)
			throws Exception;

}
