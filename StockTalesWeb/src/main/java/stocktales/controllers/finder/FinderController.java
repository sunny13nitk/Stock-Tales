package stocktales.controllers.finder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.uiHelperPOJOs.SearchText;

@Controller
@RequestMapping("/finder")
public class FinderController
{
	
	@GetMapping("/show")
	public String showFinder(
	        Model model
	)
	{
		model.addAttribute("searchTerm", new SearchText());
		/*return "finder/finder";*/
		
		return "scsnapshot/scOvwTabs";
	}
	
	/*
	 *  ---------------------  POST MAPPINGS --------------------------
	 */
	
	@PostMapping("/processNav")
	public String navigate(
	        @ModelAttribute("searchTerm") SearchText searchTerm, Model model
	)
	{
		String desurl = "success";
		if (searchTerm != null)
		{
			if (searchTerm.getStext().trim().length() > 0)
			{
				
			}
		}
		
		return desurl;
	}
}
