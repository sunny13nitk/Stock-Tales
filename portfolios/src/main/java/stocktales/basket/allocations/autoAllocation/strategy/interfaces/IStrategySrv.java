package stocktales.basket.allocations.autoAllocation.strategy.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StQuickStats;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.helperPOJO.StrategyListPOJO;

public interface IStrategySrv
{
	public void Initialize(
	);
	
	public void loadAllocationItems(
	        List<ScAllocation> allocItems
	);
	
	public void saveHeaderInfo(
	        Strategy stgyHeader
	);
	
	public StQuickStats getQuickStats(
	);
	
	public Strategy save(
	        Strategy strategy
	);
	
	public List<StrategyListPOJO> getStrategiesList(
	);
	
	public List<ScAllocation> getValuationsSimulationforStrategy(
	        int StgId
	);
}
