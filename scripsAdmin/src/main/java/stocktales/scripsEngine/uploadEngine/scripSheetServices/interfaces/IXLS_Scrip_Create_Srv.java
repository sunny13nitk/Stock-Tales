package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

public interface IXLS_Scrip_Create_Srv
{
	
	/**
	 * Create Only possible Via WB Context and Not filepath as it has to be routed via IXLS_Scrip_Upload_Srv
	 * 
	 * @param wbCtxt - Work Book Context
	 * @return -Uploaded Flag
	 * @throws EX_General
	 */
	public boolean Create_Scrip_WbContext(
	        XSSFWorkbook wbCtxt
	) throws EX_General;
	
	public void saveScripObject(
	);
	
	void saveScripRoot(
	);
	
}
