package stocktales.basket.allocations.autoAllocation.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;

public interface IMcapAllocationSrv
{
	public List<ScAllocation> calibrateAllocationbyMCap(
	        List<ScAllocation> scAllocList
	);
}
