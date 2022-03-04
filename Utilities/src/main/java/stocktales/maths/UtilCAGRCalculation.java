package stocktales.maths;

public class UtilCAGRCalculation
{
	public static double calculateCAGR(double fromVal, double toVal, double interval)
	{
		double CAGR = 0;

		CAGR = (Math.pow(toVal / fromVal, 1.0 / (interval)) - 1.0) * 100;

		return CAGR;
	}

	public static double getCAGRFinalValue(double fromVal, double rate, int duration)
	{
		double finalValue = fromVal;

		for (int i = 0; i < duration; i++)
		{
			finalValue = finalValue + (rate / 100) * finalValue;
		}
		return finalValue;
	}

	public static double getCAGRInitialValue(double rate, double finValue, int intervals)
	{
		double iniVal = 0;

		double powOP = Math.pow((1 + (rate / 100)), intervals);
		iniVal = finValue / powOP;

		return iniVal;
	}

}
