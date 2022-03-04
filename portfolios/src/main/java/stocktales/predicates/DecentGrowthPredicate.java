package stocktales.predicates;

import java.util.function.Predicate;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

@Component("DecentGrowthPredicate")
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DecentGrowthPredicate implements Predicate<SC_EDRC_Summary>
{
	
	@Override
	public boolean test(
	        SC_EDRC_Summary x
	)
	{
		return (x.getAvWtED() > 20 && x.getAvWtRR() >= 16 && (x.getAvWtCF() == 0 || x.getAvWtCF() > 50)
		        && (x.getValR() > 7 && x.getValR() < 50));
	}
	
	public String getNotes(
	)
	{
		return "(Earnings & Div. Score > 13) AND (Return Ratios Score > 18) AND (Cash Flow Score (Non Financials) > 40) AND (Value Ratio Between 7  and 45) )";
	}
	
}
