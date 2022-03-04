package stocktales.basket.allocations.autoAllocation.valuations.interfaces;

import stocktales.basket.allocations.autoAllocation.valuations.pojos.scWtPE;

public interface SCWtPESrv
{
	public scWtPE getWeightedPEforScrip(
	        String scCode
	);
}
