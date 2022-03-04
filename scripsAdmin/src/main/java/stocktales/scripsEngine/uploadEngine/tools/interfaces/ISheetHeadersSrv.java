package stocktales.scripsEngine.uploadEngine.tools.interfaces;

import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

/**
 * 
 * Get Headers from Sheet as per SheetMetadata Configuration
 *
 */
public interface ISheetHeadersSrv
{
	
	// Headers by Sheet Reference
	public <T> ArrayList<T> getHeadersbySheet(
	        XSSFSheet sheetRef
	) throws EX_General;
	
	// Headers by Work Book Path and Sheet Name
	public <T> ArrayList<T> getHeadersbyWbPathandSheetName(
	        String wbPath, String sheetName
	) throws EX_General;
	
	// Headers by Work Book context Reference and Sheet Name
	public <T> ArrayList<T> getHeadersbywbCtxtandSheetName(
	        XSSFWorkbook wbCtxt, String sheetName
	) throws EX_General;
	
	public int getNonBlankColPosBegin(
	);
	
}
