package stocktales.scripsEngine.utilities.types;

/**
 * Base Type for Penultimate QYear
 * To get the Quarter and the YEar Penultimate for financial comparison
 * Utilizes Current Financial Period Object
 */
public class PenultimateQYear
{

	private int	Year;
	private int	Quarter;

	/**
	 * @return the year
	 */
	public int getYear()
	{
		return Year;
	}

	/**
	 * @return the quarter
	 */
	public int getQuarter()
	{
		return Quarter;
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
	 * @param quarter
	 *             the quarter to set
	 */
	public void setQuarter(int quarter)
	{
		Quarter = quarter;
	}

	/**
	 * 
	 */
	public PenultimateQYear()
	{
		CurrentFinancialPeriod cfp = new CurrentFinancialPeriod();

		if (cfp.getQuarter() == 1)
		{
			this.Year = cfp.getYear() - 1;
			this.Quarter = 4;
		}
		else
		{
			this.Year = cfp.getYear();
			this.Quarter = cfp.getQuarter() - 1;
		}

	}

	/**
	 * @param year
	 * @param quarter
	 */
	public PenultimateQYear(int year, int quarter)
	{
		super();
		Year = year;
		Quarter = quarter;
	}

}
