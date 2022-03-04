package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces;

import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.enums.ERebalType;

public interface IStgyRebalTxts_Simple
{
	int getRbid(
	);
	
	String getSccode(
	);
	
	ERebalType getRbaltype(
	);
	
	String getReason(
	);
}
