package stocktales.cagrEval.intf;

import stocktales.cagrEval.helperPoJo.BalSheetSrvParam;

public interface IBalSheetUtilSrv
{
	public double getFromBalSheetByParam(
	        BalSheetSrvParam balSheetParam
	) throws Exception;
}
