package stocktales.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.impl.EDRCFacadeImpl;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.config.pojos.FinancialSectors;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.services.interfaces.ScripService;

@Service
public class ScripServiceImpl implements ScripService
{
	@Autowired
	private ISCExistsDB_Srv repoSCGen;
	
	@Autowired
	private FinancialSectors cfFinSec;
	
	@Autowired
	private EDRCFacadeImpl edrcFacSrv;
	
	@Override
	public boolean isScripBelongingToFinancialSector(
	        String scCode
	)
	{
		boolean flag = false;
		
		if (scCode != null)
		{
			
			try
			{
				if (scCode.trim().length() > 0 && repoSCGen != null)
				{
					
					EN_SC_GeneralQ scGenQ = repoSCGen.Get_ScripExisting_DB(scCode);
					if (scGenQ != null)
					{
						String sector = scGenQ.getSector();
						if (sector.equals(cfFinSec.getSectorName()))
						{
							flag = true;
						}
					}
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	@Override
	public EN_SC_GeneralQ getScripHeader(
	        String scCode
	)
	{
		EN_SC_GeneralQ scGenQ = null;
		if (scCode != null)
		{
			
			try
			{
				if (scCode.trim().length() > 0 && repoSCGen != null)
				{
					
					scGenQ = repoSCGen.Get_ScripExisting_DB(scCode);
					
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return scGenQ;
	}
	
	@Override
	public List<EN_SC_GeneralQ> getAllScripsHeaders(
	) throws EX_General
	{
		
		return repoSCGen.getAllScripsHeaders();
	}
	
	@Override
	public List<String> getAllScripNames(
	) throws EX_General
	{
		
		return repoSCGen.getAllScripNames();
	}
	
	@Override
	public List<SC_EDRC_Summary> getEDRCSummaryforAll(
	)
	{
		List<String> scrips = null;
		try
		{
			scrips = this.getAllScripNames();
			
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return edrcFacSrv.getEDRCforSCripsList(scrips);
	}
	
	public String getSectorforScrip(
	        String scCode
	)
	{
		String sector = null;
		
		EN_SC_GeneralQ scGenQ = this.getScripHeader(scCode);
		if (scGenQ != null)
		{
			sector = scGenQ.getSector();
		}
		
		return sector;
	}
	
	@Override
	public List<String> getAllScripNamesforSector(
	        String sector
	) throws EX_General
	{
		return repoSCGen.getAllScripNamesforSector(sector);
	}
}
