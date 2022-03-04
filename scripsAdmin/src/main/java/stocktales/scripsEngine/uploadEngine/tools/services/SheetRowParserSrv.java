package stocktales.scripsEngine.uploadEngine.tools.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FieldsParserProp;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FldVals;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISheetRowParserSrv;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

/**
 * 
 * Sheet Row Parser Service
 *
 */
@Service("SheetRowParserSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SheetRowParserSrv implements ISheetRowParserSrv
{
	
	/**
	 * ------------- AUTOWIRED BEANS ------------
	 */
	
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	private FilepathValidationService fpValdSrv;
	
	@Autowired
	private WBFilepathService wbFilePathSrv;
	
	/**
	 * ------------ MY PROPERTIES ----------------
	 */
	
	/**
	 * --------------------- Interface Implementations --------------------
	 */
	@Override
	public FldVals getFldValsbySheet_Field(
	        XSSFSheet sheetRef, FieldsParserProp parserProp
	) throws EX_General
	{
		FldVals fldVals = null;
		
		Row rowVals = null;
		
		if (sheetRef != null && parserProp != null)
		{
			// 1. Get Row for the Sheet Field
			rowVals = this.getRowforFieldName(sheetRef, parserProp.getSheetField());
			
			// 2. Get the values for the field from the row
			if (rowVals != null)
			{
				fldVals = new FldVals();
				fldVals.setFieldName(parserProp.getSheetField());
				fldVals.setFieldVals(this.getValsforRow(sheetRef, rowVals, parserProp));
				
			}
			
		}
		
		return fldVals;
	}
	
	@Override
	public FldVals getFldValsbyWbPathandSheetName(
	        String wbPath, String sheetName, FieldsParserProp parserProp
	) throws EX_General
	{
		FldVals fldVals = null;
		if (wbPath != null && fpValdSrv != null && wbFilePathSrv != null && sheetName != null)
		{
			try
			{
				if (fpValdSrv.validateFilePath(wbPath))
				{
					XSSFWorkbook wbCtxt = wbFilePathSrv.getWBcontextfromFilepath(wbPath);
					if (wbCtxt != null)
					{
						
						XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
						if (sheetRef != null)
						{
							
							fldVals = getFldValsbySheet_Field(sheetRef, parserProp);
						}
					}
					
				}
			} catch (IOException e)
			{
				EX_General egen = new EX_General("FILENOTFOUND", new Object[]
				{ wbPath });
				
				throw egen;
			}
		}
		return fldVals;
	}
	
	@Override
	public FldVals getFldValsbywbCtxtandSheetName(
	        XSSFWorkbook wbCtxt, String sheetName, FieldsParserProp parserProp
	) throws EX_General
	{
		FldVals fldVals = null;
		
		if (wbCtxt != null && sheetName != null)
		{
			
			XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
			if (sheetRef != null)
			{
				
				fldVals = getFldValsbySheet_Field(sheetRef, parserProp);
			}
			
		}
		
		return fldVals;
	}
	
	/**
	 * ----------- PRIVATE METHODS -----------------------
	 */
	
	private Row getRowforFieldName(
	        XSSFSheet sheetRef, String fldName
	) throws EX_General
	{
		Row rowVals = null;
		Row rowCurr = null;
		
		if (fldName != null && sheetRef != null)
		{
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = sheetRef.iterator();
			
			// Scan through Rows
			while ((rowCurr = rowIterator.next()) != null)
			{
				Cell cellOne = rowCurr.getCell(0);
				if (cellOne != null)
				{
					String fldNametoCmp = cellOne.getStringCellValue();
					if (fldNametoCmp != null)
					{
						if (fldNametoCmp.equals(fldName))
						{
							rowVals = rowCurr;
							return rowVals;
						}
					}
				}
				
			}
		}
		
		return rowVals;
	}
	
	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> getValsforRow(
	        XSSFSheet sheetRef, Row rowVals, FieldsParserProp parserProp
	) throws EX_General
	{
		ArrayList<T>     list      = new ArrayList<T>();
		FormulaEvaluator evaluator = null;
		Object           val       = null;
		
		// Get Formula Evaluator
		XSSFWorkbook wbCtxt = sheetRef.getWorkbook();
		if (wbCtxt != null)
		{
			evaluator = wbCtxt.getCreationHelper().createFormulaEvaluator();
		}
		
		Iterator<Cell> cellIterator = rowVals.cellIterator();
		if (cellIterator != null && wbMdtSrv != null)
		{
			int colpos = 0; // Starting Col Pos to take care of specific cells or End positions
			
			while (cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();
				if (colpos >= (parserProp.getColStartPos() - 1) && colpos <= (parserProp.getColEndPos() - 1))
				{
					if (cell != null)
					{
						SheetFieldsMetadata fldMdt = wbMdtSrv.getFieldMetadata(sheetRef.getSheetName(),
						        parserProp.getSheetField());
						if (fldMdt != null)
						{
							list.add((T) getValuefromCell(cell, fldMdt.getDataType(), evaluator));
						}
					}
				}
				colpos++;
			}
			
		}
		
		return list;
	}
	
	@SuppressWarnings(
	    { "unchecked", "unused" }
	)
	private <T> T getValuefromCell(
	        Cell cell, SCEenums.rowDataType dataType, FormulaEvaluator eval
	) throws EX_General
	{
		Object    val       = null;
		CellValue cellValue = null;
		
		try
		
		{
			
			switch (dataType)
			{
				case String:
					val = cell.getStringCellValue();
					break;
				
				case Decimal:
					cellValue = eval.evaluate(cell);
					if (cell.getCellStyle().getDataFormatString().contains("%"))
					{
						DataFormatter formatter = new DataFormatter(Locale.US);
						String        strVal    = formatter.formatCellValue(cell, eval);
						if (strVal != null)
						{
							String[] values = strVal.split("%");
							
							if (values[0] != null)
							{
								
								if ((values[0] == "#DIV/0!") || (values[0] == "#NUM!"))
								{
									val = 0;
								} else
								{
									val = Double.parseDouble(values[0]);
								}
								
							} else
							{
								val = 0;
							}
							
						}
						
					} else
					{
						val = cellValue.getNumberValue();
						if (val == null)
						{
							val = 0;
						}
					}
					
					break;
				
				case Numerical:
					cellValue = eval.evaluate(cell);
					if (cellValue != null)
					{
						if (cell.getCellStyle().getDataFormatString().contains("%"))
						{
							DataFormatter formatter = new DataFormatter(Locale.US);
							String        strVal    = formatter.formatCellValue(cell, eval);
							if (strVal != null)
							{
								String[] values = strVal.split("%");
								
								if (values[0] != null)
								{
									if ((values[0] == "#DIV/0!") || (values[0] == "#NUM!"))
									{
										val = 0;
									} else
									{
										val = Integer.parseInt(values[0]);
									}
								} else
								{
									val = 0;
								}
								
							}
							
						} else
						{
							val = (int) Math.round(cellValue.getNumberValue());
							if ((int) val == 0)
							{
								val = 0; // For a Zero Value Header - Treat as 0 Value here
							}
						}
					}
					break;
				
				case Date:
					if (DateUtil.isCellDateFormatted(cell))
					{
						DataFormatter formatter = new DataFormatter(Locale.US);
						val = formatter.formatCellValue(cell, eval);
						
					}
					break;
				
			}
		}
		
		catch (Exception e)
		{
			
		}
		
		return (T) val;
	}
	
}
