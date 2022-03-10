package stocktales.maths;

import org.apache.commons.math3.util.Precision;

import stocktales.enums.EnumDirection;

public class UtilPercentages
{
	public static double getPercentageDelta(double from, double to, int decimalplaces)
	{
		double delta = 0;

		if (from > 0)
		{

			delta = ((to - from) / from) * 100;
		}

		return Precision.round(delta, decimalplaces);
	}

	public static double getProportion(double from, double to, int decimalplaces)
	{
		double delta = 0;

		if (from != 0)
		{

			delta = (to / from) * 100;
		}

		return Precision.round(delta, decimalplaces);
	}

	public static double adjustNumberbyPercentage(double numtoAdjust, double percentage, EnumDirection direction)
	{
		double num = numtoAdjust;

		if (percentage != 0)
		{
			double perportion = percentage / 100 * numtoAdjust;

			if (direction == EnumDirection.Increase)
			{
				num = num + perportion;
			} else if (direction == EnumDirection.Decrease)
			{
				num = num - perportion;
			}
		}

		return num;
	}
}
