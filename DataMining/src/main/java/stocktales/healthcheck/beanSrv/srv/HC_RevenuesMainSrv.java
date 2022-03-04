package stocktales.healthcheck.beanSrv.srv;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.intf.IHC_GetEvaluationResults;
import stocktales.healthcheck.model.helperpojo.HCBeanReturn;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;

@Service("HC_RevenuesMainSrv")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HC_RevenuesMainSrv implements IHC_GetEvaluationResults
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	private HCBeanReturn mainSrvReturn = new HCBeanReturn();
	
	//Look out for Subservices with @HealthCheckCriteria as below
	private final EnumHCCriteria criteria = EnumHCCriteria.Growth;
	
	@Value("${SalesBase}")
	private final String durationBase = "3Yr";
	
	/*
	 * Populate base Val before returning - other values from Subservices handed by the aspect
	 * SCHEalthCheckEvaluateASpect
	 */
	
	@Override
	public HCBeanReturn returnAfterEvaluation(
	        String scCode, boolean isFinancial
	)
	{
		
		/*
		 * Base Value is last 3 years Sales CAGR
		 */
		this.mainSrvReturn = new HCBeanReturn();
		if (scCode != null && fsBuffSrv != null)
		{
			scDataContainer scCon = fsBuffSrv.getDataContainerforScrip(scCode);
			if (scCon != null)
			{
				Optional<EN_SC_Trends> enTrO = scCon.getTrends_L().stream()
				        .filter(x -> x.getPeriod().equals(durationBase)).findFirst();
				if (enTrO.isPresent())
				{
					//Convert Int to Double
					this.mainSrvReturn.setValueToCmp((double) ((Integer) enTrO.get().getSalesGR()).intValue());
				}
			}
		}
		
		return this.mainSrvReturn;
		
	}
}