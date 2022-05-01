package stocktales.durations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import stocktales.helperpojos.DateIntervals;
import stocktales.helperpojos.TY_YearFromTo;

public class UtilDurations
{
	public static List<Integer> getYearsList(int interval)
	{
		List<Integer> years = new ArrayList<Integer>();

		int year = Year.now().getValue();

		if (interval > 0)
		{
			for (int i = 0; i < interval; i++)
			{
				years.add(year);
				year++;
			}

		} else if (interval < 0)
		{
			for (int i = interval; i < 0; i++)
			{
				years.add(year);
				year--;
			}

		} else
		{
			years.add(year);
		}

		return years;
	}

	public static int getNumDaysbwSysDates(Date fromDate, Date toDate) throws ParseException
	{
		int numDays = 0;
		long daysDiff = (toDate.getTime() - fromDate.getTime());
		long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
		long a = TimeUnit.DAYS.toDays(noOfDays);
		numDays = (int) a;
		return numDays;

	}

	public static int getNumDaysbwSqlSysDates(Date sqlDbDate, Date implDate) throws java.text.ParseException
	{
		int numDays = 0;
		Date sqlDate = null;
		if (sqlDbDate != null && implDate != null)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String sqlDateStr = sqlDbDate.toString();

			sqlDate = dateFormat.parse(sqlDateStr);

			long daysDiff = (implDate.getTime() - sqlDate.getTime());
			long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
			long a = TimeUnit.DAYS.toDays(noOfDays);
			numDays = (int) a;
		}

		return numDays;
	}

	public static int getCurrentQuarter()
	{
		int Qtr = 0;

		LocalDate currentdate = LocalDate.now();
		Month currentMonth = currentdate.getMonth();
		int monVal = currentMonth.getValue();

		if (monVal >= 1 && monVal <= 3)
		{
			Qtr = 1;
		} else if (monVal > 3 && monVal <= 6)
		{
			Qtr = 2;
		} else if (monVal > 6 && monVal <= 9)
		{
			Qtr = 3;
		} else if (monVal > 9 && monVal <= 12)
		{
			Qtr = 4;
		}

		return Qtr;
	}

	/**
	 * Get FRom and TO Years From Current Year
	 * 
	 * @param sincelast - Positive Value of # Years to Traverse in Past
	 * @return
	 */
	public static TY_YearFromTo getYearsFromHistory(int sincelast)
	{
		TY_YearFromTo yearFromTo = new TY_YearFromTo();

		int year = Year.now().getValue();

		int currQ = getCurrentQuarter();

		if (currQ > 2) // Usually Financial Results updated after Q2
		{
			yearFromTo.setYearTo(year);
		} else
		{
			// Results Not published and updated , consider penultimate Year
			yearFromTo.setYearTo(year - 1);
		}

		yearFromTo.setYearFrom(yearFromTo.getYearTo() - sincelast);

		return yearFromTo;
	}

	/*
	 * -- convert 2020|4 to Dec-2020
	 */
	public static String getQuarterNamefromNumber(String Quarter)
	{
		String qtrName = null;

		if (Quarter != null)
		{
			if (Quarter.trim().length() > 0)
			{
				String[] parts = Quarter.split("\\|");
				if (parts.length == 2)
				{
					qtrName = new String();
					switch (parts[1].charAt(0))
					{
					case '1':
						qtrName += "Mar-";
						break;
					case '2':
						qtrName += "Jun-";
						break;

					case '3':
						qtrName += "Sep-";
						break;

					case '4':
						qtrName += "Dec-";
						break;

					}

					qtrName += parts[0];
				}
			}
		}

		return qtrName;

	}

	/**
	 * Get List <Date From, Date To > w.r.t Current Date for N numbers of months
	 * back since
	 * 
	 * @param num - Number of Months to Traverse back
	 * @return - List of Type [ fromdate, todate]
	 */
	public static List<DateIntervals> getDateSlotsfromNumMonthsBacktoTodaybyWeeklyIntervals(int num)
	{

		List<DateIntervals> dateIntvs = new ArrayList<DateIntervals>();

		return dateIntvs;
	}

	/**
	 * Get only the Date- Time set to zero for direct date Comparison
	 * 
	 * @return
	 */
	public static Date getTodaysDateOnly()
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();

		java.util.Date date = new java.util.Date(millis);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();

		return date;
	}

	public static Date getDateOnly4mCalendar(Calendar calSource)
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();

		java.util.Date date = new java.util.Date(millis);

		Calendar cal = Calendar.getInstance();
		cal.setTime(calSource.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();

		return date;
	}

	public static Calendar getTodaysCalendarDateOnly()
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();

		java.util.Date date = new java.util.Date(millis);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	/**
	 * Get the Date however with Time Stamp to current millis
	 * 
	 * @return
	 */
	public static Date getTodaysDate()
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();

		java.util.Date date = new java.util.Date(millis);

		return date;
	}

}
