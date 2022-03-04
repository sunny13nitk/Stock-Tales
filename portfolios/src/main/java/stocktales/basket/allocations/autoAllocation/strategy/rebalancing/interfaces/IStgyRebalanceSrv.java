package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces;

import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos.StgyRebalance;

public interface IStgyRebalanceSrv
{
	/**
	 * Trigger Rebalancing for a Strategy - Generate Proposals (Additions/Removals)
	 * as per predicates
	 * @param stgId - Id of Strategy to Rebalance - INT
	 * @return - Deep POJO that contains the rebalancing information of Type
	 * @StgyRebalance
	 */
	public StgyRebalance triggerReBalancingforStgy(
	        int stgId
	);
	
	public boolean addNewScrip(
	        String scCode
	);
	
	public boolean deleteScrip(
	        String scCode
	);
	
	public StgyRebalance getRblPojo(
	);
	
	/**
	 * Finally Save the Re-balanced Strategy
	 * @return - Handle to Saved Strategy
	 */
	public void saveStrategy(
	        Strategy Stgy
	);
}
