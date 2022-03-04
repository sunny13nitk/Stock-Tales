package stocktales.scripsEngine.uploadEngine.tools.interfaces;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FieldsParserProp;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FldVals;

/**
 * 
 * Get FldVals from Sheet Row By field
 *
 */
public interface ISheetRowParserSrv
{
	// FldVals by Sheet Reference
	public FldVals getFldValsbySheet_Field(
	        XSSFSheet sheetRef, FieldsParserProp parserProp
	) throws EX_General;
	
	// FldVals by Work Book Path and Sheet Name
	public FldVals getFldValsbyWbPathandSheetName(
	        String wbPath, String sheetName, FieldsParserProp parserProp
	) throws EX_General;
	
	// FldVals by Work Book context Reference and Sheet Name
	public FldVals getFldValsbywbCtxtandSheetName(
	        XSSFWorkbook wbCtxt, String sheetName, FieldsParserProp parserProp
	) throws EX_General;
	
}
