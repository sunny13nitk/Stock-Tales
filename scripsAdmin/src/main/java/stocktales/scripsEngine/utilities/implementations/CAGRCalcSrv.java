package stocktales.scripsEngine.utilities.implementations;

public class CAGRCalcSrv
{
	public static double getCAGR(double iniValue, double finValue, int intervals)
	{
		double CAGR = 0;

		double valOP = finValue / iniValue;
		double intOP = 1.0 / (double) intervals;

		double netCaGR = Math.pow(valOP, intOP);

		CAGR = (netCaGR - 1) * 100;

		return CAGR;
	}

	public static double getiniValforCAGR(double rate, double finValue, int intervals)
	{
		double iniVal = 0;

		double powOP = Math.pow((1 + (rate / 100)), intervals);
		iniVal = finValue / powOP;

		return iniVal;
	}

}
