package stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces;

import java.lang.reflect.Method;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;

public interface ISCDataContainerSrv
{

	public void load(String scCode) throws EX_General, Exception;

	public void load(String scCode, String sheetName) throws Exception;

	public scDataContainer getScDC();

	public Method getMethodfromSCDataContainer(String sheetName, char Type);

	public String getLatestQDateForScrip(String scCode) throws Exception;
}
