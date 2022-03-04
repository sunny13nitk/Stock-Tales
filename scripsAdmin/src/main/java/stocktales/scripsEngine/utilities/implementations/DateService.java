package stocktales.scripsEngine.utilities.implementations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import stocktales.scripsEngine.utilities.interfaces.IDateService;

@Service
public class DateService implements IDateService
{
	
	@Override
	public int getNumDaysbwSqlSysDates(
	        Date sqlDbDate, Date implDate
	) throws java.text.ParseException
	{
		int  numDays = 0;
		Date sqlDate = null;
		if (sqlDbDate != null && implDate != null)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String     sqlDateStr = sqlDbDate.toString();
			
			sqlDate = dateFormat.parse(sqlDateStr);
			
			long daysDiff = (implDate.getTime() - sqlDate.getTime());
			long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
			long a        = TimeUnit.DAYS.toDays(noOfDays);
			numDays = (int) a;
		}
		
		return numDays;
	}
	
	@Override
	public int getNumDaysbwSysDates(
	        Date fromDate, Date toDate
	) throws ParseException
	{
		int  numDays  = 0;
		long daysDiff = (toDate.getTime() - fromDate.getTime());
		long noOfDays = TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);
		long a        = TimeUnit.DAYS.toDays(noOfDays);
		numDays = (int) a;
		return numDays;
		
	}
	
}
