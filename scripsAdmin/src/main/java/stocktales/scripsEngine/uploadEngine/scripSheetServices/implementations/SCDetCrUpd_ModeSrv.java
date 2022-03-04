package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SC_XLS_CrUpd_Mode;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCCode_Getter_XLS;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCDetCrUpd_Mode;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.validations.implementations.FilepathValidationService;
import stocktales.scripsEngine.uploadEngine.validations.implementations.WBFilepathService;

@Service("SCDetCrUpd_ModeSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SCDetCrUpd_ModeSrv implements ISCDetCrUpd_Mode
{
	@Autowired
	private ISCCode_Getter_XLS scCodeGetSrv;
	
	@Autowired
	private ISCExistsDB_Srv scExistsSrv;
	
	@Autowired
	private FilepathValidationService fpSrv;
	
	@Autowired
	private WBFilepathService wbfpSrv;
	
	// Buffer Service to be added later
	
	@Override
	public SC_XLS_CrUpd_Mode getModeforWB(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		SC_XLS_CrUpd_Mode scMode = new SC_XLS_CrUpd_Mode();
		scMode.setMode(SCEenums.ModeOperation.NONE);
		
		if (wbCtxt != null)
		{
			if (scCodeGetSrv != null && scExistsSrv != null) // Buffer Srv to be added
			{
				String scCode = scCodeGetSrv.getSCCode(wbCtxt);
				if (scCode != null)
				{
					scMode.setSCCode(scCode);
					
					// 1. First check from Buffer if the Scrip Exists - TBD
					
					// 2. Then check from DB - If not found in Buffer/Cache
					
					if (scExistsSrv.Is_ScripExisting_DB(scCode))
					{
						scMode.setMode(SCEenums.ModeOperation.UPDATE);
					} else
					{
						scMode.setMode(SCEenums.ModeOperation.CREATE);
					}
					
				}
			}
		}
		
		return scMode;
	}
	
	@Override
	public SC_XLS_CrUpd_Mode getModeforFilePath(
	        String filePath
	) throws EX_General
	{
		SC_XLS_CrUpd_Mode scMode = new SC_XLS_CrUpd_Mode();
		scMode.setMode(SCEenums.ModeOperation.NONE);
		
		if (filePath != null && fpSrv != null)
		{
			try
			{
				if (fpSrv.validateFilePath(filePath))
				{
					if (wbfpSrv != null)
					{
						XSSFWorkbook wbCtxt = wbfpSrv.getWBcontextfromFilepath(filePath);
						if (wbCtxt != null)
						{
							scMode = this.getModeforWB(wbCtxt);
						}
					}
				}
			} catch (Exception e)
			{
				EX_General egen = new EX_General("ERR_INVALID_FP", new Object[]
				{ filePath, e.getMessage() });
				
				throw egen;
			}
		}
		
		return scMode;
		
	}
	
}
