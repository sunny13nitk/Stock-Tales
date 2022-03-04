package stocktales.controllers.config.site;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.siteconfig.interfaces.IParamListManager;
import stocktales.siteconfig.model.entity.SitePaths;
import stocktales.siteconfig.repo.RepoSitePaths;

@Controller
@RequestMapping("/siteconfig")
public class SiteController
{
	@Autowired
	private RepoSitePaths repoSites;
	
	@Autowired
	private IParamListManager paramMgrSrv;
	
	@GetMapping("/qvsp")
	public String showQuickSiteUrlConfig(
	        Model model
	)
	{
		SitePaths newPath = new SitePaths();
		model.addAttribute("sitepath", newPath);
		model.addAttribute("paramBeanNames", paramMgrSrv.getAllParamBeansNames());
		return "siteConfig/sitePath_Edit";
	}
	
	@GetMapping("/list")
	public String showSitePathList(
	        Model model
	)
	{
		model.addAttribute("sitepaths", repoSites.findAll());
		return "siteConfig/list";
	}
	
	@GetMapping("/qvsp/{pathid}")
	public String editQuickSiteUrlConfig(
	        Model model, @PathVariable String pathid
	)
	{
		if (pathid != null)
		{
			Optional<SitePaths> sitePathfound = repoSites.findById(new Integer(pathid));
			if (sitePathfound.isPresent())
			{
				
				model.addAttribute("sitepath", sitePathfound.get());
			}
			
			model.addAttribute("paramBeanNames", paramMgrSrv.getAllParamBeansNames());
			
		}
		return "siteConfig/sitePath_Edit";
	}
	
	@GetMapping("/qvsp/delete/{pathid}")
	public String deleteQuickSiteUrlConfig(
	        Model model, @PathVariable String pathid
	)
	{
		if (pathid != null)
		{
			Optional<SitePaths> sitePathfound = repoSites.findById(new Integer(pathid));
			if (sitePathfound.isPresent())
			{
				repoSites.deleteById(new Integer(pathid));
			}
			
		}
		return "redirect:/siteconfig/list";
	}
	
	/*
	 * -----------------------------------------------------------------------
	 *                                 POST MAPPINGS
	 * -----------------------------------------------------------------------
	 */
	
	@PostMapping("/path")
	public String saveSitePath(
	        @ModelAttribute("sitepath") SitePaths sitepath, Model model
	)
	{
		if (sitepath != null)
		{
			//First check for Duplicate Entry
			Optional<SitePaths> sitePathfound = repoSites.findByTitleIgnoreCase(sitepath.getTitle());
			if (sitePathfound.isPresent() && sitepath.getPathid() == 0) //Only for New
			{
				//Error - Duplicate Path
				model.addAttribute("formError", "Site Path with Title : " + sitepath.getTitle() + " already Exists!");
				return "siteConfig/sitePath_Edit";
			} else //Proceed Save
			{
				repoSites.save(sitepath);
			}
			
		}
		
		return "redirect:/siteconfig/list";
		
	}
}
