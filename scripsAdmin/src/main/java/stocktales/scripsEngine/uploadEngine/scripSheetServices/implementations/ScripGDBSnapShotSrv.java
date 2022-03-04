package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.services.interfaces.ISCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SCSheetValStamps;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IScripDBSnapShotSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IScripGDBSnapShotSrv;

@Service("ScripGDBSnapShotSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScripGDBSnapShotSrv implements IScripGDBSnapShotSrv
{
	
	@Autowired
	private IScripDBSnapShotSrv scDBSSSrv;
	
	@Autowired
	private ISCWBMetadataSrv wbMdtSrv;
	
	@Override
	public ArrayList<SCSheetValStamps> getScripsDBSnapShot(
	) throws EX_General
	{
		
		ArrayList<SCSheetValStamps> scSheetValStamps = null;
		
		if (wbMdtSrv != null && scDBSSSrv != null)
		{
			
			// 1. Get the Scrips saved in DB
			try
			{
				/*
				 * IQueryService qs = (IQueryService) QueryManager
				 * .getQuerybyRootObjname(wbMdtSrv.getMetadataforBaseSheet().getBobjName()); if (qs != null) {
				 * ArrayList<EN_SC_General> scRoots = qs.executeQuery(); if (scRoots != null) { if (scRoots.size() > 0)
				 * { scSheetValStamps = new ArrayList<SCSheetValStamps>(); for (EN_SC_General scRoot : scRoots) {
				 * scSheetValStamps.add(scDBSSSrv.getScripLatestValsbyScCode(scRoot)); } } } }
				 */
			} catch (Exception e)
			{
				EX_General egen = new EX_General("ERR_SCDBSS_LOAD", new Object[]
				{ e.getMessage() });
				
				throw egen;
			}
			
		}
		
		return scSheetValStamps;
	}
	
}
