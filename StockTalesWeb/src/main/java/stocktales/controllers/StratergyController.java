package stocktales.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary_List_Repo;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocationList;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStrategySrv;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.dataBook.model.entity.adhocScrips.AdhocScrip;
import stocktales.dataBook.model.repo.adhocScrips.RepoAdhocScrip;
import stocktales.historicalPrices.enums.EnumInterval;
import stocktales.historicalPrices.pojo.StgyRelValuation;
import stocktales.historicalPrices.srv.intf.ITimeSeriesStgyValuationSrv;
import stocktales.predicates.GenericSCEDRCSummaryPredicate;
import stocktales.predicates.manager.PredicateManager;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.services.interfaces.ScripService;
import stocktales.strategy.helperPOJO.NiftyStgyCAGR;

@Controller
@RequestMapping("/stratergy")
public class StratergyController
{
	@Autowired
	private SC_EDRC_Summary_List_Repo edrcFilteredRepo;
	
	@Autowired
	private IScAllocationListRepo allocationsRepo;
	
	@Autowired
	private PredicateManager predMgrSrv;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private RepoAdhocScrip repoAdhocSc;
	
	@Autowired
	private IStrategySrv stgySrv;
	
	@Autowired
	private IRepoStrategy stgRepo;
	
	@Autowired
	private IStgyRebalanceSrv stgyRebal_Srv;
	
	@Autowired
	private ICAGRCalcSrv cagrCalcSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Autowired
	private ITimeSeriesStgyValuationSrv timeSeriesSrv;
	
	private List<String> scCodes;
	
	@GetMapping("/myFilter")
	public String showStratergyStaging(
	        Model model
	)
	{
		
		if (allocationsRepo != null)
		{
			
			try
			{
				//Populate Scrip Codes if Null
				getSCCodes();
				
				//Add a Local Variable to Filter Scrips coming from Filter Predicate
				List<String> scCodesList = new ArrayList<String>();
				scCodesList = this.scCodes;
				
				//Get List of Scrips from Autowired Filtered List
				if (this.edrcFilteredRepo != null)
				{
					ScAllocationList scAllocList = new ScAllocationList();
					if (this.edrcFilteredRepo.getEdrcFilteredList() != null)
					{
						if (this.edrcFilteredRepo.getEdrcFilteredList().size() > 0)
						{
							List<String> scrips = new ArrayList<String>();
							
							edrcFilteredRepo.getEdrcFilteredList().stream().filter(x -> scrips.add(x.getScCode()))
							        .distinct().collect(Collectors.toList());
							
							//Prepare the SCrip List
							if (scrips.size() > 0)
							{
								scAllocList.setScAllocations(allocationsRepo.stageAllocationsforScrips(scrips));
								
								model.addAttribute("scStagingList", scAllocList);
							}
							
						}
						
					}
					
					for (ScAllocation scAllocation : scAllocList.getScAllocations())
					{
						Optional<String> scCodeFound = scCodesList.stream()
						        .filter(x -> x.toString().equals(scAllocation.getScCode())).findFirst();
						
						if (scCodeFound.isPresent())
						{
							scCodesList.remove(scAllocation.getScCode());
							
						}
						
					}
				}
				
				//Add Blank Scrip Code to Model
				model.addAttribute("scCode", new String());
				//Add Scrip Codes
				model.addAttribute("scCodes", scCodesList);
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return "strategy/Staging_1";
	}
	
	@GetMapping("/staging_1")
	public String showStaging_1(
	        Model model
	)
	{
		if (allocationsRepo != null)
		{
			if (allocationsRepo.getScAllocList() != null)
			{
				if (allocationsRepo.getScAllocList().size() > 0)
				{
					
					ScAllocationList scAllocList = new ScAllocationList();
					scAllocList.setScAllocations(allocationsRepo.getScAllocList());
					//Add Allocations List
					model.addAttribute("scStagingList", scAllocList);
					
					//Prepare List of Scrips as per Allocations List
					List<String> scrips = new ArrayList<String>();
					allocationsRepo.getScAllocList().stream().filter(x -> scrips.add(x.getScCode())).distinct()
					        .collect(Collectors.toList());
					
					//Get all Scrips Codes
					//Add a Local Variable to Filter Scrips coming from Current Allocations Collection
					List<String> scCodesList = new ArrayList<String>();
					scCodesList = getSCCodes(); //Al SC Codes
					
					for (ScAllocation scAllocation : scAllocList.getScAllocations())
					{
						Optional<String> scCodeFound = scCodesList.stream()
						        .filter(x -> x.toString().equals(scAllocation.getScCode())).findFirst();
						
						if (scCodeFound.isPresent())
						{
							scCodesList.remove(scAllocation.getScCode());
							
						}
						
					}
					
					//Add Blank Scrip Code to Model
					model.addAttribute("scCode", new String());
					//Add Scrip Codes
					model.addAttribute("scCodes", scCodesList);
					
				}
			}
		}
		
		return "strategy/Staging_1";
	}
	
	@GetMapping("/staging/delete/{scCode}")
	public String showStaging_1afterScDelete(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			this.allocationsRepo.delete(scCode);
		}
		return "redirect:/stratergy/staging_1";
	}
	
	@GetMapping("/staging_2")
	public String showStaging_2(
	        Model model
	)
	{
		if (stgySrv != null)
		{
			
			//Calculate CAGRs for Intervals before Saving
			List<NiftyStgyCAGR> stgyResults = new ArrayList<NiftyStgyCAGR>();
			try
			{
				cagrCalcSrv.Initialize4mSCAllocationBuffer();
				model.addAttribute("qStats", stgySrv.getQuickStats());
				model.addAttribute("cagrResults", cagrCalcSrv.getScAllocCAGRResults());
				model.addAttribute("strategy", new Strategy());
			}
			
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return "strategy/Staging_2";
		
	}
	
	@GetMapping("/list")
	public String listAll(
	        Model model
	)
	{
		if (stgySrv != null)
		{
			model.addAttribute("stgList", stgySrv.getStrategiesList());
		}
		return "strategy/list";
	}
	
	@GetMapping("/{stgId}")
	public String showStrategyDetails(
	        @PathVariable("stgId") int stgId, Model model
	)
	{
		if (stgId > 0)
		{
			
			//Get the Strategy and its allocItems
			if (stgySrv != null)
			{
				this.allocationsRepo.clear_replace_allocations(stgySrv.getValuationsSimulationforStrategy(stgId));
				//Add Allocations List
				ScAllocationList scAllocList = new ScAllocationList();
				scAllocList.setScAllocations(allocationsRepo.getScAllocList());
				//Add Strategy ID
				scAllocList.setStgyId(stgId);
				model.addAttribute("scStagingList", scAllocList);
				
			}
		}
		
		return "strategy/simulation";
	}
	
	@GetMapping("/rebal/{stgId}")
	public String showRebalancing(
	        @PathVariable("stgId") String stgId, Model model
	)
	{
		Optional<Strategy> stgyO = stgRepo.findByStid(new Integer(stgId));
		if (stgyO.isPresent())
		{
			
			GenericSCEDRCSummaryPredicate predBean = predMgrSrv.getActivePredicateBean(stgyO.get().getPredicatebean());
			if (predBean != null)
			{
				model.addAttribute("criteria", predBean.getNotes());
			}
			model.addAttribute("concept", stgyO.get().getConcept());
			model.addAttribute("rblPOJO", stgyRebal_Srv.triggerReBalancingforStgy(new Integer(stgId)));
			model.addAttribute("scCodes", getSCCodes());
		}
		return "strategy/reBalance";
	}
	
	@GetMapping("/timeseries/{stgId}/{interval}")
	public String showTimeSeries(
	        @PathVariable("stgId") String stgId, @PathVariable("interval") EnumInterval interval, Model model
	)
	{
		int          stgyId = new Integer(stgId);
		EnumInterval intv   = interval;
		
		if (stgyId > 0 && timeSeriesSrv != null && intv != null && this.stgRepo != null)
		{
			//Default option for last 1 year
			try
			{
				List<StgyRelValuation> pricePoints = timeSeriesSrv.getValuationsforStrategy(stgyId, intv);
				if (pricePoints != null)
				{
					model.addAttribute("latestVal",
					        Precision.round(pricePoints.get(pricePoints.size() - 1).getValue(), 1));
					model.addAttribute("stgyData", stgRepo.findByStidShort(stgyId));
					model.addAttribute("seriesval", pricePoints);
					model.addAttribute("interval", intv);
				}
				
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return "strategy/timeseries";
		
	}
	
	@GetMapping("/rebal/add4mProposal/{scCode}")
	public String addScriptoCurrScrips(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		stgyRebal_Srv.addNewScrip(scCode);
		model.addAttribute("rblPOJO", stgyRebal_Srv.getRblPojo());
		model.addAttribute("scCodes", getSCCodes());
		Optional<Strategy> stgyO = stgRepo.findByStid(this.stgyRebal_Srv.getRblPojo().getStid());
		if (stgyO.isPresent())
		{
			model.addAttribute("concept", stgyO.get().getConcept());
			GenericSCEDRCSummaryPredicate predBean = predMgrSrv.getActivePredicateBean(stgyO.get().getPredicatebean());
			if (predBean != null)
			{
				model.addAttribute("criteria", predBean.getNotes());
			}
		}
		
		return "strategy/reBalance";
	}
	
	@GetMapping("/rebal/rem4mProposal/{scCode}")
	public String removeScripFromCurrScrips(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		stgyRebal_Srv.deleteScrip(scCode);
		model.addAttribute("rblPOJO", stgyRebal_Srv.getRblPojo());
		model.addAttribute("scCodes", getSCCodes());
		
		Optional<Strategy> stgyO = stgRepo.findByStid(this.stgyRebal_Srv.getRblPojo().getStid());
		if (stgyO.isPresent())
		{
			model.addAttribute("concept", stgyO.get().getConcept());
			GenericSCEDRCSummaryPredicate predBean = predMgrSrv.getActivePredicateBean(stgyO.get().getPredicatebean());
			if (predBean != null)
			{
				model.addAttribute("criteria", predBean.getNotes());
			}
		}
		
		return "strategy/reBalance";
	}
	
	@GetMapping("/rebal/delete/{scCode}")
	public String deleteScripFromCurrScrips(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		stgyRebal_Srv.deleteScrip(scCode);
		model.addAttribute("rblPOJO", stgyRebal_Srv.getRblPojo());
		model.addAttribute("scCodes", getSCCodes());
		
		Optional<Strategy> stgyO = stgRepo.findByStid(this.stgyRebal_Srv.getRblPojo().getStid());
		if (stgyO.isPresent())
		{
			model.addAttribute("concept", stgyO.get().getConcept());
			GenericSCEDRCSummaryPredicate predBean = predMgrSrv.getActivePredicateBean(stgyO.get().getPredicatebean());
			if (predBean != null)
			{
				model.addAttribute("criteria", predBean.getNotes());
			}
		}
		
		return "strategy/reBalance";
	}
	
	@GetMapping("/rebal/staging")
	public String showRebalancing_Stage1(
	        Model model
	)
	{
		if (stgyRebal_Srv.getRblPojo().getCurrScrips() != null)
		{
			if (stgyRebal_Srv.getRblPojo().getCurrScrips().size() > 0)
			{
				this.allocationsRepo.clearAllocations();
				this.allocationsRepo.stageAllocationsforScrips(stgyRebal_Srv.getRblPojo().getCurrScrips());
				ScAllocationList scAllocList = new ScAllocationList();
				scAllocList.setScAllocations(allocationsRepo.getScAllocList());
				//Add Allocations List
				model.addAttribute("scStagingList", scAllocList);
				
			}
		}
		return "strategy/rebal_Staging1";
	}
	
	@GetMapping("/rebal/staging_refresh")
	public String showRebalancingRefresh_Stage1(
	        Model model
	)
	{
		if (this.allocationsRepo.getScAllocList() != null)
		{
			if (this.allocationsRepo.getScAllocList().size() > 0)
			{
				
				ScAllocationList scAllocList = new ScAllocationList();
				scAllocList.setScAllocations(allocationsRepo.getScAllocList());
				//Add Allocations List
				model.addAttribute("scStagingList", scAllocList);
				
			}
		}
		return "strategy/rebal_Staging1";
	}
	
	@GetMapping("/rebal/Staging2")
	public String showRebalancesStgyFinal(
	        Model model
	)
	{
		if (stgyRebal_Srv.getRblPojo() != null && this.allocationsRepo != null && stgySrv != null)
		{
			
			if (stgyRebal_Srv.getRblPojo().getStid() > 0 && allocationsRepo.getScAllocList() != null)
			{
				if (
				    stgyRebal_Srv.getRblPojo().getCurrScrips().size() > 0 && allocationsRepo.getScAllocList().size() > 0
				)
				{
					//Calculate CAGRs for Intervals before Saving
					List<NiftyStgyCAGR> stgyResults = new ArrayList<NiftyStgyCAGR>();
					
					try
					{
						cagrCalcSrv.Initialize4mSCAllocationBuffer();
						model.addAttribute("qStats", stgySrv.getQuickStats());
						model.addAttribute("cagrResults", cagrCalcSrv.getScAllocCAGRResults());
						Optional<Strategy> stgyO = this.stgRepo.findByStid(stgyRebal_Srv.getRblPojo().getStid());
						if (stgyO.isPresent())
						{
							model.addAttribute("strategy", stgyO.get());
						}
						
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
		}
		return "strategy/rebal_Staging2";
	}
	
	/*
	 * ____________________________________________________________________________________
	 * 
	 *                     POST MAPPINGS
	 * _____________________________________________________________________________________
	 */
	
	@PostMapping("/myFilter/addScrip")
	public String addScripToStrategy(
	        @ModelAttribute("scCode") String scCode
	)
	{
		
		if (scCode != null)
		{
			this.allocationsRepo.addNew(scCode);
		}
		
		return "redirect:/stratergy/staging_1";
		
	}
	
	@PostMapping(value = "/staging_1", params = "action=validProc")
	public String refreshStaging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		if (scAllocations != null)
		{
			List<ScAllocation> allocList = new ArrayList<ScAllocation>();
			allocList = scAllocations.getScAllocations();
			if (allocList.size() > 0)
			{
				//Totals of Allocation rounded to zero places should be 100
				double totalAllocPer = scAllocations.getScAllocations().stream()
				        .mapToDouble(ScAllocation::getAllocation).sum();
				totalAllocPer = Precision.round(totalAllocPer, 0);
				if (totalAllocPer == 100)
				{
					// Initialize Session Bean for Stratergy
					stgySrv.Initialize();
					
					// Load on Allocations
					stgySrv.loadAllocationItems(allocList);
					
				}
				
			}
		}
		return "redirect:/stratergy/staging_2";
	}
	
	@PostMapping(value = "/staging_1", params = "action=refresh")
	public String vaildateProcStaging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		if (scAllocations != null)
		{
			refreshAllocations(scAllocations);
		}
		//redirect to stratergy/staging_1
		return "redirect:/stratergy/staging_1";
	}
	
	@PostMapping(value = "/rebal_staging_1", params = "action=refresh")
	public String validateRebal_Staging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		if (scAllocations != null)
		{
			refreshAllocations(scAllocations);
		}
		//redirect to stratergy/staging_1
		return "redirect:/stratergy/rebal/staging_refresh";
	}
	
	@PostMapping(value = "/rebal_staging_1", params = "action=validProc")
	public String procRebalStaging_1(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	
	)
	{
		
		String viewName = "strategy/rebal_Staging1";
		
		if (scAllocations != null)
		{
			List<ScAllocation> allocList = new ArrayList<ScAllocation>();
			allocList = scAllocations.getScAllocations();
			if (allocList.size() > 0)
			{
				//Totals of Allocation rounded to zero places should be 100
				double totalAllocPer = scAllocations.getScAllocations().stream()
				        .mapToDouble(ScAllocation::getAllocation).sum();
				totalAllocPer = Precision.round(totalAllocPer, 0);
				
				if (totalAllocPer == 100)
				{
					refreshAllocations(scAllocations);
					
					// Initialize Session Bean for Strategy
					stgySrv.Initialize();
					
					// Load on Allocations
					stgySrv.loadAllocationItems(allocList);
					
					viewName = "redirect:/stratergy/rebal/Staging2";
				}
				
				else //Allocations sum != 100
				{
					//Reset Allocations as per Form View
					
					refreshAllocations(scAllocations);
					
					ScAllocationList scAllocList = new ScAllocationList();
					scAllocList.setScAllocations(allocationsRepo.getScAllocList());
					//Add Allocations List
					model.addAttribute("scStagingList", scAllocList);
					
					String msgText = msgSrc.getMessage("ERR_STGY_ALLOC", new Object[]
					{ totalAllocPer }, Locale.ENGLISH);
					model.addAttribute("formError", msgText);
					
				}
				
			}
		}
		return viewName;
	}
	
	@PostMapping("/save")
	public String saveStrategy(
	        @ModelAttribute("strategy") Strategy strategy
	
	)
	{
		if (strategy != null && stgySrv != null)
		{
			stgySrv.save(strategy);
		}
		
		return "redirect:/stratergy/list";
	}
	
	@PostMapping("/simulation")
	public String simulateStrategy(
	        @ModelAttribute("scStagingList") ScAllocationList scAllocations, Model model
	)
	{
		
		refreshAllocations(scAllocations);
		//Add Allocations List
		ScAllocationList scAllocList = new ScAllocationList();
		scAllocList.setScAllocations(this.allocationsRepo.getScAllocList());
		//Add Strategy ID
		scAllocList.setStgyId(scAllocations.getStgyId());
		model.addAttribute("scStagingList", scAllocList);
		
		return "strategy/simulation";
	}
	
	@PostMapping("/rebal/addNewScrip")
	public String addAdHocScripRebal(
	        @ModelAttribute("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			stgyRebal_Srv.addNewScrip(scCode);
			model.addAttribute("rblPOJO", stgyRebal_Srv.getRblPojo());
			model.addAttribute("scCodes", getSCCodes());
			
			Optional<Strategy> stgyO = stgRepo.findByStid(this.stgyRebal_Srv.getRblPojo().getStid());
			if (stgyO.isPresent())
			{
				model.addAttribute("concept", stgyO.get().getConcept());
				GenericSCEDRCSummaryPredicate predBean = predMgrSrv
				        .getActivePredicateBean(stgyO.get().getPredicatebean());
				if (predBean != null)
				{
					model.addAttribute("criteria", predBean.getNotes());
				}
			}
			
		}
		
		return "strategy/reBalance";
	}
	
	@PostMapping("save_rebal")
	public String saveRebalStgy(
	        @ModelAttribute("strategy") Strategy Stgy, Model model
	)
	{
		if (Stgy != null)
		{
			stgyRebal_Srv.saveStrategy(Stgy);
		}
		
		return "redirect:/stratergy/list";
	}
	
	/*************************************************************************
	 * ------------------PRIVATE METHODS -----------------------------------
	 *************************************************************************/
	
	private void refreshAllocations(
	        ScAllocationList scAllocations
	)
	{
		List<ScAllocation> allocList = new ArrayList<ScAllocation>();
		allocList = scAllocations.getScAllocations();
		if (allocList.size() > 0)
		{
			//for Each Allocation Item
			for (ScAllocation scAllocation : allocList)
			{
				
				//Need to Consider AdHoc Scrips Too here
				Optional<AdhocScrip> adSCO = repoAdhocSc.findBySccodeIgnoreCase(scAllocation.getScCode());
				if (adSCO.isPresent())
				{
					//As is with User Ascribed Allocation
					this.allocationsRepo.refreshAllocation(scAllocation);
				}
				
				else
				{
					
					//Get the Valuation = Retrigger Valuation Calculation using current CMP MoS
					ScValuation scValRecalc = scValSrv.getValuationforScrip(scAllocation.getScCode(),
					        scAllocation.getCMP(), scAllocation.getMoS());
					if (scValRecalc != null)
					{
						//Create SCAllocation POJO using this valuation calc above
						ScAllocation newAlloc = new ScAllocation(scValRecalc);
						//Set allocation Percentage as per POJO in loop pass to Create SC Allocation
						newAlloc.setAllocation(scAllocation.getAllocation());
						//Call Repo Method Refresh passing the Updated SCAllocation POJO
						this.allocationsRepo.refreshAllocation(newAlloc);
					}
				}
				
			}
			
		}
	}
	
	/* _______________________________________________________________________________________
	 * 
	 *                                  PRIVATE METHODS
	 * _______________________________________________________________________________________
	 */
	
	public List<String> getSCCodes(
	)
	{
		
		//Populate Scrip Codes if Null
		if (this.scCodes == null)
		{
			if (scSrv != null)
			{
				try
				{
					this.scCodes = scSrv.getAllScripNames();
					List<String> adSC = repoAdhocSc.getAllAdhoScripNames();
					if (adSC != null)
					{
						if (adSC.size() > 0)
						{
							this.scCodes.addAll(adSC);
						}
					}
					
				} catch (EX_General e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return this.scCodes;
		
	}
	
}
