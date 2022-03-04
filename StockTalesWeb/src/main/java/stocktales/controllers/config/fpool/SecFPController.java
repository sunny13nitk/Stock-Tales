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

import stocktales.dataBook.config.entity.CfgSectorsFieldsPool;
import stocktales.dataBook.model.repo.config.RepoCfgSecFP;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

@Controller
@RequestMapping("/fpconfig")
public class SecFPController
{
	@Autowired
	private RepoCfgSecFP repoCfgsecFP;
	
	@Autowired
	private ISCExistsDB_Srv scExisSrv;;
	
	@GetMapping("/list")
	public String showFPConfigList(
	        Model model
	)
	{
		model.addAttribute("cfgs", repoCfgsecFP.findAll());
		return "sectorFieldPools/config/list";
	}
	
	@GetMapping("/newSecConfig")
	public String showNewSecSpConfig(
	        Model model
	)
	{
		model.addAttribute("cfg", new CfgSectorsFieldsPool());
		
		try
		{
			model.addAttribute("sectors", scExisSrv.getAllSectors());
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "sectorFieldPools/config/secFPConfig_Edit";
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
				Optional<CfgSectorsFieldsPool> cfgO = repoCfgsecFP.findById(fp_id);
				if (cfgO.isPresent())
				{
					model.addAttribute("cfg", cfgO.get());
					try
					{
						model.addAttribute("sectors", scExisSrv.getAllSectors());
					} catch (EX_General e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return "sectorFieldPools/config/secFPConfig_Edit";
	}
	
	/*
	 * _______________________________________________________________
	 *                        POST MAPPINGS
	 * _______________________________________________________________                       
	 * 
	 */
	
	@PostMapping("/save")
	public String saveConfig(
	        @ModelAttribute("cfg") CfgSectorsFieldsPool config, Model model
	)
	{
		
		String desurl = null;
		if (config != null)
		{
			//Check for Duplicate by Sector
			Optional<CfgSectorsFieldsPool> exisCfgO = repoCfgsecFP.findBySector(config.getSector());
			if (exisCfgO.isPresent() && exisCfgO.get().getFpid() == 0)
			{
				model.addAttribute("formError",
				        "Field Pool Config for Sector - " + config.getSector() + " already Exists!");
				desurl = "sectorFieldPools/config/secFPConfig_Edit"; //Stay Here
			} else
			{
				repoCfgsecFP.save(config);
				desurl = "redirect:/fpconfig/list";
			}
			
		}
		return desurl;
	}
}
