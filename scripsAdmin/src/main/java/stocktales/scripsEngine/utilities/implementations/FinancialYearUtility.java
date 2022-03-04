package stocktales.scripsEngine.utilities.implementations;

import java.util.Date;

import stocktales.scripsEngine.utilities.types.CurrentFinancialPeriod;

/**
 * Class for Financial Year Computation - Static Methods Financial Year is in terms of Assement Year = CFY + 1
 */
public class FinancialYearUtility
{
	public static final String constantFY    = "FY - ";
	public static final String constantSlash = "/";
	
	/**
	 * Get Current Financial Year in String format FY - xxxx/yy Financial Year is in terms of Assement Year = CFY + 1
	 * 
	 * @return - Financial Year in String format
	 */
	public static String getCurrentFinancialYear(
	)
	{
		String cFYear   = null;
		String nYearStr = null;
		
		int cYear = 0;
		int nYear = 0;
		
		CurrentFinancialPeriod cfp = new CurrentFinancialPeriod();
		if (cfp != null)
		{
			if (cfp.getQuarter() > 1)
			{
				cYear = cfp.getYear() + 1;
				
			} else
			{
				cYear = cfp.getYear();
			}
			nYear    = cYear + 1;
			nYearStr = Integer.toString(nYear);
			
			cFYear = constantFY + cYear + constantSlash + nYearStr.substring(2);
			
		}
		
		return cFYear;
	}
	
	/**
	 * Get Financial Year in String format FY - xxxx/yy for a particular Date Financial Year is in terms of Assement
	 * Year = CFY + 1
	 * 
	 * @param d - Date input - javaUtil.Date
	 * @return - Financial Year in String format
	 */
	public static String getFinancialYearforDate(
	        Date d
	)
	{
		String FYear = null;
		
		String nYearStr = null;
		
		int cYear = 0;
		int nYear = 0;
		
		if (d != null)
		{
			if (d.getMonth() > 3)
			{
				cYear = d.getYear() + 1900 + 1;
			} else
			{
				cYear = d.getYear() + 1900;
			}
			nYear    = cYear + 1;
			nYearStr = Integer.toString(nYear);
			FYear    = constantFY + cYear + constantSlash + nYearStr.substring(2);
		}
		
		return FYear;
	}
	
}
