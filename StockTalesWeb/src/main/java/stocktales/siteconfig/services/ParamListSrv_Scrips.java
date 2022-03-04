package stocktales.siteconfig.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.dataBook.model.repo.adhocScrips.RepoAdhocScrip;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.services.interfaces.ScripService;
import stocktales.siteconfig.interfaces.IParamList;

@Service("AllScripNames")
public class ParamListSrv_Scrips implements IParamList
{
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private RepoAdhocScrip repoAdhocScrips;
	
	@Override
	public List<String> getParams(
	)
	{
		List<String> params      = null;
		List<String> adhocScList = null;
		try
		{
			params = scSrv.getAllScripNames();
			
			adhocScList = repoAdhocScrips.getAllAdhoScripNames();
			if (adhocScList != null)
			{
				if (adhocScList.size() > 0)
				{
					for (String sccode : adhocScList)
					{
						params.add(sccode);
					}
				}
			}
			
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return params;
	}
	
}
