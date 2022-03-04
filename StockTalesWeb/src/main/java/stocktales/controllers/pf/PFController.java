package stocktales.controllers.pf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.strategy.helperPOJO.StgyStatsSummary;
import stocktales.strategy.intf.IStrategyStatsSrv;
import stocktales.usersPF.intf.ISessionUserManager;
import stocktales.usersPF.model.UserPFConfig;
import stocktales.usersPF.model.UserStrategy;
import stocktales.usersPF.repo.RepoBrokers;
import stocktales.usersPF.repo.RepoUserPFConfig;

@Controller
@RequestMapping("/pf")
public class PFController
{
	
	@Autowired
	private ISessionUserManager usrMgrSrv;
	
	@Autowired
	private RepoBrokers repoBrokers;
	
	@Autowired
	private RepoUserPFConfig repoPFConfig;
	
	@Autowired
	private IStrategyStatsSrv stgyStatsSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@GetMapping("/ovw")
	public String showUserPFOvw(
	        Model model
	)
	{
		String vwName = "pf/pfOvw";
		if (usrMgrSrv.getUserName() != null)
		{
			usrMgrSrv.loadPFDetails();
			UserPFConfig pfConfig = usrMgrSrv.getUserPFDetails();
			try
			{
				if (pfConfig != null)
				{
					//Load the model accordingly and Traverse
					model.addAttribute("pfConfig", pfConfig);
					model.addAttribute("depSummary", usrMgrSrv.getPFBalDepSummary());
					model.addAttribute("canSubscribe", usrMgrSrv.CanUserSubscibeTOStrategies());
					model.addAttribute("stgySSList", usrMgrSrv.getUserStgySnapshots());
					
				} else
				{
					//Initialize and Traverse
					vwName   = "pf/pfCusForm";
					pfConfig = new UserPFConfig();
					pfConfig.setUsername(usrMgrSrv.getUserName());
					model.addAttribute("pfConfig", pfConfig);
					model.addAttribute("message", msgSrc.getMessage("NO_PFCUS", null, Locale.ENGLISH));
					model.addAttribute("brokers", repoBrokers.getBrokerNames());
					
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return vwName;
	}
	
	@GetMapping("/cus")
	public String showCustomizing(
	        Model model
	)
	{
		if (usrMgrSrv.getUserName() != null)
		{
			usrMgrSrv.loadPFDetails();
			UserPFConfig pfConfig = usrMgrSrv.getUserPFDetails();
			if (pfConfig != null)
			{
				if (pfConfig.getCid() > 0)
				{
					model.addAttribute("pfConfig", pfConfig);
					model.addAttribute("message", msgSrc.getMessage("EDIT_PFCUS", null, Locale.ENGLISH));
					model.addAttribute("brokers", repoBrokers.getBrokerNames());
					
				}
			}
		}
		
		return "pf/pfCusForm";
	}
	
	@GetMapping("/subscribe")
	public String showSubscribeOptions(
	        Model model
	)
	{
		List<Integer> canbeSubsStg = usrMgrSrv.getSubscribableStrategies();
		
		if (canbeSubsStg != null)
		{
			if (canbeSubsStg.size() > 0)
			{
				try
				{
					List<StgyStatsSummary> summaryList = new ArrayList<StgyStatsSummary>();
					for (Integer stid : canbeSubsStg)
					{
						summaryList.add(stgyStatsSrv.getStatsforStrategy(stid));
					}
					
					model.addAttribute("summaryList", summaryList);
				} catch (Exception e)
				{
					model.addAttribute("formError", e.getMessage());
				}
			}
		}
		
		return "pf/subscribe";
	}
	
	@GetMapping("/addSubscription/{stid}")
	public String addSubscription(
	        @PathVariable String stid, Model model
	)
	{
		int stgyid = new Integer(stid);
		if (stgyid > 0)
		{
			usrMgrSrv.loadPFDetails();
			UserPFConfig pfConfig = usrMgrSrv.getUserPFDetails();
			
			UserStrategy newStgySubs = new UserStrategy();
			newStgySubs.setActive(true); //active
			newStgySubs.setStid(stgyid); //Strategy ID
			
			//Subscribed by User PF Config
			pfConfig.subscribeToStrategy(newStgySubs);
			
			//Save the User PF Config
			repoPFConfig.save(pfConfig);
			
		}
		
		//Back to Portfolio OverView
		return "redirect:/pf/ovw";
	}
	
	@GetMapping("/deactivate/{id}")
	public String deactivateSubscription(
	        @PathVariable String id, Model model
	)
	{
		
		usrMgrSrv.deactivateStrategyforUser(new Long(id));
		
		//Back to Portfolio OverView
		return "redirect:/pf/ovw";
	}
	
	@PostMapping("/saveConfig")
	public String saveConfig(
	        @Valid @ModelAttribute("pfConfig") UserPFConfig pfConfig, BindingResult bres, Model model
	)
	{
		String vwName = "redirect:/pf/ovw";
		if (pfConfig != null)
		{
			if (bres.hasErrors())
			{
				model.addAttribute("brokers", repoBrokers.getBrokerNames());
				vwName = "pf/pfCusForm";
			} else
			{
				// Set Dividends and Realized P&L at User level to 0
				pfConfig.setRealzdiv(0);
				pfConfig.setRealzpl(0);
				
				repoPFConfig.save(pfConfig);
			}
			
		}
		
		return vwName;
	}
}
