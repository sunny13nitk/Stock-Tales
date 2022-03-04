package stocktales.scripsEngine.uploadEngine.tools.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_AttribVal;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_SingleCard_SheetRawData;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISingleCard_SheetDataParserSrv;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleCard_SheetDataParserSrv implements ISingleCard_SheetDataParserSrv
{
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	private FilepathValidationService fpValdSrv;
	
	@Autowired
	private WBFilepathService wbFilePathSrv;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@Override
	public TY_SingleCard_SheetRawData getFldValsbyWbPathandSheetName(
	        String wbPath, String sheetName
	) throws EX_General
	{
		TY_SingleCard_SheetRawData nonCollSheetRawData = null;
		
		if (wbPath != null && sheetName != null && fpValdSrv != null && wbFilePathSrv != null)
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
							
							nonCollSheetRawData = getFldValsbySheetRef(sheetRef);
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
		
		return nonCollSheetRawData;
	}
	
	@Override
	public TY_SingleCard_SheetRawData getFldValsbywbCtxtandSheetName(
	        XSSFWorkbook wbCtxt, String sheetName
	) throws EX_General
	{
		TY_SingleCard_SheetRawData nonCollSheetRawData = null;
		if (wbCtxt != null && sheetName != null)
		{
			XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
			if (sheetRef != null)
			{
				nonCollSheetRawData = this.getFldValsbySheetRef(sheetRef);
			}
		}
		
		return nonCollSheetRawData;
	}
	
	@Override
	public TY_SingleCard_SheetRawData getFldValsbySheetRef(
	        XSSFSheet sheetRef
	) throws EX_General
	{
		TY_SingleCard_SheetRawData nonCollSheetRawData = null;
		
		if (sheetRef != null && wbMdtSrv != null)
		{
			// Validate from Metadata if this is a single Entity Sheet and not a Collection
			SCSheetMetadata scMdt = wbMdtSrv.getMetadataforSheet(sheetRef.getSheetName());
			if (scMdt != null)
			{
				if (!scMdt.isCollection())
				{
					nonCollSheetRawData = new TY_SingleCard_SheetRawData();
					nonCollSheetRawData.setSheetName(sheetRef.getSheetName());
					nonCollSheetRawData.setSheetRawData(this.getFldVals(sheetRef, scMdt));
				} else
				{
					EX_General egen = new EX_General("ERR_COLL_SHEET", new Object[]
					{ sheetRef.getSheetName() });
					
					throw egen;
				}
			}
			
		}
		
		return nonCollSheetRawData;
	}
	
	/**
	 * --------------------------- PRIVATE SECTION ---------------------------------
	 */
	
	private ArrayList<TY_AttribVal> getFldVals(
	        XSSFSheet sheetRef, SCSheetMetadata shMdt
	) throws EX_General
	{
		ArrayList<TY_AttribVal> attrVals  = new ArrayList<TY_AttribVal>();
		FormulaEvaluator        evaluator = null;
		Row                     rowCurr   = null;
		
		if (sheetRef != null && shMdt != null)
		{
			evaluator = sheetRef.getWorkbook().getCreationHelper().createFormulaEvaluator();
			
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = sheetRef.iterator();
			
			try
			{
				// Scan through Rows
				while ((rowCurr = rowIterator.next()) != null)
				{
					Cell cellOne = rowCurr.getCell(0);
					if (cellOne != null)
					{
						String fldNametoCmp = cellOne.getStringCellValue();
						if (fldNametoCmp != null)
						{
							// Get field Metadata from Sheet Metadata
							try
							{
								SheetFieldsMetadata fldMdt = shMdt.getFldsMdt().stream()
								        .filter(x -> x.getSheetField().equals(fldNametoCmp)).findFirst().get();
								if (fldMdt != null)
								{
									TY_AttribVal attrVal = new TY_AttribVal(fldMdt.getObjField(), fldNametoCmp, null,
									        fldMdt.getDataType());
									attrVal.setValue(this.getValueforField(fldMdt, rowCurr.getCell(1), evaluator));
									
									if (attrVal.getValue() != null)
									{
										attrVals.add(attrVal);
									}
								}
							} catch (NoSuchElementException e)
							{
								// Do nothing simply Pass through to Next Row for Next Field
							}
						}
						
					}
				}
				
			}
			
			catch (NoSuchElementException e)
			{
				// Do nothing
			}
			
		}
		
		return attrVals;
	}
	
	private Object getValueforField(
	        SheetFieldsMetadata fldMdt, Cell cell, FormulaEvaluator eval
	) throws EX_General
	{
		Object val = null;
		
		CellValue cellValue = null;
		
		try
		
		{
			
			switch (fldMdt.getDataType())
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
		
		return val;
	}
	
}
