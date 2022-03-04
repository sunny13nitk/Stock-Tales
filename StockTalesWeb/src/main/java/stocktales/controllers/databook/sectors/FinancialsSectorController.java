package stocktales.controllers.databook.sectors;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.helperPojo.IdNotes;
import stocktales.dataBook.model.entity.sectors.FPFinancialSector;
import stocktales.dataBook.model.repo.sectors.RepoFPFinancialSector;
import stocktales.services.interfaces.ScripService;
import stocktales.utilities.DurationUtilities;
/*
 * -----------------------------------------------------------------------------
 *   -------------        FINANCIAL SECTOR FIELD POOL     ----------------------
 * -----------------------------------------------------------------------------
 */

@Controller
@RequestMapping("/Financials")
public class FinancialsSectorController
{
	
	@Autowired
	private RepoFPFinancialSector repoFPFinancials;
	
	@Autowired
	private ScripService scSrv;
	
	@GetMapping("/newfp/{scCode}")
	public String createFinSecFPool(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			FPFinancialSector newfPool = new FPFinancialSector();
			newfPool.setSccode(scCode);
			model.addAttribute("fpdata", newfPool);
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
		}
		
		return "scrips/dataBook/sectors/Financials";
	}
	
	@GetMapping("/{fpid}")
	public String showFinancialsfpoolData(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		FPFinancialSector           fpData  = null;
		Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(fpid));
		if (fpDataO.isPresent())
		{
			fpData = fpDataO.get();
		}
		model.addAttribute("fpdata", fpData);
		model.addAttribute("years", DurationUtilities.getYearsList(-5));
		model.addAttribute("scCode", fpData.getSccode());
		return "scrips/dataBook/sectors/Financials";
	}
	
	@GetMapping("/{scCode}/{interval}/{valM}/{valD}")
	public String showFinancialsfpoolDataByInterval(
	        @PathVariable("scCode") String scCode, @PathVariable("interval") EnumInterval intervalType, @PathVariable(
	            "valM"
	        ) String valM, @PathVariable("valD") String valD, Model model
	)
	{
		//Default View in case FPool with Interval Found - else New FP View
		String desurl = "scrips/dataBook/sectors/Financials";
		if (scCode != null && intervalType != null && valM != null)
		{
			
			Optional<FPFinancialSector> fpDataO = repoFPFinancials.findAlLBySccodeAndIntervalAndValmAndVald(scCode,
			        intervalType, new Integer(valM), new Integer(valD));
			if (fpDataO.isPresent())
			{
				desurl = "redirect:/Financials/" + fpDataO.get().getId();
			} else //New FieldPool Data - Interval Defaulted
			{
				FPFinancialSector newfPool = new FPFinancialSector();
				newfPool.setInterval(intervalType);
				newfPool.setValm(new Integer(valM));
				newfPool.setValm(new Integer(valD));
				newfPool.setSccode(scCode);
				model.addAttribute("fpdata", newfPool);
				model.addAttribute("years", DurationUtilities.getYearsList(-5));
				
			}
			
		}
		
		return desurl;
	}
	
	@GetMapping("/Notes/{fpid}")
	public String showNotes(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		if (fpid != null)
		{
			Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(fpid));
			if (fpDataO.isPresent())
			{
				model.addAttribute("scCode", fpDataO.get().getSccode());
				String sector = scSrv.getSectorforScrip(fpDataO.get().getSccode());
				
				if (fpDataO.get().getVald() > 0)
				{
					model.addAttribute("interval", fpDataO.get().getValm() + "-Q" + fpDataO.get().getVald());
				} else
				{
					model.addAttribute("interval", fpDataO.get().getValm());
				}
				
				IdNotes idNotes = new IdNotes(fpDataO.get().getId(), sector, fpDataO.get().getNotes());
				model.addAttribute("idNotes", idNotes);
			}
			
			///Populate IdNotes POJO and navigate
		}
		
		return "scrips/dataBook/NotesPanel_SS_FP";
	}
	
	/**
	 * ________________________________________________________________________
	 *                       POST MAPPINGS
	 * ________________________________________________________________________
	 */
	
	@PostMapping("/save")
	public String savesecSpFpData(
	        @ModelAttribute("fpdata") FPFinancialSector fpData
	)
	{
		if (fpData != null)
		{
			if (fpData.getSccode() != null)
			{
				repoFPFinancials.save(fpData);
			}
		}
		//REdirect to List for Specific Scrip - /secSpData/{scCode}/list
		return "redirect:/databook/scrip/secsp/" + fpData.getSccode();
		
	}
	
	@PostMapping("/Notes")
	public String savesecSpFpNotes(
	        @ModelAttribute("idNotes") IdNotes idNotes
	)
	{
		FPFinancialSector fpData = null;
		if (idNotes != null)
		{
			Optional<FPFinancialSector> fpDataO = repoFPFinancials.findById(new Long(idNotes.getId()));
			if (fpDataO.isPresent())
			{
				fpData = fpDataO.get();
				//REset Notes
				fpData.setNotes(idNotes.getNotes());
				//Save the Entity
				repoFPFinancials.save(fpData);
				
			}
		}
		
		//REdirect to List for Specific Scrip 
		return "redirect:/databook/scrip/secsp/" + fpData.getSccode();
	}
}
