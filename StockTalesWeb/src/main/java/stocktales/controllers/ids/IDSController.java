package stocktales.controllers.ids;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.model.pf.entity.MoneyBag;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.PFSchemaRebalUI;
import stocktales.IDS.pojo.UI.IDSBuyPropMassUpdateList;
import stocktales.IDS.pojo.UI.IDSOverAllocList;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.IDS_PF_Chart_DepAmnt;
import stocktales.IDS.pojo.UI.IDS_PF_Chart_PFReturns;
import stocktales.IDS.pojo.UI.IDS_PF_Chart_PLSpread;
import stocktales.IDS.pojo.UI.IDS_PF_OverAllocations;
import stocktales.IDS.pojo.UI.IDS_PF_OverAllocsContainer;
import stocktales.IDS.pojo.UI.IDS_XIRR_UI;
import stocktales.IDS.pojo.UI.MBUI;
import stocktales.IDS.pojo.UI.PFDBContainer;
import stocktales.IDS.pojo.UI.PFHoldingsPL;
import stocktales.IDS.srv.impl.IDS_CorePFSrv;
import stocktales.IDS.srv.intf.IDS_ConfigLoader;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;
import stocktales.IDS.srv.intf.IDS_PFDashBoardUISrv;
import stocktales.IDS.srv.intf.IDS_PFSchema_REbalUI_Srv;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.IDS.srv.intf.IDS_XIRR_UI_Srv;
import stocktales.NFS.repo.RepoBseData;
import stocktales.annotations.RetainView;
import stocktales.durations.UtilDurations;
import stocktales.strategy.helperPOJO.SectorAllocations;
import stocktales.usersPF.enums.EnumTxnType;

@Controller
@RequestMapping("/ids")
public class IDSController
{

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private Environment environment;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private IDS_PFSchema_REbalUI_Srv pfSchRebalSrv;

	@Autowired
	private IDS_PFDashBoardUISrv pfDashBSrv;

	@Autowired
	private IDS_CorePFSrv pfCoreSrv;

	@Autowired
	private IDS_ConfigLoader idsCfgSrv;

	@Autowired
	private IDS_VPSrv idsVPSrv;

	@Autowired
	private IDS_XIRR_UI_Srv xirrSrv;

	@Autowired
	private IDS_MoneyBagSrv mbSrv;

	private final String prodProfile = "prod";

	private final String testProfile = "test";

	private final String reRouteDBVw = "redirect:/ids/launch";

	@GetMapping("/launch")
	public String showIDSHome(Model model)
	{
		MBUI MB = new MBUI();
		model.addAttribute("MB", MB);

		if (pfDashBSrv != null)
		{
			try
			{
				PFDBContainer pfDBCon = pfDashBSrv.getPFDashBoardContainer();
				List<IDS_PF_Chart_PFReturns> retChart = new ArrayList<IDS_PF_Chart_PFReturns>();
				List<IDS_PF_Chart_PLSpread> plChart = new ArrayList<IDS_PF_Chart_PLSpread>();
				List<IDS_PF_Chart_DepAmnt> depChart = new ArrayList<IDS_PF_Chart_DepAmnt>();
				if (pfDBCon.getHoldings().size() > 0)
				{
					for (PFHoldingsPL holding : pfDBCon.getHoldings())
					{
						IDS_PF_Chart_PFReturns pfREt = new IDS_PF_Chart_PFReturns(holding.getScCode(),
								holding.getInvestments(), holding.getCurrVal(), holding.getPl());
						retChart.add(pfREt);

						IDS_PF_Chart_PLSpread plSpread = new IDS_PF_Chart_PLSpread(holding.getScCode(),
								holding.getPlPer());
						plChart.add(plSpread);
						IDS_PF_Chart_DepAmnt depAmntSpread = new IDS_PF_Chart_DepAmnt(holding.getScCode(),
								holding.getDepPer());
						depChart.add(depAmntSpread);

					}
				}
				model.addAttribute("pfDBCon", pfDBCon);
				model.addAttribute("retData", retChart);
				model.addAttribute("plData", plChart);
				model.addAttribute("depData", depChart);
				model.addAttribute("isOverAlloc", pfDashBSrv.areOverAllocationsPresent());

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "ids/IDShome";
	}

	@GetMapping("/xirr")
	public String showXIRR(Model model) throws Exception
	{
		if (xirrSrv != null && pfDashBSrv != null)
		{
			IDS_XIRR_UI xirrUI = xirrSrv.generateXIRRUI();
			if (xirrUI != null)
			{
				model.addAttribute("xirrUI", xirrUI);
				model.addAttribute("xirrChart", xirrUI.getXirrChart());
			}
		}
		return "ids/IDS_Xirr";
	}

	@GetMapping("/vprofile")
	public String showvpDetails(Model model) throws Exception
	{

		if (pfCoreSrv != null && idsCfgSrv != null)
		{
			model.addAttribute("vp", pfCoreSrv.refreshPFVolatilityProfiles());
			model.addAttribute("smaWts", idsCfgSrv.getSMAWts());
			model.addAttribute("vpDeps", idsCfgSrv.getVPDeployments());
			model.addAttribute("vpRange", idsCfgSrv.getVPRange());

			if (pfDashBSrv != null)
			{
				if (pfDashBSrv.getPFDashBoardContainer4mSession() != null)
				{
					if (pfDashBSrv.getPFDashBoardContainer4mSession().getSmaPvwL() != null)
					{

						model.addAttribute("smaList", pfDashBSrv.getPFDashBoardContainer4mSession().getSmaPvwL());
					} else
					{
						model.addAttribute("smaList", pfCoreSrv.getPFSchemaSMAPreview());
					}

				}
			}

		}

		return "ids/IDS_vpDetails";

	}

	@GetMapping("/buyP")
	@RetainView(viewName = "ids/IDshome")
	public String showbuyProposals(Model model) throws Exception
	{
		String viewName = "ids/IDSbuyP";

		// Check if Schema Exists
		if (repoPFSchema.count() == 0)
		{
			viewName = "ids/IDS_NoSchema";

		} else
		{
			// Get latest Buy Proposals from DB and also Update in PF Dashboard Container
			IDS_BuyProposalBO proposal = pfCoreSrv.getBuyProposals();
			if (proposal != null)
			{
				if (proposal.getBuyP().size() > 0)
				{
					// Synch to PF DashBoard Container
					pfDashBSrv.refreshContainer4RoundTrip(proposal);

					// Add to model
					model.addAttribute("proposal", proposal);

				} else // No Proposals Found - Go to DashBoard
				{
					viewName = "redirect:/ids/launch";
				}
			}

		}

		return viewName;
	}

	@GetMapping("/realign")
	public String showOverAllocations(Model model) throws Exception
	{
		String viewName = "/ids/IDSOverAlloc";
		if (pfDashBSrv != null)
		{
			IDS_PF_OverAllocsContainer overAllocContainer = pfDashBSrv.fetchOverAllocations();
			if (overAllocContainer != null)
			{
				if (overAllocContainer.getOverAllocs().size() > 0)
				{
					IDSOverAllocList overAllocList = new IDSOverAllocList();

					for (IDS_PF_OverAllocations overAllocI : overAllocContainer.getOverAllocs())
					{
						overAllocList.getOverAllocList().add(overAllocI);
					}

					model.addAttribute("overAllocContainer",
							this.pfDashBSrv.getPFDashBoardContainer4mSession().getOverAllocsContainer());
					model.addAttribute("ovAllocList", overAllocList);
				}
			}

		}

		return viewName;

	}

	@GetMapping("/buyP/edit")
	public String showbuyProposalMassUpdate(Model model) throws Exception
	{
		if (pfDashBSrv != null)
		{
			if (pfDashBSrv.getPFDashBoardContainer4mSession().getBuyProposals() != null)
			{
				if (pfDashBSrv.getPFDashBoardContainer4mSession().getBuyProposals().getBuyP() != null)
				{
					if (pfDashBSrv.getPFDashBoardContainer4mSession().getBuyProposals().getBuyP().size() > 0)
					{
						IDSBuyPropMassUpdateList props = new IDSBuyPropMassUpdateList();
						for (IDS_SCBuyProposal prop : pfDashBSrv.getPFDashBoardContainer4mSession().getBuyProposals()
								.getBuyP())
						{
							HCI hci = new HCI();

							hci.setTxntype(EnumTxnType.Buy);
							hci.setSccode(prop.getScCode());
							hci.setSmarank(prop.getSmaBreach().ordinal());
							hci.setTxnppu(prop.getPpuBuy());
							hci.setUnits(prop.getNumUnitsBuy());

							props.getBuyList().add(hci);

						}
						model.addAttribute("propList", props);
					}
				}
			}
		}

		return "ids/buyPUpdate";
	}

	@GetMapping("/newSchema")
	public String shownewSchema(Model model)
	{
		String viewName = "ids/IDS_updSchema";

		if (repoBseData != null && pfSchRebalSrv != null)
		{
			model.addAttribute("scrips", repoBseData.findAllNseCodes());
			model.addAttribute("schmPOJO", pfSchRebalSrv.createSchema());
		}

		return viewName;
	}

	@RetainView(viewName = "ids/IDS_updSchema")
	@GetMapping("/updateSchema")
	public String loadSchemaforupdate(Model model) throws Exception
	{
		String viewName = "ids/IDS_updSchema";

		if (repoBseData != null && pfSchRebalSrv != null)
		{

			if (repoPFSchema.count() > 0) // Only if Schema Exists
			{

				model.addAttribute("scrips", repoBseData.findAllNseCodes());
				model.addAttribute("schmPOJO", pfSchRebalSrv.uploadSchemaforUpdate());

				if (pfSchRebalSrv.getRebalContainer().getStats() != null)
				{

					if (pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs().size() > 0)
					{
						List<SectorAllocations> mCapchartData = new ArrayList<SectorAllocations>();
						for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs())
						{
							mCapchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

						}

						model.addAttribute("mCapData", mCapchartData);
					}

					if (pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs().size() > 0)
					{
						List<SectorAllocations> secchartData = new ArrayList<SectorAllocations>();
						for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs())
						{
							secchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

						}

						model.addAttribute("secData", secchartData);
					}
					model.addAttribute("seriesval", pfSchRebalSrv.getRebalContainer().getStats().getDateVals());
				}
			} else
			{
				viewName = reRouteDBVw;
			}
		}

		return viewName;

	}

	@RetainView(viewName = "ids/IDS_updSchema")
	@GetMapping("pfSchema/delete/{scCode}")
	public String removeScrip4mSchema(@PathVariable("scCode") String scCode, Model model) throws Exception
	{
		String viewName = "ids/IDS_updSchema";
		if (scCode != null)
		{
			if (pfSchRebalSrv != null)
			{

				pfSchRebalSrv.removeScrip4mSchema(scCode);
				/*
				 * REfurbish Model
				 */
				model.addAttribute("scrips", repoBseData.findAllNseCodes());
				model.addAttribute("schmPOJO", pfSchRebalSrv.getRebalContainer());
				model.addAttribute("formError", null); // Remove Error if Any on Succ. Validation

				if (pfSchRebalSrv.getRebalContainer().getStats() != null)
				{

					if (pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs().size() > 0)
					{
						List<SectorAllocations> mCapchartData = new ArrayList<SectorAllocations>();
						for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs())
						{
							mCapchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

						}

						model.addAttribute("mCapData", mCapchartData);
					}

					if (pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs().size() > 0)
					{
						List<SectorAllocations> secchartData = new ArrayList<SectorAllocations>();
						for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs())
						{
							secchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

						}

						model.addAttribute("secData", secchartData);
					}
					model.addAttribute("seriesval", pfSchRebalSrv.getRebalContainer().getStats().getDateVals());
				}

			}
		}

		return viewName;

	}

	/**
	 * ----------------------------- POST MAPPINGS -------------------------------
	 */

	@PostMapping("/cfScSch")
	public String updatePFSchema(@ModelAttribute("schmPOJO") PFSchemaRebalUI rebalPOJO, Model model)
	{
		String viewName = "ids/IDS_updSchema";
		if (rebalPOJO != null)
		{
			if (rebalPOJO.getScripsStr() != null)
			{
				if (rebalPOJO.getScripsStr().trim().length() > 0)
				{
					// Validate and Add Scrip(s)
					pfSchRebalSrv.addValidateScrips(rebalPOJO.getScripsStr());
					// Clear Chosen Scrips that are already added to Schema for Allocations
					// Maintenance
					pfSchRebalSrv.clearSelScrips();

					/*
					 * REfurbish Model
					 */
					model.addAttribute("scrips", repoBseData.findAllNseCodes());
					model.addAttribute("schmPOJO", pfSchRebalSrv.getRebalContainer());

					if (pfSchRebalSrv.getRebalContainer().getStats() != null)
					{

						if (pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs().size() > 0)
						{
							List<SectorAllocations> mCapchartData = new ArrayList<SectorAllocations>();
							for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats()
									.getMCapAllocs())
							{
								mCapchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

							}

							model.addAttribute("mCapData", mCapchartData);
						}

						if (pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs().size() > 0)
						{
							List<SectorAllocations> secchartData = new ArrayList<SectorAllocations>();
							for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats()
									.getSecAllocs())
							{
								secchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

							}

							model.addAttribute("secData", secchartData);
						}

						model.addAttribute("seriesval", pfSchRebalSrv.getRebalContainer().getStats().getDateVals());
					}

				}
			}
		}
		return viewName;
	}

	@RetainView(viewName = "ids/IDS_updSchema")
	@PostMapping(value = "/schemaVal", params = "action=validProc")
	public String refreshStaging_1(@ModelAttribute("schmPOJO") PFSchemaRebalUI rebalPOJO, Model model

	) throws Exception
	{
		String viewName = "ids/IDS_updSchema";
		if (rebalPOJO != null)
		{
			if (rebalPOJO.getScAllocMassUpdate() != null)
			{
				if (rebalPOJO.getScAllocMassUpdate().getScAllocList().size() > 0)
				{

					// Push POJO Back to Service

					pfSchRebalSrv.refurbishAllocations(rebalPOJO.getScAllocMassUpdate());
					/*
					 * REfurbish Model
					 */
					model.addAttribute("scrips", repoBseData.findAllNseCodes());
					model.addAttribute("schmPOJO", pfSchRebalSrv.getRebalContainer());
					model.addAttribute("formError", null); // Remove Error if Any on Succ. Validation
					model.addAttribute("formSucc", msgSrc.getMessage("pf.allocSucc", null, Locale.ENGLISH));

					if (pfSchRebalSrv.getRebalContainer().getStats() != null)
					{

						if (pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs().size() > 0)
						{
							List<SectorAllocations> mCapchartData = new ArrayList<SectorAllocations>();
							for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats()
									.getMCapAllocs())
							{
								mCapchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

							}

							model.addAttribute("mCapData", mCapchartData);
						}

						if (pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs().size() > 0)
						{
							List<SectorAllocations> secchartData = new ArrayList<SectorAllocations>();
							for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats()
									.getSecAllocs())
							{
								secchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

							}

							model.addAttribute("secData", secchartData);
						}
						model.addAttribute("seriesval", pfSchRebalSrv.getRebalContainer().getStats().getDateVals());
					}

				}
			}
		}

		return viewName;

	}

	@RetainView(viewName = "ids/IDS_updSchema")
	@PostMapping(value = "/schemaVal", params = "action=save")
	public String commitSchemaChanges(@ModelAttribute("schmPOJO") PFSchemaRebalUI rebalPOJO, Model model

	) throws Exception
	{
		/**
		 * TO be Created Later //String viewName = "ids/schemaDetails";
		 */

		String viewName = reRouteDBVw;
		if (rebalPOJO != null)
		{
			if (rebalPOJO.getScAllocMassUpdate() != null)
			{
				if (rebalPOJO.getScAllocMassUpdate().getScAllocList().size() > 0)
				{
					// Commit the Schema Changes
					pfSchRebalSrv.commitValidatedSchema(rebalPOJO.getScAllocMassUpdate());
					// Update the DashBoard Buffer for Schema change(s)
					pfDashBSrv.refreshSchemaPostTxn();
					pfDashBSrv.refreshContainer4SchemaChange();
				}
			}
		}

		return viewName;
	}

	@RetainView(viewName = "/ids/procMassUpdatebuyP")
	@PostMapping("/procMassUpdatebuyP")
	public String processBuyProposalCommit(@ModelAttribute("propList") IDSBuyPropMassUpdateList propList, Model model

	) throws Exception
	{
		if (propList != null)
		{
			if (propList.getBuyList() != null)
			{
				if (propList.getBuyList().size() > 0)
				{
					if (pfCoreSrv != null)
					{

						pfCoreSrv.pushandSyncPFTxn(propList.getBuyList());

						pfDashBSrv.refreshSchemaPostTxn();

						pfDashBSrv.refreshContainer4Txn();

					}
				}
			}
		}

		// Re-direct to Home
		return "redirect:/ids/launch";
	}

	@RetainView(viewName = reRouteDBVw)
	@PostMapping("/procMBTxn")
	public String processBuyProposalCommit(@ModelAttribute("MB") MBUI MB, Model model

	) throws Exception
	{
		if (MB != null)
		{
			// Process Transaction
			if (MB.getMbAmnt() != 0 && mbSrv != null)
			{
				MoneyBag mbTxn = new MoneyBag();
				if (MB.getMbAmnt() > 0) // Deposit
				{
					mbTxn.setDate(UtilDurations.getTodaysDateOnly());
					mbTxn.setAmount(MB.getMbAmnt());
					mbTxn.setType(stocktales.IDS.enums.EnumTxnType.Deposit);
					mbTxn.setRemarks("Deposit on - " + mbTxn.getDate().toString() + " for Rs. " + mbTxn.getAmount());
					mbSrv.processMBagTxn(mbTxn);

				} else // WithDraw
				{
					mbTxn.setDate(UtilDurations.getTodaysDateOnly());
					mbTxn.setAmount(MB.getMbAmnt() * -1);
					mbTxn.setType(stocktales.IDS.enums.EnumTxnType.Withdraw);
					mbTxn.setRemarks("Withdraw on - " + mbTxn.getDate().toString() + " for Rs. " + mbTxn.getAmount());
					mbSrv.processMBagTxn(mbTxn);
				}
				// Equivalent to Schema Reset
				pfDashBSrv.refreshSchemaPostTxn();
				pfDashBSrv.refreshContainer4SchemaChange();
			}
		}

		// Re-direct to Home
		return "redirect:/ids/launch";
	}

	@PostMapping(value = "/overAllocProcess", params = "action=refreshPL")
	public String realignRefreshPL(@ModelAttribute("ovAllocList") IDSOverAllocList overAllocList, Model model

	) throws Exception
	{
		String viewName = "ids/IDSOverAlloc";
		if (overAllocList != null)
		{
			if (overAllocList.getOverAllocList() != null)
			{
				pfDashBSrv.refreshOverAllocationsPL(overAllocList);
				model.addAttribute("overAllocContainer",
						this.pfDashBSrv.getPFDashBoardContainer4mSession().getOverAllocsContainer());
				model.addAttribute("ovAllocList", overAllocList);
			}
		}

		return viewName;
	}

	@PostMapping(value = "/overAllocProcess", params = "action=commit")
	public String commitOverAllocChanges(@ModelAttribute("ovAllocList") IDSOverAllocList overAllocList, Model model

	) throws Exception
	{

		if (overAllocList != null)
		{
			if (overAllocList.getOverAllocList() != null)
			{
				pfDashBSrv.commitOverAllocationsSells(overAllocList);
				/*
				 * Reset PF DB Container
				 */
				pfDashBSrv.refreshSchemaPostTxn();
			}
		}

		return reRouteDBVw;
	}

}
