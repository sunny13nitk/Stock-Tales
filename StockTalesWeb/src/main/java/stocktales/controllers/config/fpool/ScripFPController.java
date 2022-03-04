package stocktales.controllers.config.fpool;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.dataBook.config.entity.CfgScripsFieldsPool;
import stocktales.dataBook.model.repo.config.RepoCfgScripFP;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.services.interfaces.ScripService;

@Controller
@RequestMapping("/ScSpFPools")
public class ScripFPController
{
	@Autowired
	private RepoCfgScripFP repo_cfgScSp;
	
	@Autowired
	private ScripService scSrv;
	
	@GetMapping("/list")
	public String showlist(
	        Model model
	)
	{
		model.addAttribute("cfgs", repo_cfgScSp.findAll());
		return "cfScSpFPools/list";
	}
	
	@GetMapping("/newConfig")
	public String showNewScSpConfig(
	        Model model
	)
	{
		model.addAttribute("cfg", new CfgScripsFieldsPool());
		try
		{
			model.addAttribute("scrips", scSrv.getAllScripNames());
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "cfScSpFPools/scFPConfig_Edit";
	}
	
	@GetMapping("/cfg/{fpid}")
	public String showEditSecSpConfig(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		if (fpid != null)
		{
			int fp_id = new Integer(fpid);
			if (fp_id > 0)
			{
				Optional<CfgScripsFieldsPool> cfgO = repo_cfgScSp.findById(fp_id);
				if (cfgO.isPresent())
				{
					model.addAttribute("cfg", cfgO.get());
					try
					{
						model.addAttribute("scrips", scSrv.getAllScripNames());
					} catch (EX_General e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return "cfScSpFPools/scFPConfig_Edit";
	}
	
	/*
	 * _______________________________________________________________
	 *                        POST MAPPINGS
	 * _______________________________________________________________                       
	 * 
	 */
	
	@PostMapping("/save")
	public String saveConfig(
	        @ModelAttribute("cfg") CfgScripsFieldsPool config, Model model
	)
	{
		
		String desurl = null;
		if (config != null)
		{
			//Check for Duplicate by Scrip Code
			Optional<CfgScripsFieldsPool> exisCfgO = repo_cfgScSp.findBySccode(config.getSccode());
			if (exisCfgO.isPresent() && exisCfgO.get().getFpid() == 0)
			{
				model.addAttribute("formError",
				        "Field Pool Config for Scrip - " + config.getSccode() + " already Exists!");
				desurl = "cfScSpFPools/scFPConfig_Edit"; //Stay Here
			} else
			{
				repo_cfgScSp.save(config);
				desurl = "redirect:/ScSpFPools/list";
			}
			
		}
		return desurl;
	}
}
