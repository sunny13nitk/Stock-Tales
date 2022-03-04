package stocktales.predicates.manager;

import java.util.List;

import stocktales.predicates.GenericSCEDRCSummaryPredicate;

public interface PredicateManager
{
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	);
	
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	        String predicateBeanNameVal
	);
	
	public String getActivePredicateBeanName(
	        String predicateBeanNameVal
	);
	
	public List<String> getPredicateBeanNames(
	);
}
