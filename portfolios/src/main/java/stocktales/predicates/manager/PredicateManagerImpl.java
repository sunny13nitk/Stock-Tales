package stocktales.predicates.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import stocktales.predicates.GenericSCEDRCSummaryPredicate;

@Component
public class PredicateManagerImpl implements PredicateManager
{
	@Autowired
	private ApplicationContext appCtxt;
	
	@Value("${predicateBeanName}")
	private String predicateBeanName;
	
	/*
	 * Using #Predicate Bean Qualifier -predicateBeanName= ValueHuntPredicate
	 */
	
	@Override
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	)
	{
		GenericSCEDRCSummaryPredicate predBean = null;
		if (appCtxt != null && predicateBeanName != null)
		{
			predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanName);
		}
		
		return predBean;
	}
	
	/*
	 * Using External Called Bean Name : Can get from UI by selecting Bean Name to show diff Filter Strategies
	 */
	@Override
	public GenericSCEDRCSummaryPredicate getActivePredicateBean(
	        String predicateBeanNameVal
	)
	{
		GenericSCEDRCSummaryPredicate predBean = null;
		
		if (appCtxt != null)
		{
			if (predicateBeanNameVal != null)
			{
				if (predicateBeanName.trim().length() > 0)
				{
					predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanNameVal);
				}
			} else
			{
				predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanName);
			}
		}
		
		return predBean;
	}
	
	@Override
	public List<String> getPredicateBeanNames(
	)
	{
		List<String> predBeans = null;
		if (appCtxt != null)
		{
			String[] beansNamesArray = appCtxt.getBeanNamesForType(GenericSCEDRCSummaryPredicate.class);
			if (beansNamesArray.length > 0)
			{
				predBeans = new ArrayList<String>();
				for (int i = 0; i < beansNamesArray.length; i++)
				{
					if (!beansNamesArray[i].contains("."))
					{
						predBeans.add(beansNamesArray[i]);
					}
				}
			}
		}
		return predBeans;
	}
	
	@Override
	public String getActivePredicateBeanName(
	        String predicateBeanNameVal
	)
	{
		String                        beanName = null;
		GenericSCEDRCSummaryPredicate predBean = null;
		
		if (appCtxt != null)
		{
			if (predicateBeanNameVal != null)
			{
				if (predicateBeanName.trim().length() > 0)
				{
					predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanNameVal);
				}
			} else
			{
				predBean = (GenericSCEDRCSummaryPredicate) appCtxt.getBean(predicateBeanName);
			}
			
			if (predBean != null)
			{
				String[] beansNamesArray = appCtxt.getBeanNamesForType(predBean.getClass());
				for (int i = 0; i < beansNamesArray.length; i++)
				{
					if (!beansNamesArray[i].contains("."))
					{
						beanName = beansNamesArray[i];
					}
				}
			}
		}
		
		return beanName;
	}
	
}
