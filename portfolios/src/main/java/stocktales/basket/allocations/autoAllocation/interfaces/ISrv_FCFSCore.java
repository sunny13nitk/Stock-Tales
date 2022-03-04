package stocktales.basket.allocations.autoAllocation.interfaces;

import stocktales.basket.allocations.autoAllocation.pojos.FCFScore;

public interface ISrv_FCFSCore
{
	
	public FCFScore getFCFScorebyScrip(
	        String scCode
	);
	
	public double getFCFYield(
	        String scCode
	);
	
	public double getCFOYield(
	        String scCode
	);
}
