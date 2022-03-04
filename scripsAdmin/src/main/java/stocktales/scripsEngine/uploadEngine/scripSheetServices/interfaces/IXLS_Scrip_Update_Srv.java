package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

public interface IXLS_Scrip_Update_Srv
{
	
	/**
	 * Update Only possible Via WB Context and Not filepath as it has to be routed via IXLS_Scrip_Upload_Srv
	 * 
	 * @param wbCtxt - Work Book Context
	 * @return -Uploaded Flag
	 * @throws EX_General
	 */
	public boolean Update_Scrip_WbContext(
	        XSSFWorkbook wbCtxt
	) throws EX_General;
	
}
