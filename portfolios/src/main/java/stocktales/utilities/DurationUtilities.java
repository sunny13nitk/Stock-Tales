package stocktales.utilities;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class DurationUtilities
{
	public static List<Integer> getYearsList(
	        int interval
	)
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
}
