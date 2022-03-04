package stocktales.predicates;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

@Component("ValueHuntPredicate")
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UndervaluedPredicate extends GenericSCEDRCSummaryPredicate
{
	
	@Override
	public boolean test(
	        SC_EDRC_Summary x
	)
	{
		
		return (x.getAvWtED() > 13 && x.getAvWtRR() >= 18 && (x.getAvWtCF() == 0 || x.getAvWtCF() > 25)
		        && (x.getValR() > 3 && x.getValR() < 50));
		
	}
	
	public String getNotes(
	)
	{
		return "(Earnings & Div. Score > 13) AND (Return Ratios Score > 18) AND (Cash Flow Score (Non Financials) > 40) AND (Value Ratio Between 7  and 45) )";
	}
	
}
