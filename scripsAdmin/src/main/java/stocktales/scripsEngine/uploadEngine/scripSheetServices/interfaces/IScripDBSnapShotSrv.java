package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_General;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SCSheetValStamps;

public interface IScripDBSnapShotSrv
{
	public SCSheetValStamps getScripLatestValsbyScCode(
	        String ScCode
	) throws EX_General;
	
	public SCSheetValStamps getScripLatestValsbyScCode(
	        EN_SC_General scRoot
	) throws EX_General;
	
	public SCSheetValStamps getScripLatestValsbyScDesc(
	        String ScDescBeginsWith
	) throws EX_General;
	
	SCSheetValStamps getScripLatestValsbyScCode(
	        EN_SC_GeneralQ scRoot
	) throws EX_General;
}
