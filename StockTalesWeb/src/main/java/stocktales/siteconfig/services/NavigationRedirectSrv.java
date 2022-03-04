package stocktales.siteconfig.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.events.EV_UploadScrip;
import stocktales.siteconfig.interfaces.INavigationRedirectSrv;
import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.repo.RepoSitePaths;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.INTERFACES)
public class NavigationRedirectSrv implements INavigationRedirectSrv
{
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	private String pathTitle;

	private String scCode;

	private final String scCodeParamDEsc = "Scrip Code";
	private final String paramStart = "\\{";

	@Autowired
	private RepoSitePaths repoSitePaths;

	@Override
	public String getPathUrl()
	{
		String desUrl = null;

		if (repoSitePaths != null)
		{
			if (this.pathTitle != null)
			{
				Optional<SitePaths> desSPO = repoSitePaths.findByTitleIgnoreCase(this.pathTitle);
				if (desSPO.isPresent())
				{

					if (desSPO.get().getParamdesc() != null)
					{
						if (desSPO.get().getParamdesc().equals(this.scCodeParamDEsc))
						{
							String[] arrOfStr = desSPO.get().getUrl().split(paramStart);
							if (scCode != null)
							{
								desUrl = arrOfStr[0] + this.getScCode();
								publishScripUploadEvent(desSPO.get().getTitle());
							}
						}
					} else
					{
						desUrl = desSPO.get().getUrl();
					}

				}
			}
		}

		return desUrl;

	}

	@Override
	public void clear()
	{
		this.pathTitle = null;
		this.scCode = null;

	}

	@Override
	public void publishScripUploadEvent(String navSrcTitle)
	{
		// Create an Instance of Money Bag Transaction Event
		EV_UploadScrip evScripUpload = new EV_UploadScrip(this, this.getScCode(), navSrcTitle);
		// Publish the Event using Injected Application Event Publisher
		applicationEventPublisher.publishEvent(evScripUpload);

	}

}
