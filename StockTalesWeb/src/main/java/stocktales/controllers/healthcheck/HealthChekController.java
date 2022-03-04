package stocktales.controllers.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.healthcheck.intf.IHC_Srv;

@Controller
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/hc")
public class HealthChekController
{
	@Autowired
	private IHC_Srv hcSrv;
	
	@GetMapping("/{scCode}")
	private String showHCDetailsforScrip(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		String vwName = "hc/details";
		if (scCode != null && scCode.trim().length() > 3 && hcSrv != null)
		{
			try
			{
				hcSrv.Initialize(scCode);
			} catch (Exception e)
			{
				vwName = "Error";
				model.addAttribute("formError", e.getMessage());
			}
			hcSrv.processScripHealthCheck();
			model.addAttribute("hcResults", hcSrv.getResults());
			model.addAttribute("scCode", scCode);
		}
		
		return vwName;
	}
	
}
