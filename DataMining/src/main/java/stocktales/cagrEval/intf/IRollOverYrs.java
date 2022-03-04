package stocktales.cagrEval.intf;

import stocktales.cagrEval.helperPoJo.YearsRollOverResults;

public interface IRollOverYrs
{
	public YearsRollOverResults generateRollOverYrs(
	        int baseYr, int intervalsize, int totalLength
	);
	
}
