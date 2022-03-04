package stocktales.siteconfig.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import stocktales.siteconfig.interfaces.IParamList;
import stocktales.siteconfig.interfaces.IParamListManager;

@Service
public class ParamsListManagerSrv implements IParamListManager
{
	@Autowired
	private ApplicationContext appCtxt;
	
	@Override
	public IParamList getParamsListBeanByName(
	        String beanName
	)
	{
		IParamList predBean = null;
		
		if (appCtxt != null)
		{
			if (beanName != null)
			{
				if (beanName.trim().length() > 0)
				{
					predBean = (IParamList) appCtxt.getBean(beanName);
				}
			}
		}
		
		return predBean;
	}
	
	@Override
	public List<String> getAllParamBeansNames(
	)
	{
		List<String> predBeans = null;
		if (appCtxt != null)
		{
			String[] beansNamesArray = appCtxt.getBeanNamesForType(IParamList.class);
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
	
}
