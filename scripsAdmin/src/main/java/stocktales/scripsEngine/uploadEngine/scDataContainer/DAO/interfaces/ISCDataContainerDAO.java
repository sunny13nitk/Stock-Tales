package stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.interfaces;

import java.lang.reflect.Method;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;

public interface ISCDataContainerDAO
{
	public scDataContainer load(
	        String scCode
	) throws EX_General, Exception;
	
	public scDataContainer load(
	        String scCode, String sheetName
	) throws Exception;
	
	public scDataContainer getScDC(
	);
	
	public Method getMethodfromSCDataContainer(
	        String sheetName, char Type
	);
}
