package stocktales.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController
{
	@GetMapping("/showMyLoginPage")
	public String showMyLoginPage(
	)
	{
		
		return "login/fancy-login";
		
	}
	
	// add request mapping for /access-denied
	
	@GetMapping("/access-denied")
	public String showAccessDenied(
	)
	{
		
		return "login/access-denied";
		
	}
	
	@GetMapping("/register")
	public String showRegister(
	        Model model
	)
	{
		return "success";
	}
}
