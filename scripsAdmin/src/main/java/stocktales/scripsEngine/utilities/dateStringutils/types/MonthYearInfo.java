package stocktales.scripsEngine.utilities.dateStringutils.types;

import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.monthsConfig;

public class MonthYearInfo
{
	
	private monthsConfig monthInfo;
	
	private int Year;
	
	public monthsConfig getMonthInfo(
	)
	{
		return monthInfo;
	}
	
	public void setMonthInfo(
	        monthsConfig monthInfo
	)
	{
		this.monthInfo = monthInfo;
	}
	
	public int getYear(
	)
	{
		return Year;
	}
	
	public void setYear(
	        int year
	)
	{
		Year = year;
	}
	
	public MonthYearInfo(
	        monthsConfig monthInfo, int year
	)
	{
		super();
		this.monthInfo = monthInfo;
		Year           = year;
	}
	
	public MonthYearInfo(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
