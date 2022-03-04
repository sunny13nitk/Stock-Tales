package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.interfaces.ISCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_General;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SCSheetValStamps;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.SheetValStamp;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IScripDBSnapShotSrv;

@Service("ScripDBSnapShotSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScripDBSnapShotSrv implements IScripDBSnapShotSrv
{
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private ISCWBMetadataSrv wbMdtSrv;
	
	private EN_SC_GeneralQ scRoot;
	
	/**
	 * ---------------- INTERFACE IMPLEMENTATIONS ----------------------------
	 */
	
	@Override
	public SCSheetValStamps getScripLatestValsbyScCode(
	        String ScCode
	) throws EX_General
	{
		SCSheetValStamps scValStamps = null;
		
		if (ScCode != null && scExSrv != null)
		{
			this.scRoot = scExSrv.Get_ScripExisting_DB(ScCode);
			if (scRoot != null)
			{
				try
				{
					
				} catch (Exception e)
				{
					EX_General egen = new EX_General("ERR_SC_DBSTAMP", new Object[]
					{ ScCode, e.getMessage() });
					
					throw egen;
				}
			}
		}
		
		return scValStamps;
	}
	
	@Override
	public SCSheetValStamps getScripLatestValsbyScDesc(
	        String ScDescBeginsWith
	) throws EX_General
	{
		SCSheetValStamps scValStamps = null;
		
		if (ScDescBeginsWith != null && scExSrv != null)
		{
			this.scRoot = scExSrv.Get_ScripExisting_DB_DescSW(ScDescBeginsWith);
			if (scRoot != null)
			{
				try
				{
					
				} catch (Exception e)
				{
					EX_General egen = new EX_General("ERR_SC_DBSTAMP", new Object[]
					{ ScDescBeginsWith, e.getMessage() });
					
					throw egen;
				}
			}
		}
		
		return scValStamps;
	}
	
	@Override
	public SCSheetValStamps getScripLatestValsbyScCode(
	        EN_SC_GeneralQ scRoot
	) throws EX_General
	{
		SCSheetValStamps scValStamps = null;
		
		if (scRoot != null)
		{
			this.scRoot = scRoot;
			if (scRoot != null)
			{
				try
				{
					
				} catch (Exception e)
				{
					EX_General egen = new EX_General("ERR_SC_DBSTAMP", new Object[]
					{ scRoot.getSCCode(), e.getMessage() });
					
					throw egen;
				}
			}
		}
		
		return scValStamps;
	}
	
	/**
	 * ----------------- PRIVATE METHODS -------------------------
	 * 
	 * @throws EX_InvalidRelationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * 
	 */
	
	private SCSheetValStamps getSnapShotfromScRoot(
	) throws EX_General
	{
		SCSheetValStamps scValStamps = null;
		String           headerFld   = null;
		
		if (wbMdtSrv != null)
		{
			scValStamps = new SCSheetValStamps();
			
			scValStamps.setScCode(scRoot.getSCCode());
			
			// For UpdateHeaderDelta Mode sheets
			
			for (SCSheetMetadata shMdt : wbMdtSrv.getWbMetadata().getSheetMetadata())
			{
				headerFld = null;
				if (shMdt.isUpdHeaderDeltaMode())
				{
					SheetValStamp shvalStamp = new SheetValStamp();
					shvalStamp.setSheetName(shMdt.getSheetName());
					headerFld = shMdt.getHeadScanConfig().getObjField();
					String relName = wbMdtSrv.getRelationNameforSheetName(shMdt.getSheetName());
					if (relName != null)
					{
						
						// Get Object Information from Object Factory
						/*
						 * Object_Info objInfo = FrameworkManager.getObjectsInfoFactory()
						 * .Get_ObjectInfo_byName(shMdt.getBobjName()); ArrayList<DependantObject> relEntList =
						 * scRoot.getRelatedEntities(relName); if (relEntList != null) { if (relEntList.size() > 0) { //
						 * Get Last Entity DependantObject depEntity = relEntList.get((relEntList.size() - 1)); if
						 * (depEntity != null) { // GEt the Getter for Current Object Method getM =
						 * objInfo.Get_Getter_for_FieldName(headerFld); if (getM != null) { // Invoke getter for Last
						 * entity in Related Entities Collection shvalStamp.setValue(getM.invoke(depEntity, new Object[]
						 * {})); scValStamps.getSheetVals().add(shvalStamp); } } } }
						 */
					}
					
				}
			}
			
		}
		return scValStamps;
	}
	
	@Override
	public SCSheetValStamps getScripLatestValsbyScCode(
	        EN_SC_General scRoot
	) throws EX_General
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
