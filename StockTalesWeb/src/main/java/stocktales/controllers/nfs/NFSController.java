package stocktales.controllers.nfs;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.ATH.model.pojo.ATHContainer;
import stocktales.ATH.srv.intf.ATHProcessorSrv;
import stocktales.ATH.ui.pojo.ATH_UI_Summary;
import stocktales.DataLake.model.repo.RepoATHScripPrices;
import stocktales.DataLake.model.repo.intf.IDL_IDSStats;
import stocktales.DataLake.srv.intf.DL_ATH_DataRefreshSrv;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.model.pojo.NFSContainer;
import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.ui.NFSIncAlloc;
import stocktales.NFS.model.ui.NFSMCapClass;
import stocktales.NFS.model.ui.NFSNewPF_PREUI;
import stocktales.NFS.model.ui.NFSPFExit_UISel;
import stocktales.NFS.model.ui.NFSPFSummary;
import stocktales.NFS.model.ui.NFSRunTmpList;
import stocktales.NFS.model.ui.NFSRunTmp_UISel;
import stocktales.NFS.model.ui.NFSUISCMassUpdateList;
import stocktales.NFS.model.ui.NFS_UIRebalProposalContainer;
import stocktales.NFS.model.ui.NFS_UI_Summary;
import stocktales.NFS.repo.RepoBseData;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.repo.RepoNFSTmp;
import stocktales.NFS.srv.intf.INFSPFUISrv;
import stocktales.NFS.srv.intf.INFSProcessor;
import stocktales.NFS.srv.intf.INFSRebalanceUISrv;
import stocktales.NFS.srv.intf.INFS_CashBookSrv;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.strategy.helperPOJO.SectorAllocations;

@Controller
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/nfs")
public class NFSController
{
	@Autowired
	private INFSProcessor nfsProcSrv;

	@Autowired
	private SCPricesMode scPricesMode;

	@Autowired
	private ATHProcessorSrv ATHProcSrv;

	@Autowired
	private INFSRebalanceUISrv nfsRebalSrv;

	@Autowired
	private RepoNFSPF repoNFSPF;

	@Autowired
	private RepoNFSTmp repoNFSTmp;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private RepoATHScripPrices repoATHScPrices;

	@Autowired
	private INFSPFUISrv nfsUISrv;

	@Autowired
	private INFS_CashBookSrv nfsCBSrv;

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private DL_ATH_DataRefreshSrv athDLSrv;

	@Value("${nfs.minmAmntErr}")
	private final String errMinAmnt = "";

	private NFSContainer nfsContainer;

	private ATHContainer athContainer;

	@GetMapping("/launch")
	private String showNFSHome(Model model)
	{
		String viewName = null;
		if (repoNFSPF != null)
		{
			if (repoNFSPF.count() > 0)
			{
				viewName = "redirect:/nfs/pf/list";
			} else
			{
				if (repoNFSTmp.count() > 0)
				{
					viewName = "nfs/launchRebal";
				} else
				{
					viewName = "nfs/launch";
				}
			}
		}
		return viewName;
	}

	@GetMapping("/crExisProp")
	private String creatPFExisProposal(Model model)
	{
		String viewName = null;
		if (repoNFSTmp != null)
		{

			Date lastGen = repoNFSTmp.getProposalDate();

			Format formatter = new SimpleDateFormat("dd-MM-yyyy");
			String dateStr = formatter.format(lastGen);
			model.addAttribute("genDate", dateStr);

			NFSRunTmpList scSelList = new NFSRunTmpList();
			scSelList.setScSel(nfsUISrv.getScripsForSelectionFromSavedProposal());
			if (scSelList.getScSel() != null)
			{
				if (scSelList.getScSel().size() >= (nfsConfig.getPfSize() * 1.2))
				{
					model.addAttribute("scSelList", scSelList);
					viewName = "/nfs/crpfSel";
				} else
				{
					model.addAttribute("selScrips", scSelList.getScSel().size());
					model.addAttribute("minSize", (int) (nfsConfig.getPfSize() * 1.2));
					viewName = "/nfs/noCreate";
				}
			}

		}

		return viewName;

	}

	@GetMapping("/pf/massUpd")
	private String showMassUpdateView(Model model)
	{
		if (repoNFSPF != null)
		{

			NFSUISCMassUpdateList pfMassUpd = new NFSUISCMassUpdateList();
			pfMassUpd.setCurrPf(repoNFSPF.findAll());
			model.addAttribute("scSelList", pfMassUpd);
		}

		return "/nfs/massUpdate";

	}

	@GetMapping("/athDL")
	private String resfreshDataLake(Model model)
	{
		if (athDLSrv != null && repoATHScPrices != null)
		{

			athDLSrv.refreshDataLake();

			List<IDL_IDSStats> statsHub = repoATHScPrices.getGlobalDataHubStats();
			model.addAttribute("stats", statsHub);
			model.addAttribute("numSchema", repoBseData.count());
			model.addAttribute("numDL", repoATHScPrices.getNumberofScrips());

		}

		return "/nfs/ATHDL";

	}

	@GetMapping("/crpf/tmp")
	private String createPFTmp(Model model)
	{
		// Create PF from NFSRunTmp - Use in Submit button Action
		NFSIncAlloc newAlloc = new NFSIncAlloc();
		newAlloc.setNewPF(true);
		newAlloc.setExisProp(true);

		try
		{
			NFSNewPF_PREUI pfDetails = nfsUISrv.getNewPF_PreCreateDetails();

			model.addAttribute("pfDetails", pfDetails);
			newAlloc.setMinInv(Precision.round(pfDetails.getMinInv(), 1));

			if (newAlloc.isNewPF())
			{
				newAlloc.setIncInvestment(Precision.round(pfDetails.getMinInv(), 0));
			} else
			{

				double amount = nfsCBSrv.getDeployableBalance();
				newAlloc.setIncInvestment(Precision.round(amount, 1));
			}

			model.addAttribute("alloc", newAlloc);
		} catch (Exception e)
		{
			model.addAttribute("formError", e.getMessage());
		}

		return "/nfs/incInv";
	}

	@GetMapping("/rebal_incI")
	private String showRebal_IncInv(Model model)
	{

		// Create PF from NFSRunTmp - Use in Submit button Action
		NFSIncAlloc newAlloc = new NFSIncAlloc();
		newAlloc.setNewPF(false);
		newAlloc.setExisProp(true);

		try
		{
			NFSNewPF_PREUI pfDetails = nfsUISrv.getNewPF_PreCreateDetails();

			model.addAttribute("pfDetails", pfDetails);
			newAlloc.setMinInv(Precision.round(pfDetails.getMinInv(), 1));

			double amount = nfsCBSrv.getDeployableBalance();
			newAlloc.setIncInvestment(Precision.round(amount, 1));

			model.addAttribute("alloc", newAlloc);
		} catch (Exception e)
		{
			model.addAttribute("formError", e.getMessage());
		}

		return "/nfs/incInv";
	}

	@GetMapping("/start")
	private String startNFSProcess()
	{
		String viewName = "success";
		NFSContainer nfsContainer = null;

		if (scPricesMode != null)
		{
			if (scPricesMode.getScpricesDBMode() == 1)
			{
				if (ATHProcSrv != null)
				{
					long start = System.currentTimeMillis();
					long elapsedMins = 0;
					try
					{
						this.athContainer = ATHProcSrv.generateProposal(true).get();
						if (this.athContainer != null)
						{
							if (this.athContainer.getProposals().size() > 0)
							{
								elapsedMins = (System.currentTimeMillis() - start) / 60000;
								this.athContainer.getAthStats().setElapsedMins(elapsedMins);
								viewName = "redirect:/nfs/showStats";
							} else
							{
								if (repoNFSPF.count() > 0)
								{
									viewName = "redirect:/nfs/pf/list";
								} else
								{
									viewName = "nfs/NoScrips";
								}
							}
						}
					} catch (Exception e)
					{
						// Exception Centrally Handled at the service level - Autodirected to Global
						// Exception Handler
						e.printStackTrace();
					}

				}

			} else
			{
				if (nfsProcSrv != null)
				{

					long start = System.currentTimeMillis();
					long elapsedMins = 0;
					try
					{
						this.nfsContainer = nfsProcSrv.generateProposal(true).get();
					} catch (Exception e)
					{
						// Exception Centrally Handled at the service level - Autodirected to Global
						// Exception Handler
						e.printStackTrace();
					}
					if (this.nfsContainer != null)
					{
						if (this.nfsContainer.getBaseDataPool().size() > 0)
						{

							elapsedMins = (System.currentTimeMillis() - start) / 60000;
							this.nfsContainer.getNfsStats().setElapsedMins(elapsedMins);
							viewName = "redirect:/nfs/showStats";
						}
					}
				}
			}
		}

		return viewName;
	}

	@GetMapping("/showStats")
	private String showNFSLaunch(Model model)
	{
		String viewName = null;
		if (scPricesMode != null)
		{
			if (scPricesMode.getScpricesDBMode() == 1)
			{
				viewName = "ath/nfsStats";

				model.addAttribute("stats", athContainer.getAthStats());

				List<ATH_UI_Summary> statsList = new ArrayList<ATH_UI_Summary>();

				ATH_UI_Summary listedScrips = new ATH_UI_Summary("Total Listed Scrips",
						athContainer.getAthStats().getTotalScrips());
				statsList.add(listedScrips);

				ATH_UI_Summary availableScrips = new ATH_UI_Summary("Data Available",
						athContainer.getAthStats().getTotalScrips() - athContainer.getAthStats().getDataError());
				statsList.add(availableScrips);

				ATH_UI_Summary mCapScrips = new ATH_UI_Summary("MCap > 1000 Cr. & Trade Days Sieved Scrips",
						athContainer.getAthStats().getMcapFltRemain());
				statsList.add(mCapScrips);

				ATH_UI_Summary momScrips = new ATH_UI_Summary("Momentum Sieved Scrips",
						athContainer.getAthStats().getMomentumRemain());
				statsList.add(momScrips);

				ATH_UI_Summary finalScrips = new ATH_UI_Summary("Finally Sieved & Ranked Scrips",
						athContainer.getAthStats().getNumFinalScrips());
				statsList.add(finalScrips);

				model.addAttribute("statsList", statsList);

				/*
				 * DO not allow PF creation if Number of Scrips selected is less that minimum
				 * threshold Scrips
				 */
				if (athContainer.getAthStats().getNumFinalScrips() < (nfsConfig.getPfSize() * 1.2))
				{
					model.addAttribute("noPF", true);

				}

			} else
			{

				if (nfsProcSrv != null && nfsContainer.getNfsStats() != null)
				{
					viewName = "nfs/nfsStats";

					model.addAttribute("stats", nfsContainer.getNfsStats());

					List<NFS_UI_Summary> statsList = new ArrayList<NFS_UI_Summary>();

					NFS_UI_Summary listedScrips = new NFS_UI_Summary("Total Listed Scrips", 0,
							nfsContainer.getNfsStats().getNumScripsTotal());
					statsList.add(listedScrips);

					NFS_UI_Summary avScrips = new NFS_UI_Summary("Data Available ",
							nfsContainer.getNfsStats().getNumScripsTotal()
									- nfsContainer.getNfsStats().getNumScripsDataAvail(),
							nfsContainer.getNfsStats().getNumScripsDataAvail());
					statsList.add(avScrips);

					NFS_UI_Summary mcapFltr = new NFS_UI_Summary("Market Cap Filter ",
							nfsContainer.getNfsStats().getNumMcapFltOut(),
							nfsContainer.getNfsStats().getNumScripsDataAvail()
									- nfsContainer.getNfsStats().getNumMcapFltOut());
					statsList.add(mcapFltr);

					NFS_UI_Summary actvFltr = new NFS_UI_Summary("Active Trading Days Filter ",
							nfsContainer.getNfsStats().getNumDurationFltOut(),
							nfsContainer.getNfsStats().getNumScripsDataAvail()
									- nfsContainer.getNfsStats().getNumMcapFltOut()
									- nfsContainer.getNfsStats().getNumDurationFltOut());
					statsList.add(actvFltr);

					NFS_UI_Summary consFltr = new NFS_UI_Summary("Returns Consistency Filter ",
							nfsContainer.getNfsStats().getNumConsistencyFltOut(),
							nfsContainer.getNfsStats().getNumScripsDataAvail()
									- nfsContainer.getNfsStats().getNumMcapFltOut()
									- nfsContainer.getNfsStats().getNumDurationFltOut()
									- nfsContainer.getNfsStats().getNumConsistencyFltOut());
					statsList.add(consFltr);

					NFS_UI_Summary momFltr = new NFS_UI_Summary("Price Momentum Filter ",
							nfsContainer.getNfsStats().getNumSMACMP_Trends_FltOut(),
							consFltr.getNumscrips() - nfsContainer.getNfsStats().getNumSMACMP_Trends_FltOut());
					statsList.add(momFltr);

					NFS_UI_Summary manPFltr = new NFS_UI_Summary("Price Manipulation Filter ",
							nfsContainer.getNfsStats().getPriceManipulationFltOut(),
							momFltr.getNumscrips() - nfsContainer.getNfsStats().getPriceManipulationFltOut());
					statsList.add(manPFltr);

					if (nfsContainer.getNfsStats().getNumMcapFltOut() > 0)
					{
						NFS_UI_Summary cmpFltr = new NFS_UI_Summary("T2T Category & Max PF Lot Size  Filter ",
								manPFltr.getNumscrips() - nfsContainer.getNfsStats().getNumFinalScrips(),
								nfsContainer.getNfsStats().getNumFinalScrips());
						statsList.add(cmpFltr);
					}
					model.addAttribute("statsList", statsList);

					/*
					 * DO not allow PF creation if Number of Scrips selected is less that minimum
					 * threshold Scrips
					 */
					if (nfsContainer.getNfsStats().getNumFinalScrips() < (nfsConfig.getPfSize() * 1.2))
					{
						model.addAttribute("noPF", true);

					}

				}
			}

			// If PF already Exists
			if (repoNFSPF.count() > 0)
			{
				// Do not prompt to create a new one
				model.addAttribute("noPF", true);
				if (repoNFSTmp.count() > 0)
				{
					model.addAttribute("rebal", true);
				}
			}

		}
		return viewName;
	}

	@GetMapping("/pf/list")
	private String showPfOvw(Model model)
	{

		if (nfsUISrv != null)
		{

			try
			{
				NFSPFSummary pfSummary = nfsUISrv.getPfSummary();
				if (pfSummary != null)
				{
					model.addAttribute("pfSummary", pfSummary);
					if (pfSummary.getMcapClass() != null)
					{
						if (pfSummary.getMcapClass().size() > 0)
						{
							List<SectorAllocations> chartData = new ArrayList<SectorAllocations>();
							for (NFSMCapClass mcapAlloc : pfSummary.getMcapClass())
							{
								chartData.add(
										new SectorAllocations(mcapAlloc.getMcapCatgName(), mcapAlloc.getPercentage()));

							}

							model.addAttribute("chartData", chartData);
						}
					}

					/**
					 * UPDATE daily Prices by or after 3:30 P.M
					 */
					Calendar C = new GregorianCalendar();
					int hour = C.get(Calendar.HOUR_OF_DAY);
					int minute = C.get(Calendar.MINUTE);

					if (hour >= 15 && minute > 30)
					{
						// athDLSrv.performDeltaLoad();
					}

					// Check Any Exits in PF

					// model.addAttribute("numExits", nfsProcSrv.getNumExitScrips());
				}

			} catch (Exception e)
			{
				// Exception Centrally Handled at the service level - Autodirected to Global
				// Exception Handler
				e.printStackTrace();
			}

		}
		return "nfs/pfOvw";
	}

	@GetMapping("/exitometer")
	private String showExitometer(Model model)
	{
		try
		{
			NFSIncAlloc newAlloc = new NFSIncAlloc();
			newAlloc.setNewPF(false);
			newAlloc.setExisProp(true);

			double amount = nfsCBSrv.getDeployableBalance();
			newAlloc.setIncInvestment(Precision.round(amount, 1));

			NFSPFExitSS nfsExitSS = nfsProcSrv.getPFExitSnapshot();
			if (nfsExitSS != null)
			{
				model.addAttribute("nfsExitSS", nfsExitSS);
				model.addAttribute("alloc", newAlloc);

			}
		} catch (Exception e)
		{
			// Exception Centrally Handled at the service level - Autodirected to Global
			// Exception Handler
			e.printStackTrace();
		}
		return "nfs/exitometer";
	}

	/*
	 * --------------------------------------------------------------------
	 * ----------------------- POST MAPPINGS--------------------------------
	 * --------------------------------------------------------------------
	 */

	@PostMapping(value = "/crpf_staging_1", params = "action=refresh")
	public String CreatePFStaging_1(@ModelAttribute("scSelList") NFSRunTmpList runTmpList, Model model

	)
	{

		if (runTmpList != null)
		{
			if (runTmpList.getScSel().size() > 0)
			{
				// Call NFS UI Processor Create PF Method with User Selected Scrips
				List<NFSRunTmp_UISel> inclScrips = runTmpList.getScSel().stream().filter(x -> x.isIsincluded() == true)
						.collect(Collectors.toList());
				if (inclScrips.size() > 0)
				{
					nfsUISrv.saveSelScripsinBuffer(inclScrips);
				}
			}
		}
		// redirect to Incremental Investment
		return "redirect:/nfs/crpf/tmp";
	}

	@PostMapping(value = "/pf/save")
	public String savePF(@ModelAttribute("alloc") NFSIncAlloc newAlloc, Model model

	)
	{
		if (newAlloc != null)
		{
			if (newAlloc.isNewPF())
			{
				// Amount Invested Less than Minimum needed to create the PF
				if (newAlloc.getIncInvestment() < newAlloc.getMinInv())
				{
					model.addAttribute("formError", msgSrc.getMessage(errMinAmnt, new Object[]
					{ newAlloc.getIncInvestment(), newAlloc.getMinInv() }, Locale.ENGLISH));

					return "redirect:/nfs/crpf/tmp";
				} else
				{
					// Persist the Portfolio
					try
					{
						nfsUISrv.createPF4mExistingProposalSelection(newAlloc.getIncInvestment());
					} catch (Exception e)
					{
						model.addAttribute("formError", e.getMessage());

						return "/nfs/incInv";
					}
				}
			}

			else
			{

				if (newAlloc != null)
				{
					if (!newAlloc.isNewPF() && newAlloc.isExisProp())
					{
						try
						{
							// Prepare Entries and Exits if Any or Otherwise do Incremental Investments
							NFS_UIRebalProposalContainer rebalContainer = nfsProcSrv
									.rebalancePF_UI(newAlloc.getIncInvestment());

							model.addAttribute("rebalContainer", rebalContainer);
							return "/nfs/rebalSel"; // Re-balance- Incrementally Invest

						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
		return "redirect:/nfs/pf/list"; // Happy Path
	}

	@PostMapping(value = "/pf/rebal")
	public String showRebalProposals(@ModelAttribute("alloc") NFSIncAlloc newAlloc, Model model

	)
	{
		if (newAlloc != null)
		{
			if (!newAlloc.isNewPF() && newAlloc.isExisProp())
			{
				try
				{
					// Prepare Entries and Exits if Any or Otherwise do Incremental Investments
					NFS_UIRebalProposalContainer rebalContainer = nfsProcSrv
							.rebalancePF_UI(newAlloc.getIncInvestment());
					model.addAttribute("rebalContainer", rebalContainer);

				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return "/nfs/rebalSel";
	}

	@PostMapping(value = "/save_rebal")
	public String processRebalance(@ModelAttribute("rebalContainer") NFS_UIRebalProposalContainer rebalContainer,
			Model model

	)
	{
		if (rebalContainer != null && nfsRebalSrv != null)
		{
			// Get Re-balance Amount - Incremental if Any
			double incInv = rebalContainer.getInvAmnt();

			// Get Entries if any
			List<String> entries = new ArrayList<String>();
			if (rebalContainer.getProposals() != null)
			{
				if (rebalContainer.getProposals().getScSel().size() > 0)
				{
					List<NFSRunTmp_UISel> entriesQ = rebalContainer.getProposals().getScSel().stream()
							.filter(x -> x.isIsincluded() == true).collect(Collectors.toList());
					if (entriesQ.size() > 0)
					{

						entriesQ.stream().filter(x -> entries.add(x.getSccode())).collect(Collectors.toList());
					}
				}
			}

			// Get Exits if any
			List<String> exits = new ArrayList<String>();
			if (rebalContainer.getExitsList() != null)
			{
				if (rebalContainer.getExitsList().getScExit().size() > 0)
				{
					List<NFSPFExit_UISel> exitsQ = rebalContainer.getExitsList().getScExit().stream()
							.filter(x -> x.isIsincluded() == true).collect(Collectors.toList());
					if (exitsQ.size() > 0)
					{
						exitsQ.stream().filter(x -> exits.add(x.getScCode())).collect(Collectors.toList());
					}

				}
			}

			try
			{
				nfsRebalSrv.processRebalance(incInv, exits, entries);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "redirect:/nfs/pf/list";
	}

	@PostMapping(value = "/procMassUpdate")
	public String processMassUpdate(@ModelAttribute("scSelList") NFSUISCMassUpdateList massUpdPFList, Model model

	)
	{
		if (massUpdPFList != null && nfsProcSrv != null)
		{
			if (massUpdPFList.getCurrPf() != null)
			{
				if (massUpdPFList.getCurrPf().size() > 0)
				{
					nfsProcSrv.massUpdatePF(massUpdPFList.getCurrPf());
				}
			}
		}

		return "redirect:/nfs/pf/list";
	}

}
