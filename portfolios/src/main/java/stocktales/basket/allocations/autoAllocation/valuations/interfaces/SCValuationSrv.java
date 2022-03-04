package stocktales.basket.allocations.autoAllocation.valuations.interfaces;

import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;

public interface SCValuationSrv
{
	//For DB CMP give CMP as 0 and for 100% Calc EDRC- ED SCore give MoS as 1
	public ScValuation getValuationforScrip(
	        String scCode, double CMP, double MoS
	);
	
}
