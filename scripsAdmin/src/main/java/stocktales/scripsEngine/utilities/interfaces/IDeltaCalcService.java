package stocktales.scripsEngine.utilities.interfaces;

import stocktales.scripsEngine.enums.SCEenums;

/**
 * 
 * Delta Calculation Service
 */
public interface IDeltaCalcService
{
	
	public double getDeltaPercentage(
	        double fromVal, double toVal
	);
	
	public double getDelta(
	        double fromVal, double toVal
	);
	
	public double getDeltaPercentage(
	        int fromVal, int toVal
	);
	
	public double getDelta(
	        int fromVal, int toVal
	);
	
	public double adjustNumberbyPercentage(
	        double numtoAdjust, double percentage, SCEenums.direction direction
	);
	
}
