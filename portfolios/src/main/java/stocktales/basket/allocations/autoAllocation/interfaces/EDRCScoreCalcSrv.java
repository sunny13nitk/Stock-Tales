package stocktales.basket.allocations.autoAllocation.interfaces;

import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;

public interface EDRCScoreCalcSrv
{
	public ScripEDRCScore getEDRCforScrip(
	        String scCode
	);
}
