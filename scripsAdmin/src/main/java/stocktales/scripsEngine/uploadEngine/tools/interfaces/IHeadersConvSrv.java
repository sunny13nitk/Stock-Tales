package stocktales.scripsEngine.uploadEngine.tools.interfaces;

import java.util.ArrayList;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

/**
 * 
 * Headers Conversion Service
 */
public interface IHeadersConvSrv
{
	public <T> ArrayList<T> convertHeaderVals(
	        ArrayList<T> unConvHeaders
	) throws EX_General;
	
}
