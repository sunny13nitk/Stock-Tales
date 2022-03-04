package stocktales.healthcheck.intf;

import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.model.helperpojo.HCBeanReturn;

/*
 * To Be Implemented by Every Figure Getter Bean Used in Scrip Health Check
 * Main Service also has to delegate to SubService whether it is a financial or not
 * as a situation might arise where main service tasks needs to be done for Financials too but 
 * subservices- need not be invoked, this is to provide that flexibility
 */
public interface IHC_GetEvaluationResults
{
	
	public HCBeanReturn returnAfterEvaluation(
	        String scCode, boolean isFinancial
	);
	
	public EnumHCCriteria getCriteria(
	);
}
