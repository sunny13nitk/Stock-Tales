package stocktales.siteconfig.services;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.siteconfig.interfaces.INavUrlSrv;
import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.model.pojos.UrlParam;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NavUrlSrv implements INavUrlSrv
{
	
	private UrlParam     navUrl     = new UrlParam();
	private SitePaths    sitePath;
	private final String paramStart = "\\{";
	
	@Override
	public void setNavUrl(
	        UrlParam urlParam
	)
	{
		this.navUrl = urlParam;
		
	}
	
	@Override
	public UrlParam getNavUrl(
	)
	{
		return this.navUrl;
	}
	
	@Override
	public String getFormattedUrl(
	)
	{
		String formattedUrl = null;
		if (this.navUrl != null)
		{
			if (this.navUrl.getParam() != null && this.navUrl.getUrl() != null)
			{
				String[] arrOfStr = this.navUrl.getUrl().split(paramStart);
				
				formattedUrl = arrOfStr[0] + this.navUrl.getParam();
			}
		}
		return formattedUrl;
	}
	
}
