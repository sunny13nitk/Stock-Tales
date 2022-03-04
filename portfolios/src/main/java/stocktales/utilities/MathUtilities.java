package stocktales.utilities;

import org.apache.commons.math3.util.Precision;

public class MathUtilities
{
	public static double getPercentageDelta(
	        double from, double to
	)
	{
		double delta = 0;
		
		if (from > 0)
		{
			
			delta = ((to - from) / from) * 100;
		}
		
		return Precision.round(delta, 1);
	}
	
	public static double getCAGRFinalValue(
	        double initialValue, double rate, int duration
	)
	{
		double finalValue = initialValue;
		
		for (int i = 0; i < duration; i++)
		{
			finalValue = finalValue + (rate / 100) * finalValue;
		}
		return finalValue;
	}
}
