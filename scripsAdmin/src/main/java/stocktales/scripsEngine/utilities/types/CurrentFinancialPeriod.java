package stocktales.scripsEngine.utilities.types;

/**
 * Current Financial Period - type
 * To get finanical period references from Current Time stamp
 */
public class CurrentFinancialPeriod
{

	private int	Year;
	private int	Month;
	private int	Quarter;
	private int	DOM;

	/**
	 * @return the year
	 */
	public int getYear()
	{
		return Year;
	}

	/**
	 * @param year
	 *             the year to set
	 */
	public void setYear(int year)
	{
		Year = year;
	}

	/**
	 * @return the month
	 */
	public int getMonth()
	{
		return Month;
	}

	/**
	 * @param month
	 *             the month to set
	 */
	public void setMonth(int month)
	{
		Month = month;
	}

	/**
	 * @return the quarter
	 */
	public int getQuarter()
	{
		return Quarter;
	}

	/**
	 * @param quarter
	 *             the quarter to set
	 */
	public void setQuarter(int quarter)
	{
		Quarter = quarter;
	}

	/**
	 * @return the dOM
	 */
	public int getDOM()
	{
		return DOM;
	}

	/**
	 * @param dOM
	 *             the dOM to set
	 */
	public void setDOM(int dOM)
	{
		DOM = dOM;
	}

	/**
	 * 
	 */
	public CurrentFinancialPeriod()
	{
		this.Year = java.time.Year.now().getValue();
		this.Month = java.time.LocalDate.now().getMonthValue();
		this.Quarter = getQuarterfromMonth(Month);

	}

	private int getQuarterfromMonth(int Month)
	{
		int qu = 0;

		qu = Month % 3;
		if (qu == 0)
		{
			qu = Month / 3;
		}
		else
		{
			qu = Month / 3 + 1;
		}

		return qu;
	}

}
