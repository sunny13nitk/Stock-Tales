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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.enums.SCEenums.rowScanType;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.definitions.EntityListHeadScannerConfig;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IHeadersConvSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISheetHeadersSrv;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

@Service()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SheetHeadersSrv implements ISheetHeadersSrv
{
	
	/**
	 * -- AUTOWIRED BEANS --
	 */
	
	@Autowired
	private ApplicationContext appctxt;
	
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	private FilepathValidationService fpValdSrv;
	
	@Autowired
	private WBFilepathService wbFilePathSrv;
	
	/**
	 * ------- My Properties -------
	 */
	
	private SCSheetMetadata scshtMdt;
	
	private int nonBlankColPosBegin = 0;
	
	/**
	 * -- Interface Methods ---
	 */
	@Override
	public int getNonBlankColPosBegin(
	)
	{
		return nonBlankColPosBegin;
	}
	
	@Override
	public <T> ArrayList<T> getHeadersbySheet(
	        XSSFSheet sheetRef
	) throws EX_General
	{
		ArrayList<T> list = null;
		
		if (sheetRef != null)
		{
			String sheetName = sheetRef.getSheetName();
			
			list = getHeadersforSheet(sheetRef, sheetName);
		}
		return list;
	}
	
	@Override
	public <T> ArrayList<T> getHeadersbyWbPathandSheetName(
	        String wbPath, String sheetName
	) throws EX_General
	{
		ArrayList<T> list = null;
		
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
							
							list = getHeadersforSheet(sheetRef, sheetName);
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
		return list;
	}
	
	@Override
	public <T> ArrayList<T> getHeadersbywbCtxtandSheetName(
	        XSSFWorkbook wbCtxt, String sheetName
	) throws EX_General
	{
		ArrayList<T> list = null;
		
		if (wbCtxt != null && sheetName != null)
		{
			
			XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
			if (sheetRef != null)
			{
				
				list = getHeadersforSheet(sheetRef, sheetName);
			}
			
		}
		
		return list;
		
	}
	
	/**
	 * -------- PRIVATE Implementations ----------------------------
	 */
	
	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> getHeadersforSheet(
	        XSSFSheet sheetRef, String sheetName
	) throws EX_General
	{
		ArrayList<T>                list           = new ArrayList<T>();
		Object                      val            = null;
		Row                         rowVals        = null;
		FormulaEvaluator            evaluator      = null;
		EntityListHeadScannerConfig headScanConfig = null;
		
		XSSFWorkbook wbCtxt = sheetRef.getWorkbook();
		if (wbCtxt != null)
		{
			evaluator = wbCtxt.getCreationHelper().createFormulaEvaluator();
		}
		
		if (this.wbMdtSrv != null && sheetName != null && sheetRef != null && evaluator != null)
		{
			// Get Sheet Metadata
			this.scshtMdt = wbMdtSrv.getMetadataforSheet(sheetName);
			if (scshtMdt != null)
			{
				headScanConfig = scshtMdt.getHeadScanConfig();
				if (headScanConfig != null)
				{
					
					Row rowToScan = sheetRef.getRow(headScanConfig.getRowToScan() - 1);
					if (rowToScan != null)
					{
						
						// Null Value Scan Provisions
						if (headScanConfig.isCheckNullVals() && (headScanConfig.getNullValRowPos() - 1) >= 0)
						{
							rowVals = sheetRef.getRow(headScanConfig.getNullValRowPos() - 1);
						}
						
						// For each row, iterate through each columns
						Iterator<Cell> cellIterator = rowToScan.cellIterator();
						if (cellIterator != null)
						{
							int colpos = 0; // Starting Col Pos to take care of specific cells or End positions
							
							while (cellIterator.hasNext())
							{
								Cell cell = cellIterator.next();
								
								// Continous Head Scan
								if (headScanConfig.getRowScanType() == rowScanType.Continous)
								{
									
									if (
									    colpos >= (headScanConfig.getColToScanStart() - 1)
									            && colpos <= (headScanConfig.getColToScanEnd() - 1)
									)
									{
										if (cell != null)
										{
											// Null Values Check Configured - Only allow Headers in Case of
											// Non null Row Vals
											if (headScanConfig.isCheckNullVals())
											{
												// Get the value from Value SCan Row Current Column
												if (rowVals != null)
												{
													Cell cellVal = rowVals.getCell(colpos);
													val = this.getValuefromCell(cellVal,
													        headScanConfig.getValrowDataType(), evaluator);
													if (val != null)
													{
														list.add((T) getValuefromCell(cell,
														        headScanConfig.getRowDataType(), evaluator));
													} else
													{
														// 2 is added to accomodate for practical col
														// header count
														nonBlankColPosBegin = colpos + 2;
													}
												}
												
											} else
											{
												// Directly get the value
												list.add((T) getValuefromCell(cell, headScanConfig.getRowDataType(),
												        evaluator));
											}
										}
									}
									
								} else // Specific Cell Scan
								{
									if (
									    colpos == (headScanConfig.getColToScanStart() - 1)
									            || colpos == (headScanConfig.getColToScanEnd() - 1)
									)
									{
										if (cell != null)
										{
											// Null Values Check Configured - Only allow Headers in Case of
											// Non null Row Vals
											if (headScanConfig.isCheckNullVals())
											{
												// Get the value from Value SCan Row Current Column
												if (rowVals != null)
												{
													Cell cellVal = rowVals.getCell(colpos);
													val = this.getValuefromCell(cellVal,
													        headScanConfig.getValrowDataType(), evaluator);
													if (val != null)
													{
														list.add((T) getValuefromCell(cell,
														        headScanConfig.getRowDataType(), evaluator));
													} else
													{
														nonBlankColPosBegin = colpos + 1;
													}
													
												}
												
											} else
											{
												// Directly get the value
												list.add((T) getValuefromCell(cell, headScanConfig.getRowDataType(),
												        evaluator));
											}
										}
									}
								}
								colpos++;
							}
							
						}
					}
					
				}
			}
			
		}
		
		/**
		 * Perform Header Value Conversions - In case Headers Conversion Bean Specified
		 */
		
		if (list != null)
		{
			if (list.size() > 0)
			{
				if (headScanConfig.getHeaderValsConvBean() != null && appctxt != null)
				{
					IHeadersConvSrv hdrConvSrv = (IHeadersConvSrv) appctxt
					        .getBean(headScanConfig.getHeaderValsConvBean());
					if (hdrConvSrv != null)
					{
						list = hdrConvSrv.convertHeaderVals(list);
					}
				}
			}
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
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
								val = Double.parseDouble(values[0]);
							}
							
						}
						
					} else
					{
						val = cellValue.getNumberValue();
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
									val = Integer.parseInt(values[0]);
								}
								
							}
							
						} else
						{
							val = (int) Math.round(cellValue.getNumberValue());
							if ((int) val == 0)
							{
								val = null; // For a Zero Value Header - Treat as Null Scenario
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
