package stocktales.controllers.databook.generic;

import java.util.Optional;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.dataBook.config.entity.CfgScripsFieldsPool;
import stocktales.dataBook.config.entity.CfgSectorsFieldsPool;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.IScripSpFieldPoolSrv;
import stocktales.dataBook.fpSrv.interfaces.ISecSpFieldPoolSrv;
import stocktales.dataBook.helperPojo.IdNotes;
import stocktales.dataBook.model.entity.sectors.FPFinancialSector;
import stocktales.dataBook.model.repo.config.RepoCfgScripFP;
import stocktales.dataBook.model.repo.config.RepoCfgSecFP;
import stocktales.dataBook.model.repo.sectors.RepoFPFinancialSector;
import stocktales.services.interfaces.ScripService;

/*
 * -----------------  DATA BOOK CONTROLLER FOR SCRIP DATA MAINTAINENCE  --------------
 */
@Controller
@RequestMapping("/databook/scrip")
public class DataBookController
{
	@Autowired
	private ScripService          scSrv;
	@Autowired
	private RepoCfgSecFP          repocfgsecFP;
	@Autowired
	private RepoCfgScripFP        repocfgscFp;
	@Autowired
	private RepoFPFinancialSector repoFPFinancials;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@GetMapping("/secsp/{scCode}")
	public String triggerNav_SecSp_DataMaintain_Base(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		String desurl = null;
		
		String sector = null;
		//Get Sector from Scrip Code
		if (scSrv != null)
		{
			sector = scSrv.getSectorforScrip(scCode);
		}
		
		//Get the BeanSrv for Scrip Sector
		if (sector != null)
		{
			Optional<CfgSectorsFieldsPool> cfgsecFPO = repocfgsecFP.findBySector(sector);
			if (cfgsecFPO.isPresent())
			{
				CfgSectorsFieldsPool cfgsecFp = cfgsecFPO.get();
				//Get the bean from AppCtxt
				@SuppressWarnings("unchecked")
				ISecSpFieldPoolSrv<T> fpSrv = (ISecSpFieldPoolSrv<T>) appCtxt.getBean(cfgsecFp.getSrvbean());
				if (fpSrv != null)
				{
					
					//Load the Model for Scrip Sector Sp. Data - in case bean found
					/*
					 * Scrip Code, List<Annual & Quarterly>
					 */
					
					model.addAttribute("scCode", scCode);
					model.addAttribute("sector", sector);
					
					model.addAttribute("AnnualList", fpSrv.findAllByInterval(EnumInterval.Annual));
					model.addAttribute("QtrylList", fpSrv.findAllByInterval(EnumInterval.Quarterly));
					
					desurl = "scrips/dataBook/secSpFPoolDataList";
				}
				
			} else
			{
				//No Config Found webpage in case of bean not found
				desurl = "scrips/dataBook/NoConfigFound";
			}
			
		}
		//REdirect to Sector Specific List Page 
		return desurl;
	}
	
	@GetMapping("/scsp/{scCode}")
	public String triggerNav_ScSp_DataMaintain_Base(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		String desurl = null;
		String sector = null;
		
		//Get the BeanSrv for Scrip Sector
		if (scCode != null)
		{
			Optional<CfgScripsFieldsPool> cfgscFPO = repocfgscFp.findBySccode(scCode);
			if (cfgscFPO.isPresent())
			{
				CfgScripsFieldsPool cfgscFp = cfgscFPO.get();
				//Get the bean from AppCtxt
				@SuppressWarnings("unchecked")
				IScripSpFieldPoolSrv<T> fpSrv = (IScripSpFieldPoolSrv<T>) appCtxt.getBean(cfgscFp.getSrvbean());
				if (fpSrv != null)
				{
					
					//Load the Model for Scrip Sector Sp. Data - in case bean found
					/*
					 * Scrip Code, List<Annual & Quarterly>
					 */
					
					if (scSrv != null)
					{
						sector = scSrv.getSectorforScrip(scCode);
					}
					
					model.addAttribute("scCode", scCode);
					model.addAttribute("sector", sector);
					model.addAttribute("AnnualList", fpSrv.findAllByInterval(EnumInterval.Annual));
					model.addAttribute("QtrylList", fpSrv.findAllByInterval(EnumInterval.Quarterly));
					
					desurl = "scrips/dataBook/scSpFPoolDataList";
				}
				
			} else
			{
				//No Config Found webpage in case of bean not found
				desurl = "scrips/dataBook/NoConfigFound";
			}
			
		}
		//REdirect to Sector Specific List Page 
		return desurl;
	}
	
	/*
	 * Never Navigate Directly always via  "databook/scrip/secsp/{scCode}"
	 */
	@GetMapping("/secSpData/{scCode}/list")
	public String showSectorSpDataforScrip(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		
		return "scrips/dataBook/secSpFPoolDataList";
	}
	
	@PostMapping("/Notes")
	public String savesecSpFpNotes(
	        @ModelAttribute("idNotes") IdNotes idNotes
	)
	{
		FPFinancialSector fpData = null;
		if (idNotes != null)
		{
			if (idNotes.getSector() != null)
			{
				
				switch (idNotes.getSector())
				{
					case "Financials":
						
						Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(idNotes.getId()));
						if (fpDataO.isPresent())
						{
							fpData = fpDataO.get();
							//REset Notes
							fpData.setNotes(idNotes.getNotes());
							//Save the Entity
							repoFPFinancials.save(fpData);
							
						}
						break;
					
					default:
						break;
				}
			}
			
		}
		
		//REdirect to List for Specific Scrip 
		return "redirect:/databook/scrip/secsp/" + fpData.getSccode();
	}
	
}
