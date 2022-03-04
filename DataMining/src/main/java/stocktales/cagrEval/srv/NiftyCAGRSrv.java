package stocktales.cagrEval.srv;

import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.cagrEval.helperPoJo.NiftyCAGRResult;
import stocktales.cagrEval.intf.INiftyCAGRSrv;
import stocktales.cagrEval.model.NiftyTracker;
import stocktales.cagrEval.repo.IRepoNiftyTracker;

@Service
public class NiftyCAGRSrv implements INiftyCAGRSrv
{
	@Autowired
	private IRepoNiftyTracker repoNiftyT;
	
	private final int baseYr = 2010;
	
	private final int maxYr = Calendar.getInstance().get(Calendar.YEAR);
	
	@Override
	public NiftyCAGRResult calculateNiftyCAGR(
	        int fromYear, int toYear
	)
	{
		NiftyCAGRResult result  = null;
		double          fromVal = 0;
		double          toVal   = 0;
		double          CAGR    = 0;
		
		if (fromYear >= baseYr && toYear <= maxYr)
		{
			
			// Get From and TO Nifty Values
			Optional<NiftyTracker> niftyFrom = repoNiftyT.findByYear(fromYear);
			if (niftyFrom.isPresent())
			{
				fromVal = niftyFrom.get().getNiftyvalue();
			}
			
			Optional<NiftyTracker> niftyToBack = repoNiftyT.findByYear(toYear);
			if (niftyToBack.isPresent())
			{
				toVal = niftyToBack.get().getNiftyvalue();
			}
			
			if (fromVal > 0 && toVal > 0)
			{
				CAGR = (Math.pow(toVal / fromVal, 1.0 / (toYear - fromYear)) - 1.0) * 100;
			}
			
			if (CAGR != 0)
			{
				result = new NiftyCAGRResult();
				result.setFrom(new NiftyTracker(fromYear, fromVal));
				result.setTo(new NiftyTracker(toYear, toVal));
				result.setDuration(toYear - fromYear);
				result.setCAGR(Precision.round(CAGR, 1));
			}
			
		}
		return result;
	}
	
	@Override
	public NiftyCAGRResult calculateNiftyCAGRToDate(
	        int fromYear
	)
	{
		NiftyCAGRResult result  = null;
		double          fromVal = 0;
		double          toVal   = 0;
		int             toYear  = 0;
		double          CAGR    = 0;
		
		if (fromYear >= baseYr)
		{
			
			// Get From and TO Nifty Values
			Optional<NiftyTracker> niftyFrom = repoNiftyT.findByYear(fromYear);
			if (niftyFrom.isPresent())
			{
				fromVal = niftyFrom.get().getNiftyvalue();
			}
			
			Optional<NiftyTracker> niftyTo = repoNiftyT.findByYear(maxYr);
			if (niftyTo.isPresent())
			{
				toVal  = niftyTo.get().getNiftyvalue();
				toYear = maxYr;
			} else
			{
				//Last but one if Current Year Nifty Value not Maintained
				niftyTo = repoNiftyT.findByYear(maxYr - 1);
				
				if (niftyTo.isPresent())
				{
					toVal  = niftyTo.get().getNiftyvalue();
					toYear = maxYr - 1;
				}
			}
			
			if (fromVal > 0 && toVal > 0)
			{
				CAGR = (Math.pow(toVal / fromVal, 1.0 / (toYear - fromYear)) - 1.0) * 100;
			}
			
			if (CAGR != 0)
			{
				result = new NiftyCAGRResult();
				result.setFrom(new NiftyTracker(fromYear, fromVal));
				result.setTo(new NiftyTracker(toYear, toVal));
				result.setDuration(toYear - fromYear);
				result.setCAGR(Precision.round(CAGR, 1));
			}
			
		}
		return result;
	}
	
}
