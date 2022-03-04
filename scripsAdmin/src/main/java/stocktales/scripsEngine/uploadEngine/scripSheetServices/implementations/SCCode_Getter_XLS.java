package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.BaseSheetKey;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCCode_Getter_XLS;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

@Service("SCCode_Getter_XLS")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SCCode_Getter_XLS implements ISCCode_Getter_XLS
{
	@Autowired
	private FilepathValidationService fpVSrv;
	
	@Autowired
	private SCWBMetadataSrv scMdtSrv;
	
	@Autowired
	private WBFilepathService wbFilePathSrv;
	
	@Override
	public String getSCCode(
	        String FilePath
	) throws EX_General
	{
		String scCode = null;
		
		if (FilePath != null)
		{
			if (fpVSrv != null && wbFilePathSrv != null)
			{
				try
				{
					if (fpVSrv.validateFilePath(FilePath))
					{
						XSSFWorkbook wbCtxt = wbFilePathSrv.getWBcontextfromFilepath(FilePath);
						if (wbCtxt != null)
						{
							scCode = this.getSCCode(wbCtxt);
						}
					}
				} catch (Exception e)
				{
					EX_General egen = new EX_General("FILENOTFOUND", new Object[]
					{ FilePath });
					
					throw egen;
				}
				
			}
		}
		
		return scCode;
	}
	
	@Override
	public String getSCCode(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		String scCode  = null;
		Row    rowCurr = null;
		int    colPos;
		
		if (wbCtxt != null && scMdtSrv != null)
		{
			BaseSheetKey bshKey = scMdtSrv.getBaseSheetKey();
			if (bshKey != null)
			{
				XSSFSheet genSheetRef = wbCtxt.getSheet(bshKey.getBaseSheetName());
				if (genSheetRef != null)
				{
					if (bshKey.getSheetfieldName() != null)
					{
						// Get iterator to all the rows in current sheet
						Iterator<Row> rowIterator = genSheetRef.iterator();
						// Scan through Rows
						while (((rowCurr = rowIterator.next()) != null) && scCode == null)
						{
							Cell cellOne = rowCurr.getCell(0);
							if (cellOne != null)
							{
								String fldNametoCmp = cellOne.getStringCellValue();
								if (fldNametoCmp != null)
								{
									if (fldNametoCmp.equals(bshKey.getSheetfieldName()))
									{
										colPos = cellOne.getColumnIndex();
										// Key field Found
										// Get the Value for the Key field from subsequent Column
										Cell cell = rowCurr.getCell(++colPos);
										if (cell != null)
										{
											scCode = cell.getStringCellValue();
										}
										
									}
								}
							}
							
						}
						
					}
				}
			}
			
		}
		
		return scCode;
	}
	
}
