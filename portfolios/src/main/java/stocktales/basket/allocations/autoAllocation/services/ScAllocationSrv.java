package stocktales.basket.allocations.autoAllocation.services;

import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.interfaces.IMcapAllocationSrv;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationSrv;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;

@Service
public class ScAllocationSrv implements IScAllocationSrv
{
	@Autowired
	private IMcapAllocationSrv mcapAllocSrv;
	
	@Override
	public List<ScAllocation> calculateAllocations(
	        List<ScAllocation> tbAllocScrips
	)
	{
		if (tbAllocScrips != null)
		{
			if (tbAllocScrips.size() > 0)
			{
				//Get Total Score sum
				double sumScore = tbAllocScrips.stream().mapToDouble(ScAllocation::getStrengthScore).sum();
				for (ScAllocation scAllocation : tbAllocScrips)
				{
					double perAlloc = (scAllocation.getStrengthScore() / sumScore) * 100;
					scAllocation.setAllocation(Precision.round(perAlloc, 1));
				}
			}
		}
		// Apply MCap Calibration Here
		
		tbAllocScrips = mcapAllocSrv.calibrateAllocationbyMCap(tbAllocScrips);
		
		return tbAllocScrips;
	}
	
}
