package stocktales.basket.allocations.autoAllocation.valuations.services;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCWtPESrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.scWtPE;
import stocktales.basket.allocations.config.pojos.DurationWeights;
import stocktales.repository.TrendsRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;

@Service
public class SCWtPESrvImpl implements SCWtPESrv
{
	@Autowired
	private TrendsRepository repoSCTrends;
	
	@Autowired
	private DurationWeights cfDurWts;
	
	@Override
	public scWtPE getWeightedPEforScrip(
	        String scCode
	)
	{
		scWtPE scripWtPE = null;
		
		if (scCode != null)
		{
			//Get the Trends
			List<EN_SC_Trends> trends = repoSCTrends.findAllBySCCodeIgnoreCase(scCode);
			if (trends != null)
			{
				if (trends.size() > 0)
				{
					double wts_total = 0;
					double valPE     = 0;
					scripWtPE = new scWtPE();
					scripWtPE.setScCode(scCode);
					for (EN_SC_Trends trend : trends)
					{
						//Get the Weights by Year in curr trend loop pass
						String cfDurWtsMName  = "getWt" + trend.getPeriod();
						Method methodcfDurWts = ReflectionUtils.findMethod(DurationWeights.class, cfDurWtsMName);
						if (methodcfDurWts != null)
						{
							double wtvalue = (double) ReflectionUtils.invokeMethod(methodcfDurWts, cfDurWts);
							if (wtvalue > 0)
							{
								//add to weight counters - adjust for missing years if any for a scrip
								wts_total += wtvalue;
								
								valPE = valPE + wtvalue * trend.getAvgPE();
							}
							
						}
					}
					
					double residualwt_pe = 1 - wts_total;
					double wtFacPE       = 1;
					if (residualwt_pe > 0)
					{
						wtFacPE = 1 / wts_total;
					}
					
					valPE = valPE * wtFacPE;
					scripWtPE.setWtPE(valPE);
					
				}
			}
		}
		
		return scripWtPE;
		
	}
	
}
