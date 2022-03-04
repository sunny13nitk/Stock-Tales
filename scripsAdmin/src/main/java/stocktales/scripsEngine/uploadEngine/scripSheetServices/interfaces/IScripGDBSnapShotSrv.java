package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import java.util.ArrayList;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SCSheetValStamps;

public interface IScripGDBSnapShotSrv
{
	public ArrayList<SCSheetValStamps> getScripsDBSnapShot(
	) throws EX_General;
	
}
