package stocktales.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.tracker.interfaces.ITrackerSrv;

@Controller
@RequestMapping("/tracker")
public class TrackerController
{
	@Autowired
	private ITrackerSrv trackerSrv;
	
	@GetMapping("/test")
	public String showasTest(
	        Model model
	)
	{
		
		String[] scrips = new String[]
		{ "AFFLE", "GLAND", "DIVISLAB", "VINATIORGA", "PIIND" };
		
		try
		{
			model.addAttribute("result", trackerSrv.getforScrips(scrips));
		}
		
		catch (Exception e)
		{
			return "Error!";
		}
		return "tracker/trackerView";
		
	}
	
}
