package stocktales.basket.allocations.autoAllocation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.interfaces.IMcapAllocationSrv;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.config.pojos.MCapAllocations;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

@Service
public class McapAllocationSrv implements IMcapAllocationSrv
{
	@Autowired
	private MCapAllocations cfMcapAlloc;
	
	@Autowired
	private ISCExistsDB_Srv scExistsSrv;
	
	@Override
	public List<ScAllocation> calibrateAllocationbyMCap(
	        List<ScAllocation> scAllocList
	)
	{
		double       extraAlloc          = 0;
		int          numScrips           = 0;
		List<String> extraAllocScrips    = new ArrayList<String>();
		double       spreadTBDistributed = 0;
		
		for (ScAllocation scAllocation : scAllocList)
		{
			if (scAllocation != null)
			{
				try
				{
					EN_SC_GeneralQ scGenQ = scExistsSrv.Get_ScripExisting_DB(scAllocation.getScCode());
					if (scGenQ != null)
					{
						if (scGenQ.getMktCap() <= cfMcapAlloc.getMCap())
						{
							if (scAllocation.getAllocation() > cfMcapAlloc.getMaxAllocation())
							{
								extraAlloc += scAllocation.getAllocation() - cfMcapAlloc.getMaxAllocation();
								numScrips  += 1;
								scAllocation.setAllocation(cfMcapAlloc.getMaxAllocation());
								extraAllocScrips.add(scAllocation.getScCode());
							}
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		spreadTBDistributed = extraAlloc / (scAllocList.size() - numScrips);
		
		//Update
		
		for (ScAllocation scAllocation : scAllocList)
		{
			Optional<String> scCodeFound = extraAllocScrips.stream()
			        .filter(x -> x.toString().equals(scAllocation.getScCode())).findFirst();
			
			if (!scCodeFound.isPresent())
			{
				//Boost by extra Allocation spread to be distributed
				
				scAllocation.setAllocation(Precision.round((scAllocation.getAllocation() + spreadTBDistributed), 1));
				
			}
			
		}
		
		return scAllocList;
	}
	
}
