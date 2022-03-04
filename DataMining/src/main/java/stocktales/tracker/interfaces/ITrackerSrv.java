package stocktales.tracker.interfaces;

import stocktales.tracker.pojos.ScTrakerResult;

public interface ITrackerSrv
{
	public ScTrakerResult getforScrips(
	        String[] scrips
	) throws Exception;
	
	public ScTrakerResult getforStrategy(
	        int StrategyId
	) throws Exception;
	
}
