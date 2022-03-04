package stocktales.controllers.databook.scrips;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.model.entity.scripSp.FPBAJFINANCE;
import stocktales.dataBook.model.repo.scripSp.RepoBAJFINANCE;
import stocktales.utilities.DurationUtilities;

@Controller
@RequestMapping("/BAJFINANCE")
public class BAJFINANCEController
{
	@Autowired
	private RepoBAJFINANCE repoBFAL;
	
	private final String sector = "Financials";
	
	@GetMapping("/fp") //New Field Pool 
	public String shownewFP(
	        Model model
	)
	{
		
		FPBAJFINANCE newfPool = new FPBAJFINANCE();
		model.addAttribute("fpdata", newfPool);
		model.addAttribute("years", DurationUtilities.getYearsList(-5));
		return "/scrips/dataBook/scrips/BAJFINANCE";
	}
	
	@GetMapping("{fpid}")
	public String showFPDetails(
	        @PathVariable("fpid") String fpid, Model model
	)
	{
		model.addAttribute("fpdata", repoBFAL.findById(new Long(fpid)));
		model.addAttribute("years", DurationUtilities.getYearsList(-5));
		
		return "/scrips/dataBook/scrips/BAJFINANCE";
	}
	
	@GetMapping("/{interval}/{valM}/{valD}")
	public String showFinancialsfpoolDataByInterval(
	        @PathVariable("interval") EnumInterval intervalType, @PathVariable("valM") String valM, @PathVariable(
	            "valD"
	        ) String valD, Model model
	)
	{
		boolean found = false;
		//Default View in case FPool with Interval Found - else New FP View
		String desurl = "redirect:/BAJFINANCE/fp";
		if (intervalType != null && valM != null)
		{
			
			List<FPBAJFINANCE> fpDataList = repoBFAL.findAllByIntervalAndValmAndVald(intervalType, new Integer(valM),
			        new Integer(valD));
			if (fpDataList != null)
			{
				if (fpDataList.size() > 0)
				{
					desurl = "redirect:/BAJFINANCE/" + fpDataList.get(0).getId();
					found  = true;
				}
			}
			
			if (found == false) //New FieldPool Data - Interval Defaulted
			{
				FPBAJFINANCE newfPool = new FPBAJFINANCE();
				newfPool.setInterval(intervalType);
				newfPool.setValm(new Integer(valM));
				newfPool.setValm(new Integer(valD));
				model.addAttribute("fpdata", newfPool);
				model.addAttribute("years", DurationUtilities.getYearsList(-5));
				desurl = "/scrips/dataBook/scrips/BAJFINANCE";
			}
			
		}
		
		return desurl;
	}
	
	/*
	 * -----------------------------------------------------------------------
	 *                          POST MAPPING
	 * -----------------------------------------------------------------------
	 */
	@PostMapping("/fp")
	public String savefp(
	        @ModelAttribute("fpdata") FPBAJFINANCE fpData
	)
	{
		if (fpData != null)
		{
			repoBFAL.save(fpData);
		}
		return "redirect:/scrips/dataBook/scsp/BAJFINANCE";
	}
}
