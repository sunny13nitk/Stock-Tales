package stocktales.cagrEval.srv;

import java.util.Calendar;

import org.springframework.stereotype.Service;

import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.helperPoJo.YearsRollOverResults;
import stocktales.cagrEval.intf.IRollOverYrs;

@Service
public class RollOverYrsSrv implements IRollOverYrs
{
	private YearsRollOverResults rolOverRes = null;
	
	private final int baseVal = 2010;
	
	@Override
	public YearsRollOverResults generateRollOverYrs(
	        int baseYr, int intervalsize, int totalLength
	)
	{
		
		//Get Current Year
		int cYear = Calendar.getInstance().get(Calendar.YEAR);
		
		if (baseYr >= baseVal && ((baseYr + totalLength) <= cYear) && (intervalsize < totalLength))
		{
			int maxYear = baseYr + totalLength;
			rolOverRes = new YearsRollOverResults();
			for (int i = baseYr; i <= maxYear; i++)
			{
				int toval = i + intervalsize;
				if (toval <= (maxYear))
				{
					YearsFromTo yEnt = new YearsFromTo(i, toval);
					this.rolOverRes.getRollOverYrs().add(yEnt);
				} else
				{
					this.rolOverRes.setE2eYrs(new YearsFromTo(baseYr, maxYear));
					i = maxYear + 1; //Break out of loop
				}
			}
		}
		
		return rolOverRes;
	}
	
}
