package stocktales.money;

import java.math.BigDecimal;

import org.apache.commons.math3.util.Precision;

public class UtilDecimaltoMoneyString
{
	public static String getMoneyStringforDecimal(
	        double decimalval, int scale
	)
	{
		String moneyStr = null;
		
		decimalval = Precision.round(decimalval, scale);
		String s      = Double.toString(decimalval);
		int    length = s.length();
		
		int dot = s.indexOf('.');
		
		if (dot < 4) // less than 1K
		{
			if (s.length() >= 12) // Cr . Case
			{
				
				BigDecimal bgVal  = new BigDecimal(decimalval / 10000000);
				String     bgValS = bgVal.toString();
				//int        crLen  = bgValS.length();
				int crDot = bgValS.indexOf('.');
				
				moneyStr = bgValS.substring(0, crDot) + " Cr.";
				
			} else
			{
				moneyStr = Double.toString(decimalval);
			}
		} else if (dot >= 4 && dot <= 5) // 1000 to 99999
		{
			decimalval = decimalval / 1000;
			decimalval = Precision.round(decimalval, scale);
			moneyStr   = Double.toString(decimalval) + " K";
		} else if (dot > 5 && dot <= 7) // 10000 to 99999
		{
			decimalval = decimalval / 100000;
			decimalval = Precision.round(decimalval, scale);
			moneyStr   = Double.toString(decimalval) + " Lacs";
		}
		
		return moneyStr;
		
	}
}
