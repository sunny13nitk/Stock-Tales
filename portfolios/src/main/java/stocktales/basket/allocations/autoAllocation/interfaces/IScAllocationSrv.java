package stocktales.basket.allocations.autoAllocation.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;

public interface IScAllocationSrv
{
	public List<ScAllocation> calculateAllocations(
	        List<ScAllocation> tbAllocScrips
	);
}
