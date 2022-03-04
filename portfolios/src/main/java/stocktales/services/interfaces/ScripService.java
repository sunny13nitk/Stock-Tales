package stocktales.services.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

public interface ScripService
{
	public boolean isScripBelongingToFinancialSector(
	        String scCode
	);
	
	public EN_SC_GeneralQ getScripHeader(
	        String scCode
	);
	
	public List<EN_SC_GeneralQ> getAllScripsHeaders(
	) throws EX_General;
	
	public List<String> getAllScripNames(
	) throws EX_General;
	
	public List<String> getAllScripNamesforSector(
	        String sector
	) throws EX_General;
	
	public List<SC_EDRC_Summary> getEDRCSummaryforAll(
	);
	
	public String getSectorforScrip(
	        String scCode
	);
}
