package stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces;

import java.util.List;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;

/**
 * 
 * Scrip exists in DB Service
 */
public interface ISCExistsDB_Srv
{
	public boolean Is_ScripExisting_DB(
	        String scCode
	) throws EX_General;
	
	public EN_SC_GeneralQ Get_ScripExisting_DB(
	        String scCode
	) throws EX_General;
	
	public boolean Is_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General;
	
	public EN_SC_GeneralQ Get_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General;
	
	public List<EN_SC_GeneralQ> getAllScripsHeaders(
	) throws EX_General;
	
	public List<String> getAllScripNames(
	) throws EX_General;
	
	public List<ScripSector> getAllScripSectors(
	) throws EX_General;
	
	public List<String> getAllSectors(
	) throws EX_General;
	
	public String getScripCodeByCodeorDescSW(
	        String searchTerm
	) throws Exception;
	
	public List<String> getAllScripNamesforSector(
	        String sector
	) throws EX_General;
	
}
