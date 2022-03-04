package stocktales.scripsEngine.uploadEngine.tools.services;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.T;
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
import stocktales.scripsEngine.uploadEngine.tools.definitions.FieldsParserProp;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FldVals;
import stocktales.scripsEngine.uploadEngine.tools.definitions.SheetFldValsHeadersList;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_SingleCard_SheetRawData;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_WBRawData;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISheetHeadersSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISheetRowParserSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISingleCard_SheetDataParserSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IWBRawDataSrv;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

/**
 * 
 * SHEET RAW Data Extraction Service - Prototype Bean
 */
@Service()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WBRawDataSrv implements IWBRawDataSrv
{
	
	/*--- AUTOWIRED Beans--- */
	
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	private FilepathValidationService fpValdSrv;
	
	@Autowired
	private WBFilepathService wbFilePathSrv;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	/* ----------INTERFACE METHODS----------- */
	@Override
	public TY_WBRawData getSheetFldVals(
	        XSSFWorkbook wbCtxt, String sheetName
	) throws EX_General
	{
		TY_WBRawData wbRawData = null;
		
		if (wbCtxt != null && sheetName != null)
		{
			XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
			if (sheetRef != null)
			{
				wbRawData = this.getSheetFldVals(sheetRef);
			}
		}
		
		return wbRawData;
	}
	
	@Override
	public TY_WBRawData getSheetFldVals(
	        String filePath, String sheetName
	) throws EX_General
	{
		TY_WBRawData wbRawData = null;
		
		if (filePath != null && sheetName != null && fpValdSrv != null && wbFilePathSrv != null)
		{
			try
			{
				if (fpValdSrv.validateFilePath(filePath))
				{
					XSSFWorkbook wbCtxt = wbFilePathSrv.getWBcontextfromFilepath(filePath);
					if (wbCtxt != null)
					{
						
						XSSFSheet sheetRef = wbCtxt.getSheet(sheetName);
						if (sheetRef != null)
						{
							
							wbRawData = getSheetFldVals(sheetRef);
						}
					}
					
				}
			} catch (IOException e)
			{
				EX_General egen = new EX_General("FILENOTFOUND", new Object[]
				{ filePath });
				
				throw egen;
			}
		}
		
		return wbRawData;
	}
	
	@Override
	public TY_WBRawData getSheetFldVals(
	        String filePath
	) throws EX_General
	{
		TY_WBRawData wbSheetsRawData = null;
		
		if (filePath != null && fpValdSrv != null && wbFilePathSrv != null)
		{
			try
			{
				if (fpValdSrv.validateFilePath(filePath))
				{
					XSSFWorkbook wbCtxt = wbFilePathSrv.getWBcontextfromFilepath(filePath);
					if (wbCtxt != null)
					{
						
						wbSheetsRawData = getSheetFldVals(wbCtxt);
						
					}
					
				}
			} catch (IOException e)
			{
				EX_General egen = new EX_General("FILENOTFOUND", new Object[]
				{ filePath });
				
				throw egen;
			}
		}
		
		return wbSheetsRawData;
	}
	
	@Override
	public TY_WBRawData getSheetFldVals(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		TY_WBRawData wbSheetsRawData = new TY_WBRawData();
		
		if (wbCtxt != null && wbMdtSrv != null)
		{
			for (int i = 0; i < wbCtxt.getNumberOfSheets(); i++)
			{
				XSSFSheet sheetRef = wbCtxt.getSheetAt(i);
				if (sheetRef != null)
				{
					
					SCSheetMetadata shMdt = wbMdtSrv.getMetadataforSheet(sheetRef.getSheetName());
					
					if (shMdt != null)
					{
						SheetFldValsHeadersList    sheetRawData           = null;
						TY_SingleCard_SheetRawData singleCardSheetRawData = null;
						if (shMdt.isCollection())
						{
							sheetRawData = this.getCollSheetFldVals(sheetRef);
							if (sheetRawData != null)
							{
								wbSheetsRawData.getCollSheetsRawData().add(sheetRawData);
							}
						} else
						{
							singleCardSheetRawData = this.getNonCollSheetFldVals(sheetRef);
							if (singleCardSheetRawData != null)
							{
								wbSheetsRawData.getNonCollSheetsRawData().add(singleCardSheetRawData);
							}
						}
						
					}
				}
				
			}
		}
		
		return wbSheetsRawData;
		
	}
	
	@Override
	public TY_WBRawData getSheetFldVals(
	        XSSFSheet sheetRef
	) throws EX_General
	{
		TY_WBRawData wbSheetsRawData = new TY_WBRawData();
		
		if (sheetRef != null && wbMdtSrv != null)
		{
			
			SCSheetMetadata shMdt = wbMdtSrv.getMetadataforSheet(sheetRef.getSheetName());
			
			if (shMdt != null)
			{
				SheetFldValsHeadersList    sheetRawData           = null;
				TY_SingleCard_SheetRawData singleCardSheetRawData = null;
				if (shMdt.isCollection())
				{
					sheetRawData = this.getCollSheetFldVals(sheetRef);
					if (sheetRawData != null)
					{
						wbSheetsRawData.getCollSheetsRawData().add(sheetRawData);
					}
				} else
				{
					singleCardSheetRawData = this.getNonCollSheetFldVals(sheetRef);
					if (singleCardSheetRawData != null)
					{
						wbSheetsRawData.getNonCollSheetsRawData().add(singleCardSheetRawData);
					}
				}
				
			}
		}
		
		return wbSheetsRawData;
	}
	
	/**
	 * ------------------------------- PRIVATE SECTION-----------------------
	 */
	
	private SheetFldValsHeadersList getCollSheetFldVals(
	        XSSFSheet sheetRef
	) throws EX_General
	{
		SheetFldValsHeadersList fldvalsList = null;
		SCSheetMetadata         shMdt       = null;
		ArrayList<T>            headers     = null;
		FldVals                 fldVals     = null;
		
		if (sheetRef != null)
		{
			if (wbMdtSrv != null)
			{
				// 1. Get Sheet Metadata
				shMdt = wbMdtSrv.getMetadataforSheet(sheetRef.getSheetName());
				if (shMdt != null && appCtxt != null)
				{
					
					// 2. Get Sheet Headers
					ISheetHeadersSrv shtHdrSrv = appCtxt.getBean(ISheetHeadersSrv.class);
					if (shtHdrSrv != null)
					{
						headers = shtHdrSrv.getHeadersbySheet(sheetRef);
						if (headers != null)
						{
							if (headers.size() > 0)
							{
								fldvalsList = new SheetFldValsHeadersList();
								fldvalsList.setSheetName(sheetRef.getSheetName());
								fldvalsList.setHeaders(headers);
								// 3. Get Fld Vals for all the fields
								
								for (SheetFieldsMetadata fldMdt : shMdt.getFldsMdt())
								{
									
									// 3.a. Prepare FieldsParserProp for Each fields
									
									int nonblankPos = 0;
									
									if (
									    shMdt.getHeadScanConfig().getColToScanStart() > shtHdrSrv
									            .getNonBlankColPosBegin()
									)
									{
										nonblankPos = shMdt.getHeadScanConfig().getColToScanStart();
									} else
									{
										nonblankPos = shtHdrSrv.getNonBlankColPosBegin();
									}
									
									FieldsParserProp fpp = new FieldsParserProp(fldMdt.getSheetField(), nonblankPos,
									        shMdt.getHeadScanConfig().getColToScanEnd());
									if (fpp != null)
									{
										ISheetRowParserSrv shRowPSrv = appCtxt.getBean(ISheetRowParserSrv.class);
										if (shRowPSrv != null)
										{
											fldVals = shRowPSrv.getFldValsbySheet_Field(sheetRef, fpp);
											if (fldVals != null)
											{
												if (fldVals.getFieldVals() != null)
												{
													if (fldVals.getFieldVals().size() > 0)
													{
														fldvalsList.getFldValList().add(fldVals);
													}
												}
											}
											
										}
									}
									
								}
								
							}
						}
					}
					
				}
				
			}
		}
		
		return fldvalsList;
	}
	
	private TY_SingleCard_SheetRawData getNonCollSheetFldVals(
	        XSSFSheet sheetRef
	) throws EX_General
	{
		TY_SingleCard_SheetRawData ssRawData = null;
		
		/**
		 * Get Single Cardinality Sheet Data Parser Service Bean to retrive the values
		 */
		
		if (appCtxt != null)
		{
			ISingleCard_SheetDataParserSrv ssDpSrv = appCtxt.getBean(ISingleCard_SheetDataParserSrv.class);
			if (ssDpSrv != null)
			{
				ssRawData = ssDpSrv.getFldValsbySheetRef(sheetRef);
			}
		}
		
		return ssRawData;
	}
	
}
