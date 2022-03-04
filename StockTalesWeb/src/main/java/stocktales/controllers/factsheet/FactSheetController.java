package stocktales.controllers.factsheet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/factsheet")
public class FactSheetController
{
	@GetMapping("/{scCode}")
	public String showFactSheet(
	        Model model
	)
	{
		
		return "success";
	}
}
