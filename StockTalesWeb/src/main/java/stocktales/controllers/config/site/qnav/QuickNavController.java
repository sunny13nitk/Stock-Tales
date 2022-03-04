package stocktales.controllers.config.site.qnav;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.siteconfig.interfaces.INavUrlSrv;
import stocktales.siteconfig.interfaces.IParamList;
import stocktales.siteconfig.interfaces.IParamListManager;
import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.model.pojos.UrlParam;
import stocktales.siteconfig.repo.RepoSitePaths;
import stocktales.uiHelperPOJOs.SearchText;

@Controller
@RequestMapping("/qnav")
public class QuickNavController
{
	@Autowired
	private RepoSitePaths repoSitePaths;

	@Autowired
	private INavUrlSrv navUrlSrv;

	@Autowired
	private IParamListManager paramMgrSrv;

	@GetMapping("/site")
	public String showqNavSite(Model model)
	{

		// Add all Title Urls to Paths
		if (repoSitePaths != null)
		{

			List<SitePaths> sitePaths = repoSitePaths.findAll();

			model.addAttribute("paths", sitePaths);
		}

		// Add a Blank Search Text POJO
		model.addAttribute("searchtext", new SearchText());

		return "quickNav/qnavSite";
	}

	@GetMapping("/navwithParam")
	public String showNavigateWithParam(Model model)
	{
		if (navUrlSrv != null)
		{
			model.addAttribute("urlParam", navUrlSrv.getNavUrl());
			model.addAttribute("sitePath", navUrlSrv.getSitePath());

			// Get the Bean Instance from Manager
			IParamList paramListBean = paramMgrSrv.getParamsListBeanByName(navUrlSrv.getSitePath().getParamslistbean());
			if (paramListBean != null)
			{
				model.addAttribute("paramList", paramListBean.getParams());
			}
		}

		return "quickNav/navwithParam";
	}

	@PostMapping("/site")
	public String navigatetoSite(@ModelAttribute("searchtext") SearchText stext)
	{
		String url = null;
		String desurl = null;
		if (stext != null)
		{
			if (stext.getStext() != null)
			{

				// Scan for PArametrized or Non Parametrized Navigation
				url = stext.getStext();
				Optional<SitePaths> sitePathO = repoSitePaths.findByUrlIgnoreCase(url);
				if (sitePathO.isPresent())
				{
					if (sitePathO.get().getParamslistbean() != null)
					{
						if (sitePathO.get().getParamslistbean().trim().length() > 0)
						{
							// Parametrized Navigation
							// Set Session Bean and Navigate
							navUrlSrv.setNavUrl(new UrlParam(url, null));
							navUrlSrv.setSitePath(sitePathO.get());
							desurl = "redirect:/qnav/navwithParam";

						}

						else
						{
							desurl = "redirect:" + url;
						}
					} else
					{
						desurl = "redirect:" + url;
					}
				}

			}
		}

		return desurl;

	}

	@PostMapping("/siteParam")
	public String navigatetoSitewithParam(@ModelAttribute("urlParam") UrlParam urlParam)
	{
		String desurl = null;
		if (urlParam != null)
		{
			// Set the Param in session bean
			navUrlSrv.getNavUrl().setParam(urlParam.getParam());
			/// get formatted Url
			desurl = "redirect:" + navUrlSrv.getFormattedUrl();
		}

		return desurl;

	}

}
