package stocktales.basket.allocations.autoAllocation.valuations.services;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCWtPESrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations.SCExistsDB_Srv;

@Service
public class SCValuationSrvImpl implements SCValuationSrv
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;
	
	@Autowired
	private SCExistsDB_Srv scDBSrv;
	
	@Autowired
	private EDRCFacade edrcFacadeSrv;
	
	@Autowired
	private SCWtPESrv scWtPESrv;
	
	@Value("${MOSDefault}")
	private double MOS;
	
	@Value("${cagrPeriod}")
	private int cagrPeriod;
	
	@Override
	public ScValuation getValuationforScrip(
	        String scCode, double CMP, double MoS
	)
	{
		
		ScValuation scValuation = null;
		
		if (MoS > 0) //If Specified MOS override default property
		{
			this.MOS = MoS;
		}
		
		try
		{
			//Get the TTM Eps from SCGenQ
			if (scDBSrv != null)
			{
				EN_SC_GeneralQ enGenQ = scDBSrv.Get_ScripExisting_DB(scCode);
				if (enGenQ != null)
				{
					scValuation = new ScValuation();
					scValuation.setScCode(scCode);
					scValuation.setCurrEPS(enGenQ.getEPS());
					scValuation.setUPH(enGenQ.getUPH());
					scValuation.setMoS(this.MOS);
					
					if (CMP > 0)
					{
						scValuation.setCMP(CMP);
						scValuation.setCurrPE(Precision.round((CMP / scValuation.getCurrEPS()), 1));
					} else
					{
						scValuation.setCMP(enGenQ.getCMP());
						scValuation.setCurrPE(Precision.round(enGenQ.getCurrPE(), 1));
					}
				}
			}
			
			//Get the ED Score from edrcSrv
			if (edrcSrv != null)
			{
				scValuation.setEDScore(edrcSrv.getEDRCforScrip(scCode).getEarningsDivScore().getResValue());
				scValuation.setEDSCcoreB4MoS(scValuation.getEDScore());
				//Adjust EDRC Score for MoS
				scValuation.setEDScore(Precision.round((scValuation.getEDScore() * this.MOS), 1));
				
			}
			
			// Get the weighted PE from SCWtPE Srv
			if (scWtPESrv != null)
			{
				scValuation.setWeightedPE(Precision.round(scWtPESrv.getWeightedPEforScrip(scCode).getWtPE(), 1));
			}
			
			//Calculate 5Yr Henceforth Price
			double resEPS = scValuation.getCurrEPS()
			        * (Math.exp(cagrPeriod * Math.log((scValuation.getEDScore() + 100) / 100)));
			if (resEPS > 0)
			{
				
				double fv = resEPS * scValuation.getWeightedPE();
				scValuation.setPrice5Yr(Precision.round(fv, 1));
				
				//Calculate 5Yr Price CAGR
				double cagrAchv = (Math.pow(fv / scValuation.getCMP(), 1.0 / this.cagrPeriod) - 1.0) * 100;
				
				scValuation.setRet5YrCAGR(Precision.round(cagrAchv, 1));
			}
			
			scValuation.setStrengthScore(edrcFacadeSrv.getEDRCforSCrip(scCode).getStrengthScore());
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return scValuation;
	}
	
}
