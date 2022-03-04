package stocktales.healthcheck.impl.subSrv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumEmphasis;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.annotations.HealthCheckCriteria;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.healthcheck.enums.EnumDataContainerTitle;
import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.intf.IHC_SubSrvRes;
import stocktales.healthcheck.model.entity.Cfg_ScripHealthCheck;
import stocktales.healthcheck.model.helperpojo.HCDataContainer;
import stocktales.healthcheck.model.helperpojo.HCMsg;
import stocktales.healthcheck.model.helperpojo.HCSubSrvRes;
import stocktales.healthcheck.repo.Repo_CfgHC;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;

@Service
@HealthCheckCriteria(criteria = EnumHCCriteria.Growth)
public class SalesTrendsSrv implements IHC_SubSrvRes
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private IScDataContSrv scDCSrv;
	
	@Autowired
	private Repo_CfgHC repocfgHC;
	
	@Autowired
	private MessageSource msgSrc;
	
	private List<HCSubSrvRes> results = new ArrayList<HCSubSrvRes>();
	
	private Cfg_ScripHealthCheck cfgRevenues;
	
	private final String ttmperiod = "TTM";
	
	@Override
	public List<HCSubSrvRes> getSubServiceResults(
	        String scCode
	)
	{
		if (scCode != null && fsBuffSrv != null)
		{
			this.results.clear();
			//Get Trends Entities List
			List<EN_SC_Trends> trends = fsBuffSrv.getDataContainerforScrip(scCode).getTrends_L();
			if (trends != null)
			{
				HCSubSrvRes                result       = new HCSubSrvRes();
				List<Cfg_ScripHealthCheck> cfgRevenuesL = repocfgHC.findByStext(EnumHCCriteria.Growth);
				if (cfgRevenuesL != null)
				{
					cfgRevenues = cfgRevenuesL.get(0); //First one is Enough we just want the ServiceName
					if (cfgRevenues != null)
					{
						
						// Populate Data Container
						result.getContainers().add(new HCDataContainer());
						result.getContainers().get(0).setTitle(EnumDataContainerTitle.SalesInIntervals);
						
						for (EN_SC_Trends trend : trends)
						{
							result.getContainers().get(0).getNameValContainer().add(
							        new NameVal(trend.getPeriod(), (double) ((Integer) trend.getSalesGR()).intValue()));
						}
						
						//Populate Messages
						
						checkforAnyBelowNominalGrowthPeriod(result);
						
						//Add to Results to return
						results.add(result);
						
					}
					
				}
				
			}
			
		}
		
		return this.results;
	}
	
	/*
	 * Any below Nominal configured Growth Period; except TTM
	 *  - Negative, Monitor if any
	 *  - Negative, Major if more than 1
	 *  - Stable, if all > nonimal
	 */
	private void checkforAnyBelowNominalGrowthPeriod(
	        HCSubSrvRes result
	)
	{
		/*
		 * Looping at data container
		 */
		int           numOccurances = 0;
		List<NameVal> belowPar      = new ArrayList<NameVal>();
		
		if (result.getContainers().size() > 0)
		{
			for (NameVal trend : result.getContainers().get(0).getNameValContainer())
			{
				if (!trend.getName().equals(this.ttmperiod))
				{
					if (trend.getValue() < cfgRevenues.getBaseval())
					{
						belowPar.add(trend);
						numOccurances += 1;
					}
				}
			}
			
			//Set Message Severity and Emphasis
			HCMsg msg = new HCMsg();
			if (numOccurances > 1)
			{
				msg.setEffect(EnumEffect.Negative);
				msg.setEmphasis(EnumEmphasis.Major);
			} else if (numOccurances == 1)
			{
				msg.setEffect(EnumEffect.Negative);
				msg.setEmphasis(EnumEmphasis.Mild);
			} else
			{
				msg.setEffect(EnumEffect.Observation);
				msg.setEmphasis(EnumEmphasis.Fair);
			}
			
			switch (msg.getEmphasis())
			{
				case Major:
				case Mild:
					for (int i = 0; i < numOccurances; i++)
					{
						HCMsg  finalMsg = new HCMsg(msg.getEffect(), msg.getEmphasis(), "");
						String msgText  = msgSrc.getMessage("LOWGROWTH", new Object[]
						{ belowPar.get(i).getName(), belowPar.get(i).getValue() }, Locale.ENGLISH);
						finalMsg.setMsg(msgText);
						
						result.getMessages().add(finalMsg);
					}
					
					break;
				
				case Fair:
					HCMsg finalMsg = new HCMsg(msg.getEffect(), msg.getEmphasis(), "");
					String msgText = msgSrc.getMessage("ABOVEPARGROWTH", new Object[]
					{ cfgRevenues.getBaseval() }, Locale.ENGLISH);
					finalMsg.setMsg(msgText);
					
					result.getMessages().add(finalMsg);
					break;
				
				default:
					break;
			}
		}
		
	}
	
}
