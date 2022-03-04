package stocktales.siteconfig.interfaces;

import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.model.pojos.UrlParam;

public interface INavUrlSrv
{
	public void setNavUrl(
	        UrlParam urlParam
	);
	
	public UrlParam getNavUrl(
	);
	
	public String getFormattedUrl(
	);
	
	public void setSitePath(
	        SitePaths sitePath
	);
	
	public SitePaths getSitePath(
	);
}
