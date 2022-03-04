package stocktales.healthcheck.beanSrv.intf;

import java.util.List;

import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;

/*
 * Data Container Service to Act upon Balance sheet  from Session Loaded Scrip Data container
 */
public interface IScDataContSrv
{
	public List<NameVal> getDeltaListforBalSheet(
	        String scCode, String attrName
	) throws Exception;
	
	public boolean isFieldValid(
	        String fldName
	) throws Exception;
}
