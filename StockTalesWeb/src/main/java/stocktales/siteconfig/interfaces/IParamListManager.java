package stocktales.siteconfig.interfaces;

import java.util.List;

public interface IParamListManager
{
	public IParamList getParamsListBeanByName(
	        String beanName
	);
	
	public List<String> getAllParamBeansNames(
	);
}
