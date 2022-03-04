package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

/*
 * Scrip Upload Service from XLS File
 */
public interface IXLS_Scrip_Upload_Srv
{
	
	public boolean Upload_Scrip_from_XLS_Filepath(
	        String FilePath
	) throws EX_General;
	
	public boolean Upload_Scrip_from_XLS_WBCtxt(
	        XSSFWorkbook wbCtxt
	) throws EX_General;
	
}
