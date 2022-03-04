package stocktales.scripsEngine.uploadEngine.tools.interfaces;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_WBRawData;

/**
 * 
 * Sheet Raw Data Service - Interface
 */
public interface IWBRawDataSrv
{
	
	public TY_WBRawData getSheetFldVals(
	        XSSFWorkbook wbCtxt, String sheetName
	) throws EX_General;
	
	public TY_WBRawData getSheetFldVals(
	        String filePath, String sheetName
	) throws EX_General;
	
	public TY_WBRawData getSheetFldVals(
	        String filePath
	) throws EX_General;
	
	public TY_WBRawData getSheetFldVals(
	        XSSFWorkbook wbCtxt
	) throws EX_General;
	
	public TY_WBRawData getSheetFldVals(
	        XSSFSheet sheetRef
	) throws EX_General;
	
}
