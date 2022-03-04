package stocktales.controllers.config.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import stocktales.usersPF.intf.ISessionUserManager;

@Controller
public class HomeController
{
	@Autowired
	private ISessionUserManager usrMgrSrv;
	
	@GetMapping("/")
	public String showHome(
	        @CurrentSecurityContext(expression = "authentication.name") String username
	)
	
	{
		if (username != null)
		{
			//Set it in Session Bean
			usrMgrSrv.setUserName(username);
		}
		return "home/StockTalesIndex";
	}
}
