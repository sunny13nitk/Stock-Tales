package stocktales.cagrEval.intf;

import stocktales.cagrEval.helperPoJo.NiftyCAGRResult;

public interface INiftyCAGRSrv
{
	public NiftyCAGRResult calculateNiftyCAGR(
	        int fromYear, int toYear
	);
	
	public NiftyCAGRResult calculateNiftyCAGRToDate(
	        int fromyear
	);
}
