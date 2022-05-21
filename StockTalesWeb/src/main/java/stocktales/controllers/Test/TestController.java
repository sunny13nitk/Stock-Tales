package stocktales.controllers.Test;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.ATH.model.pojo.ATHContainer;
import stocktales.ATH.srv.intf.ATHProcessorSrv;
import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;
import stocktales.BackTesting.IDS.pojo.BT_EP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_IP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_ScripAllocs;
import stocktales.BackTesting.IDS.srv.intf.IBT_IDS_Srv;
import stocktales.DataLake.model.entity.DL_ScripPrice;
import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.DataLake.model.pojo.UploadStats;
import stocktales.DataLake.model.repo.RepoATHScripPrices;
import stocktales.DataLake.model.repo.RepoScripPrices;
import stocktales.DataLake.model.repo.intf.IDL_IDSStats;
import stocktales.DataLake.srv.intf.DL_ATH_DataRefreshSrv;
import stocktales.DataLake.srv.intf.DL_HistoricalPricesSrv;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumSchemaDepAmntsUpdateMode;
import stocktales.IDS.enums.EnumTxnType;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.model.pf.entity.MoneyBag;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.DateAmount;
import stocktales.IDS.pojo.IDS_SCAlloc;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.IDS_SC_BonusIP;
import stocktales.IDS.pojo.IDS_SC_PL;
import stocktales.IDS.pojo.IDS_SC_PL_Items;
import stocktales.IDS.pojo.IDS_SC_SplitIP;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.pojo.XIRRContainer;
import stocktales.IDS.srv.impl.IDS_CorePFSrv;
import stocktales.IDS.srv.intf.IDS_DeploymentAmntSrv;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.NFS.enums.EnumNFSTxnType;
import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.pojo.NFSCB_IP;
import stocktales.NFS.model.pojo.NFSConsistency;
import stocktales.NFS.model.pojo.NFSContainer;
import stocktales.NFS.model.pojo.NFSPFExitSMA;
import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.pojo.NFSPriceManipulation;
import stocktales.NFS.model.pojo.NFSPriceManipulationItems;
import stocktales.NFS.model.pojo.NFSScores;
import stocktales.NFS.model.pojo.NFS_DD4ListScrips;
import stocktales.NFS.model.pojo.NFS_DD4ListScripsI;
import stocktales.NFS.model.pojo.ScripPPU;
import stocktales.NFS.model.pojo.ScripPPUUnitsRank;
import stocktales.NFS.model.ui.NFSPFSummary;
import stocktales.NFS.repo.RepoBseData;
import stocktales.NFS.repo.RepoNFSPF;
import stocktales.NFS.srv.intf.INFSPFUISrv;
import stocktales.NFS.srv.intf.INFSProcessor;
import stocktales.NFS.srv.intf.INFS_CashBookSrv;
import stocktales.NFS.srv.intf.INFS_DD_Srv;
import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.interfaces.ISrv_FCFSCore;
import stocktales.basket.allocations.autoAllocation.pojos.FCFScore;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyAllocShort;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos.StgyRebalance;
import stocktales.basket.allocations.autoAllocation.strategy.repo.RepoStgyAllocations;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCWtPESrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.scWtPE;
import stocktales.basket.allocations.config.pojos.IntvPriceCAGR;
import stocktales.basket.allocations.config.pojos.ScripCMPHistReturns;
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.helperPoJo.XIRRItems;
import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.helperPoJo.YearsRollOverResults;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.cagrEval.intf.IRollOverYrs;
import stocktales.controllers.Test.entity.MultiTest;
import stocktales.controllers.Test.pojo.TestMulti;
import stocktales.controllers.Test.repo.RepoMultiTest;
import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.PlaceHolderLong;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.ScJSummary;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.durations.UtilDurations;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.healthcheck.repo.Repo_CfgHC;
import stocktales.healthcheck.repo.intf.IRepoCfgSrvStext;
import stocktales.helperPOJO.NameValDouble;
import stocktales.helperPOJO.ScValFormPOJO;
import stocktales.historicalPrices.enums.EnumInterval;
import stocktales.historicalPrices.pojo.HistoricalQuote;
import stocktales.historicalPrices.pojo.StgyRelValuation;
import stocktales.historicalPrices.pojo.StockHistory;
import stocktales.historicalPrices.srv.intf.ITimeSeriesStgyValuationSrv;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.predicates.manager.PredicateManager;
import stocktales.repository.SC10YearRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;
import stocktales.strategy.helperPOJO.NiftyStgyCAGR;
import stocktales.strategy.helperPOJO.SectorAllocations;
import stocktales.strategy.helperPOJO.StgyStatsSummary;
import stocktales.strategy.intf.IStrategyStatsSrv;
import stocktales.topgun.model.pojo.IntervalStats;
import stocktales.topgun.model.pojo.TopGunContainer;
import stocktales.topgun.srv.intf.ITopGunSrv;
import stocktales.usersPF.repo.RepoHoldings;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

@Controller
@RequestMapping("/test")
public class TestController
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;

	@Autowired
	private ITopGunSrv topGunSrv;

	@Autowired
	private ISCExistsDB_Srv scExSrv;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private INFSProcessor nfsProcessor;

	@Autowired
	private EDRCFacade edrcFacSrv;

	@Autowired
	private SC10YearRepository sc10Yrepo;

	@Autowired
	private SCWtPESrv scWtPESrv;

	@Autowired
	private PredicateManager predMgr;

	@Autowired
	private SCValuationSrv scValSrv;

	@Autowired
	private ISrv_FCFSCore cfScoreSrv;

	@Autowired
	private IStgyRebalanceSrv stgyRblSrv;

	@Autowired
	private RepoMultiTest repoMultiTest;

	@Autowired
	private RepoScJournal repoSCJ;

	@Autowired
	private ISCDataContainerSrv scContSrv;

	@Autowired
	private IRollOverYrs roySrv;

	@Autowired
	private ICAGRCalcSrv cagrCalcSrv;

	@Autowired
	private IHC_Srv hcSrv;

	@Autowired
	private Repo_CfgHC repoHCCfg;

	@Autowired
	private RepoHoldings repoHoldings;

	@Autowired
	private RepoStgyAllocations repoStgyAlloc;

	@Autowired
	private IStrategyStatsSrv stgyStatsSrv;

	@Autowired
	private IStockSnapshotSrv sSHSrv;

	@Autowired
	private INFSPFUISrv nfsUiSrv;

	@Autowired
	private ITimeSeriesStgyValuationSrv timeSeriesSrv;

	@Autowired
	private IDS_VPSrv vpSrv;

	@Autowired
	private IDS_CorePFSrv corePFSrv;

	@Autowired
	private IDS_MoneyBagSrv mbSrv;

	@Autowired
	private IDS_DeploymentAmntSrv depAmntSrv;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private IBT_IDS_Srv bt_IdsSrv;

	@Autowired
	private INFS_DD_Srv nfsDDSrv;

	@Autowired
	private RepoNFSPF repoNFSPF;

	@Autowired
	private INFS_CashBookSrv nfsCBSrv;

	@Autowired
	private RepoScripPrices repoScPrices;

	@Autowired
	@Qualifier("DL_HistoricalPricesSrv_IDS")
	private DL_HistoricalPricesSrv hpDBSrv;

	@Autowired
	private ATHProcessorSrv ATHSrv;

	@Autowired
	private DL_ATH_DataRefreshSrv athDLSrv;

	@Autowired
	private RepoATHScripPrices repoATHDL;

	@GetMapping("/edrcSrv/{scCode}")
	public String testEDRCSrv(@PathVariable String scCode

	)
	{
		ScripEDRCScore edrcScore = edrcSrv.getEDRCforScrip(scCode);
		if (edrcScore != null)
		{
			System.out.println(edrcScore.getScCode());
			System.out.println("Earning Scrore : " + edrcScore.getEarningsDivScore().getResValue());
			System.out.println("ROCE Scrore : " + edrcScore.getReturnRatiosScore().getNettValue());
			if (edrcScore.getCashflowsScore() != null)
			{
				System.out.println("Cash Flow Score : " + edrcScore.getCashflowsScore().getNettValue());
			}
			System.out.println("Nett Score : " + edrcScore.getEdrcScore());
		}

		return "success";
	}

	@GetMapping("/sectors")
	public String testSectorsList()
	{
		try
		{
			List<String> sectors = scExSrv.getAllSectors();
			for (String sector : sectors)
			{
				System.out.println(sector);
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	@GetMapping("/edrcFacade")
	public String testEDRCFacade()
	{
		try
		{
			List<SC_EDRC_Summary> edrcList = edrcFacSrv.getEDRCforSCripsList(scExSrv.getAllScripNames());
			for (SC_EDRC_Summary edrc_item : edrcList)
			{
				System.out.println(edrc_item.getScCode() + "|" + edrc_item.getAvWtED() + "|" + edrc_item.getAvWtRR()
						+ "|" + edrc_item.getAvWtCF() + "|" + edrc_item.getEDRC());
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/edrcFacade/{scCode}")
	public String testEDRCFacadeforScrip(@PathVariable String scCode)
	{

		SC_EDRC_Summary edrc_item = edrcFacSrv.getEDRCforSCrip(scCode);
		if (edrc_item != null)
		{
			System.out.println(edrc_item.getScCode() + "|" + edrc_item.getAvWtED() + "|" + edrc_item.getAvWtRR() + "|"
					+ edrc_item.getAvWtCF() + "|" + edrc_item.getEDRC());
		}

		return "success";
	}

	@GetMapping("/repo_10Yr/{scCode}")
	public String test10YRepo(@PathVariable String scCode)
	{
		if (scCode != null)
		{
			Optional<EN_SC_10YData> ent10Y = sc10Yrepo.findBySCCode(scCode);
			if (ent10Y.isPresent())
			{
				System.out.println(ent10Y.get().getSCCode() + " : " + ent10Y.get().getValR());
			}
		}
		return "success";
	}

	@GetMapping("/wtPE/{scCode}")
	public String testwtPE(@PathVariable String scCode)
	{
		if (scCode != null)
		{
			scWtPE scripWtPE = scWtPESrv.getWeightedPEforScrip(scCode);
			System.out.println(scripWtPE.getScCode() + " : " + scripWtPE.getWtPE());
		}
		return "success";
	}

	@GetMapping("/scVal/{scCode}")
	public String testscValuation(@PathVariable String scCode, Model model)
	{
		if (scCode != null)
		{
			ScValFormPOJO scvalPOJO = new ScValFormPOJO(scCode, .7, 0);
			model.addAttribute("scValPOJO", scvalPOJO);
		}
		return "test/forms/scVal";
	}

	@GetMapping("/predicates")
	public String testpredicates(

	)
	{
		if (predMgr != null)
		{
			List<String> beanNames = predMgr.getPredicateBeanNames();
			for (String string : beanNames)
			{
				System.out.println(string);
			}
		}

		return "success";
	}

	@GetMapping("/predicates/{predName}")
	public String testpredicatesbyName(@PathVariable String predName

	)
	{
		if (predMgr != null)
		{

			System.out.println("Bean Name : " + predMgr.getActivePredicateBeanName(predName));

		}

		return "success";
	}

	@GetMapping("/cfyields/{scCode}")
	public String testcfYieldsbyScrip(@PathVariable String scCode

	)
	{
		FCFScore fcfScore = cfScoreSrv.getFCFScorebyScrip(scCode);
		if (fcfScore != null)
		{
			System.out.println("Scrip Code:  " + fcfScore.getScCode() + " | FCF Yield | " + fcfScore.getFcfYield()
					+ " | CFO Yield | " + fcfScore.getCfoYield());

		}
		return "success";
	}

	@GetMapping("/cfyields")
	public String testcfYields(

	)
	{

		List<String> scrips;
		try
		{
			scrips = scExSrv.getAllScripNames();

			for (String scCode : scrips)
			{
				FCFScore fcfScore = cfScoreSrv.getFCFScorebyScrip(scCode);
				if (fcfScore != null)
				{
					System.out.println("Scrip Code:  " + fcfScore.getScCode() + " | FCF Yield | "
							+ fcfScore.getFcfYield() + " | CFO Yield | " + fcfScore.getCfoYield());

				}
			}
		} catch (EX_General e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/stgyRebal/{stgId}")
	public String testStgyRebalancing(@PathVariable("stgId") String stgId, Model model)
	{

		int stgyId = new Integer(stgId);
		if (stgyId > 0)
		{
			StgyRebalance rblPoJo = stgyRblSrv.triggerReBalancingforStgy(stgyId);
			if (rblPoJo != null)
			{
				model.addAttribute("rblPOJO", rblPoJo);
			}
		}
		return "success";
	}

	@GetMapping("/multi")
	public String showTestMulti(Model model)
	{
		model.addAttribute("multipojo", new TestMulti());
		return "test/testMulti";
	}

	@PostMapping("/multi")
	public String handlePostMulti(@ModelAttribute("multipojo") TestMulti multiPojo, Model model)
	{
		if (multiPojo != null)
		{
			System.out.println(multiPojo.getTag());
			System.out.println(multiPojo.getOthertag());
			System.out.println(multiPojo.getCatg());

			// --Persist in DB
			MultiTest multitest = new MultiTest();
			multitest.setCatg(multiPojo.getCatg());

			if (multiPojo.getOthertag() != null)
			{
				multitest.setTag(multiPojo.getTag() + ',' + multiPojo.getOthertag());
			} else
			{
				multitest.setTag(multiPojo.getTag());
			}
			repoMultiTest.save(multitest);
		}
		return "success";
	}

	@GetMapping("/multi/tag/{tagtext}")
	public String scanTestMulti(@PathVariable("tagtext") String tagtext, Model model)
	{
		if (tagtext != null)
		{
			if (tagtext.trim().length() > 0)
			{
				List<MultiTest> list = repoMultiTest.findAllByTagContainingIgnoreCase(tagtext);
				for (MultiTest multiTest : list)
				{
					System.out.println(multiTest.getTag());

					System.out.println(multiTest.getCatg());
				}
			}
		}

		return "success";
	}

	@GetMapping("/dq/{scCode}")
	public String testdqbyScrip(@PathVariable String scCode

	)
	{

		if (repoSCJ != null)
		{
			List<Object[]> vals = repoSCJ.findByAsArray("BAJFINANCE");
			if (vals != null)
			{
				if (vals.size() > 0)
				{

					NumandLastEntry snippet = new NumandLastEntry();
					snippet.setLastEntryDate((Date) vals.get(0)[0]);
					snippet.setNumEntries((Long) vals.get(0)[1]);

					System.out.println("Total Entries - " + snippet.getNumEntries() + " & Last Entry On -  "
							+ snippet.getLastEntryDate());
				}
			}
		}
		return "success";
	}

	@GetMapping("/dqGCatg/{scCode}")
	public String testdqGbyScrip(@PathVariable String scCode

	)
	{

		if (repoSCJ != null)
		{
			List<PlaceHolderLong> vals = repoSCJ.countEntriesByCategory("BAJFINANCE");
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					for (PlaceHolderLong placeHolderLong : vals)
					{
						System.out.println(
								placeHolderLong.getPlaceholder() + " : Entries - " + placeHolderLong.getNumEntries());
						;
					}
				}
			}
		}
		return "success";
	}

	@GetMapping("/dqGTag/{scCode}")
	public String testdqGTbyScrip(@PathVariable String scCode

	)
	{

		if (repoSCJ != null)
		{

		}
		return "success";
	}

	@GetMapping("/dq_Summary/{scCode}")
	public String testdqSummarybyScrip(@PathVariable String scCode

	)
	{

		List<ScJSummary> vals = repoSCJ.getSummaryByScCode("BAJFINANCE");
		if (vals != null)
		{
			if (vals.size() > 0)
			{
				for (ScJSummary summ : vals)
				{
					System.out.println("Categories:" + summ.getNumCatgs());
					System.out.println("Sources:" + summ.getNumSources());
					System.out.println("Effects:" + summ.getNumEffects());
					System.out.println("Tags:" + summ.getNumTags());

				}
			}
		}

		return "success";

	}

	@GetMapping("/scload/{scCode}")
	public String loadSCripData(@PathVariable String scCode

	)
	{
		if (scCode != null)
		{
			try
			{
				scContSrv.load(scCode);
				scDataContainer scDC = scContSrv.getScDC();
				if (scDC != null)
				{
					if (scDC.getBalSheet_L() != null)
					{
						Optional<EN_SC_BalSheet> balMin = scDC.getBalSheet_L().stream()
								.min(Comparator.comparing(EN_SC_BalSheet::getYear));
						if (balMin.isPresent())
						{
							int ymin = balMin.get().getYear();
							System.out.println("Min Year : " + ymin);
						}

					}
					System.out.println("Scrip loaded!");
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/scripsBysector/{sector}")
	public String listScripsBySector(@PathVariable String sector

	)
	{
		if (sector != null)
		{
			try
			{
				List<String> scrips = scExSrv.getAllScripNamesforSector(sector);

				for (String string : scrips)
				{
					System.out.println(string);
				}
			} catch (EX_General e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "success";

	}

	@GetMapping("/ROY")
	public String showRollOverYears(

	)
	{
		if (roySrv != null)
		{
			YearsRollOverResults royRes = roySrv.generateRollOverYrs(2010, 3, 10);
			if (royRes != null)
			{
				System.out.println("Intervals");
				for (YearsFromTo yent : royRes.getRollOverYrs())
				{
					System.out.println(yent.getFrom() + " | " + yent.getTo());

				}

				System.out.println("End to End Points");
				System.out.println(royRes.getE2eYrs().getFrom() + "|" + royRes.getE2eYrs().getTo());
			}
		}

		return "success";
	}

	@GetMapping("/cagrsim")
	public String showCAGRCalc()
	{
		List<String> scrips = new ArrayList<String>();
		scrips.add("BAJFINANCE");
		/*
		 * scrips.add("ALKYLAMINE"); scrips.add("ABBOTTINDIA"); scrips.add("BRITANNIA");
		 */
		/*
		 * scrips.add("LTI"); scrips.add("LTTS");
		 */

		if (cagrCalcSrv != null)
		{
			try
			{
				cagrCalcSrv.Initialize(scrips, false);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2010, 3, 10, true));
				if (cagrCalcSrv.getCagrResults() != null)
				{
					for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
					{
						System.out.println("----- Duration Details ------");
						System.out.println(
								cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
						System.out.println(cagrResult.getDurationH().getDurationType().toString());

						System.out.println("---------- Constituent Details ---------");
						for (XIRRItems xirrItem : cagrResult.getItems())
						{
							System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
									+ xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
						}

						System.out.println("---------- Summary ---------");

						System.out
								.println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
						System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
						System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/cagrsim/{stgyId}")
	public String showCAGRCalcforStrategy(@PathVariable String stgyId)
	{

		if (cagrCalcSrv != null)
		{
			try
			{
				cagrCalcSrv.Initialize(new Integer(stgyId), false);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2010, 5, 10, false));
				if (cagrCalcSrv.getCagrResults() != null)
				{
					for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
					{
						System.out.println("--------------------------------");
						System.out.println("----- Duration Details ------");
						System.out.println("--------------------------------");
						System.out.println(
								cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
						System.out.println(cagrResult.getDurationH().getDurationType().toString());

						System.out.println("---------- Constituent Details ---------");
						for (XIRRItems xirrItem : cagrResult.getItems())
						{
							System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
									+ xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
						}

						System.out.println("---------- Summary ---------");

						System.out
								.println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
						System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
						System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
						System.out.println("----------------------------------------------------------------");
						System.out.println("NIFTY CAGR ------->>   " + cagrResult.getSummary().getNiftyCAGR());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/topNEDRC/{numScrips}")
	public String showTopNEDRC(@PathVariable String numScrips)
	{

		if (edrcFacSrv != null)
		{
			List<NameValDouble> topN = edrcFacSrv.getTopNED(new Integer(numScrips));
			List<String> Scrips = new ArrayList<String>();
			for (NameValDouble nameVal : topN)
			{
				Scrips.add(nameVal.getName());
				System.out.println(nameVal.getName() + " - ED Score: " + nameVal.getValue());
			}

			try
			{
				cagrCalcSrv.Initialize(Scrips, true);
				cagrCalcSrv.calculateCAGR(new RollOverDurationsParam(2015, 3, 5, true));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (cagrCalcSrv.getCagrResults() != null)
			{
				for (CAGRResult cagrResult : cagrCalcSrv.getCagrResults())
				{
					System.out.println("----- Duration Details ------");
					System.out.println(cagrResult.getDurationH().getFrom() + " : " + cagrResult.getDurationH().getTo());
					System.out.println(cagrResult.getDurationH().getDurationType().toString());

					System.out.println("---------- Constituent Details ---------");
					for (XIRRItems xirrItem : cagrResult.getItems())
					{
						System.out.println(xirrItem.getScCode() + " | " + xirrItem.getAllocation() + " | "
								+ xirrItem.getCAGR() + " | " + xirrItem.getWtCAGR());
					}

					System.out.println("---------- Summary ---------");

					System.out.println("Zero CAGR Allocation Sum : " + cagrResult.getSummary().getSumzeroCAGRAlloc());
					System.out.println("Boost Factor : " + cagrResult.getSummary().getBoostFactor());
					System.out.println("NETT. CAGR ------->>   " + cagrResult.getSummary().getNettCAGR());
				}
			}

		}
		return "success";
	}

	@GetMapping("/shc/{scCode}")
	public String performSCHealthCheck(@PathVariable String scCode)
	{

		if (hcSrv != null)
		{
			try
			{
				hcSrv.Initialize(scCode);
				hcSrv.processScripHealthCheck();
				List<HCComboResult> results = hcSrv.getResults();
				if (results != null)
				{

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/hcSrvList")
	public String getUnqHCSrv()
	{

		List<IRepoCfgSrvStext> srvList = null;

		srvList = repoHCCfg.getServicesListUnique();
		if (srvList != null)
		{
			for (IRepoCfgSrvStext srv : srvList)
			{
				System.out.println(
						srv.getSrvname() + "  :  " + srv.getStext() + "  - Financials " + srv.getForFinancials());
			}
		}

		return "success";

	}

	@GetMapping("/alloc/{usSTId}")
	public String testTotalDeployment(@PathVariable long usSTId)
	{

		double alloc = repoHoldings.getTotalAllocation(usSTId);
		System.out.println("Total Allocation for Strategy :  " + usSTId + " : " + alloc);
		return "success";
	}

	@GetMapping("/Strategy/{stgyId}")
	public String testStgyAlloc(@PathVariable int stgyId)
	{
		if (stgyId > 0)
		{
			try
			{
				StgyStatsSummary stgySummary = stgyStatsSrv.getStatsforStrategy(stgyId);
				if (stgySummary != null)
				{
					if (stgySummary.getStgyNiftyCagrVals() != null)
					{
						for (NiftyStgyCAGR cagrItem : stgySummary.getStgyNiftyCagrVals())
						{
							System.out.println(cagrItem.getDurationVal() + " Strategy CAGR : " + cagrItem.getStgyCAGR()
									+ "%" + " v/s NIFTY CAGR : " + cagrItem.getNiftyCAGR() + "%");
						}
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/stgySecAlloc/{stgyId}")
	public String testStgySecAlloc(@PathVariable int stgyId)
	{
		List<SectorAllocations> secAlloc = stgyStatsSrv.getSectorSplitUpforStrategy(stgyId);
		for (SectorAllocations sectorAllocations : secAlloc)
		{
			System.out
					.println("Sector : " + sectorAllocations.getSector() + " ---" + sectorAllocations.getAlloc() + "%");
		}

		return "success";
	}

	@GetMapping("/stalloc/{stgyId}")
	public String testStgyAllocPer(@PathVariable int stgyId)
	{

		List<IStgyAllocShort> listAlloc = repoStgyAlloc.findAllByStrategyStid(stgyId);
		for (IStgyAllocShort alloc : listAlloc)
		{
			System.out.println(alloc.getSccode() + "---" + alloc.getAlloc() + "%");
		}
		return "success";
	}

	@GetMapping("/ss/{scCode}")
	public String testStockSnapshot(@PathVariable String scCode)
	{
		if (scCode != null)
		{
			try
			{
				StockSnapshot ss = sSHSrv.getStockSnapshot(scCode);
				if (ss != null)
				{
					System.out.println(ss.getQuoteBasic());
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/quoteHAll")
	public String testStocksPricesHistory(

	)
	{

		String[] stocks = new String[]
		{ "BAJFINANCE" };
		// { "BAJFINANCE", "ALKYLAMINE", "LTI", "AFFLE" };

		try
		{
			List<StockHistory> scripsHistory = StockPricesUtility.getHistoricalPricesforScrips(stocks, Calendar.MONTH,
					3, Interval.DAILY);
			if (scripsHistory != null)
			{
				for (StockHistory stockHistory : scripsHistory)
				{
					System.out.println(stockHistory.getScCode());
					for (HistoricalQuote hQ : stockHistory.getPriceHistory())
					{
						System.out.println(hQ.getDate() + "--" + hQ.getClosePrice());
					}
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/quote/{scCode}")
	public String getQuote(@PathVariable String scCode) throws Exception
	{

		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				Stock quote = StockPricesUtility.getQuoteforScrip(scCode);
				if (quote != null)
				{
					System.out.println("CMP Rs. : " + quote.getQuote().getPrice());
				}
			}
		}

		return "success";
	}

	@GetMapping("/timeseries/{stgyId}")
	public String timeSeries(@PathVariable int stgyId)
	{
		List<StgyRelValuation> valuationsbyDate;
		try
		{
			valuationsbyDate = timeSeriesSrv.getValuationsforStrategy(stgyId,
					stocktales.historicalPrices.enums.EnumInterval.Last5Yrs);
			if (valuationsbyDate != null)
			{
				for (StgyRelValuation stgyRelValuation : valuationsbyDate)
				{
					System.out.println(stgyRelValuation.getDate() + "----" + stgyRelValuation.getValue());
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/schRet")
	public String timeSeriesSchema()
	{
		List<StgyRelValuation> valuationsbyDate;
		try
		{
			valuationsbyDate = timeSeriesSrv.getValuationsforSchema(EnumInterval.Last5Yrs);
			if (valuationsbyDate != null)
			{
				for (StgyRelValuation stgyRelValuation : valuationsbyDate)
				{
					System.out.println(stgyRelValuation.getDate() + "----" + stgyRelValuation.getValue());
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/sccodes")
	public String allScrips(

	)
	{
		if (repoBseData != null)
		{
			List<String> scCodes = repoBseData.findAllNseCodes();
			List<StockHistory> stocksHistory;
			List<StockHistory> stocksHistoryM = new ArrayList<StockHistory>();
			if (scCodes != null)
			{
				if (scCodes.size() > 0)
				{
					String[] scrips = new String[scCodes.size()];
					int i = 0;

					for (String scCode : scCodes)
					{

						scrips[i] = scCode;
						i++;

					}

					try
					{
						stocksHistory = StockPricesUtility.getHistoricalPricesforScrips(scrips, Calendar.MONTH, 28,
								Interval.MONTHLY);
						if (stocksHistory != null)
						{

							stocksHistoryM.addAll(stocksHistory);
						}
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (stocksHistoryM.size() > 0)
					{

						for (StockHistory stH : stocksHistoryM)
						{
							System.out.println(stH.scCode + " - Months : " + stH.getPriceHistory().size());

						}
						System.out.println("Total Scrips in 'A','B','T' BSE Catg :  " + scCodes.size());
						System.out.println("Total Scrips Data Available  " + stocksHistoryM.size());
					}

				}
			}
		}

		return "success";
	}

	@GetMapping("/nfs")
	public String nfs(

	)
	{
		NFSContainer nfsContainer = null;
		if (nfsProcessor != null)
		{
			try
			{
				long start = System.currentTimeMillis();

				nfsContainer = nfsProcessor.generateProposal(true).get();
				if (nfsContainer.getBaseDataPool().size() > 0)
				{
					if (nfsContainer.getNfsStats() != null)
					{
						System.out.println("---------------------------------------------------------------------");
						System.out.println("Total Scrips in 'A','B','T' BSE Catg :  "
								+ nfsContainer.getNfsStats().getNumScripsTotal());
						System.out.println(
								"Total Scrips Data Available  " + nfsContainer.getNfsStats().getNumScripsDataAvail());
						System.out.println("---------------------------------------------------------------------");

						System.out.println("----------------------FILTER STATS - BEGIN ---------------------------");
						System.out.println(" Min'm Mcap. Filter Removed # Scrips -  "
								+ nfsContainer.getNfsStats().getNumMcapFltOut());

						System.out.println(" Traded Months Filter Removed # Scrips -  "
								+ nfsContainer.getNfsStats().getNumDurationFltOut());

						System.out.println(" Monthly Returns Consistency Filter Removed # Scrips -  "
								+ nfsContainer.getNfsStats().getNumConsistencyFltOut());

						System.out.println(" Top N Average & Min Monthly Return Run Rate -  "
								+ nfsContainer.getNfsStats().getNumScripsTopN() + " Scrips qualify!"
								+ "  Top'N' Rate/Month : "
								+ Precision.round(nfsContainer.getNfsStats().getRRTopNAvg(), 2) + "%"
								+ "  Min'n Monthly Return Rate:  "
								+ Precision.round(nfsContainer.getNfsStats().getRRMin(), 2) + "%");

						System.out.println(" Price Momentum Trends Removed # Scrips -  "
								+ nfsContainer.getNfsStats().getNumSMACMP_Trends_FltOut());

						System.out.println(" Price Manipulation Scrips Removed -  "
								+ nfsContainer.getNfsStats().getPriceManipulationFltOut());

						System.out.println("----------------------FILTER STATS - END -----------------------------");
					}

					System.out.println("----------------------Scrips Data - BEGIN---------------------------");
					for (NFSConsistency nfsCons : nfsContainer.getBaseDataPool())
					{

						System.out.println(" Scrip : " + nfsCons.getScCode() + "   Data for Last :  "
								+ nfsCons.getNumMonths() + " Months " + "   MCap (Cr.) : " + nfsCons.getMCap()
								+ "  Monthly Return Rate (%) - " + nfsCons.getMonthlyRR());
					}

					System.out.println("----------------------Scrips Data - END---------------------------");

					System.out.println(
							"----------------------Scrips Price Manipulation - BEGIN---------------------------");
					for (NFSPriceManipulation nfsPM : nfsContainer.getManipulatedScrips())
					{
						System.out.println("Scrip Code :  " + nfsPM.getScCode());

						System.out.println("Date -- Open --- High -- Low --Close ---Delta H/L ---Delta O/C");
						for (NFSPriceManipulationItems nfsItem : nfsPM.getPriceItems())
						{
							System.out.println(nfsItem.getDate() + " - " + nfsItem.getOpen().doubleValue() + " - "
									+ nfsItem.getHigh().doubleValue() + " - " + nfsItem.getClose().doubleValue() + " - "
									+ nfsItem.getDeltaHL().doubleValue() + "-" + nfsItem.getDeltaOC().doubleValue());
						}
					}

					System.out.println(
							"----------------------Scrips Price Manipulation - END---------------------------");

					System.out.println("----------------------Scrips Price Trends - BEGIN---------------------------");
					System.out.println("Total # Scrips Passing Consistency and Momentum Test -  "
							+ nfsContainer.getFinalSieveScores().size());

					System.out.println(
							"----------------------Scrips Finally Sieved & Ranked ---------------------------");
					for (NFSScores nfsScores : nfsContainer.getFinalSieveScores())
					{

						System.out.println(" Scrip : " + nfsScores.getScCode() + "   Consistency Score  "
								+ nfsScores.getMrScore() + " Momentum Score: " + nfsScores.getMomentumScore()
								+ " Consolidated Score : " + nfsScores.getConsolidatedScore() + " Rank : "
								+ nfsScores.getRankCurr() + " Series : " + nfsScores.getSeries());

					}

					System.out.println("----------------------Scrips Price Trends - END---------------------------");

					System.out.println("Elapsed time: " + ((System.currentTimeMillis() - start)) / 60000 + "  mins..");

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/topgun")
	public String topgun(

	)
	{
		if (topGunSrv != null)
		{
			try
			{
				TopGunContainer tgC = topGunSrv.dryRun(8, 2);
				if (tgC != null)
				{
					if (tgC.getIntvStats().size() > 0)
					{
						double sumEqWtRet = 0;
						double sumTGRet = 0;
						for (IntervalStats intv : tgC.getIntvStats())
						{

							System.out.println("****************************************************************");
							System.out.println();
							System.out.println("Iteration ending on :  " + intv.getEndDate().toString());
							System.out.println("-----------------------------------------------------------");
							System.out.println("Equi. Weighted Returns - " + Precision.round(intv.getAllEquwRet(), 1));
							sumEqWtRet += intv.getAllEquwRet();

							System.out.println("TG Realized Returns - " + Precision.round(intv.getTopNRealized(), 1));
							System.out
									.println("TG Unrealized Returns - " + Precision.round(intv.getTopNUnrealized(), 1));
							System.out.println("-----------------------------------------------------------");
							System.out.println("TG Total Returns - "
									+ Precision.round((intv.getTopNRealized() + intv.getTopNUnrealized()), 1));

							sumTGRet += Precision.round((intv.getTopNRealized() + intv.getTopNUnrealized()), 1);

							System.out.println("_______________________________________________________________");
							System.out.println("Cumulative TG Returns - " + Precision.round(sumTGRet, 1));
							System.out.println("Cumulative EqWt. Returns - " + Precision.round(sumEqWtRet, 1));
							System.out.println();
							System.out.println("****************************************************************");
						}
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/topgun/rebal")
	public String topgunCreateRebal(

	)
	{
		if (topGunSrv != null)
		{

			try
			{
				topGunSrv.createRebalance(3);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/topgun/dataPool")
	public String topgunRefreshDataPool(

	)
	{
		if (topGunSrv != null)
		{
			try
			{
				topGunSrv.refreshListFromCorePF();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/nfsexit")
	public String nfsexitShow(

	)
	{
		if (nfsProcessor != null)
		{
			try
			{
				NFSPFExitSS exitSS = nfsProcessor.getPFExitSnapshot();
				List<NFSPFExitSMA> list = exitSS.getPfExitsSMAList();
				if (list != null)
				{
					if (list.size() > 0)
					{
						System.out.println(
								"----------------------NFS PF CMP/SMA Exit Details---------------------------");
						for (NFSPFExitSMA item : list)
						{
							System.out.println(
									"---------------------------------------------------------------------------------------------------");
							System.out.println("Scrip:  " + item.getScCode() + " |CMP : " + item.getPriceCmp()
									+ " | Exit Price :  " + item.getPriceExit() + " | Incl. Price :  "
									+ item.getPriceIncl() + " | Delta CMP/Exit:  " + item.getCmpExitDelta()
									+ " | P&L at Exit :  " + item.getPlExit());
							System.out.println(
									"--------------------------------------------------------------------------------------------------");
						}

					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/nfsrebal/{amount}")
	public String nfsRebalance(@PathVariable double amount

	)
	{
		if (nfsProcessor != null)
		{
			try
			{
				nfsProcessor.rebalancePF_DB(amount, true);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "success";
	}

	@GetMapping("/smaDelta/{scCode}/{days}")
	public String sma(@PathVariable String scCode, @PathVariable int days

	)
	{
		try
		{
			double delta = StockPricesUtility.getDeltaSMAforDaysfromCMP(scCode, days).getDelta();
			System.out.println("CMP / SMA" + days + " Price Delta :  " + delta);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	@GetMapping("/nfspf")
	public String nfsPF(

	)
	{
		if (nfsProcessor != null)
		{
			NFSPFSummary pfSummary;
			try
			{
				pfSummary = nfsUiSrv.getPfSummary();
				if (pfSummary != null)
				{
					System.out.println(pfSummary.getPfTable().size());
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/sma/{scCode}")
	public String sma(@PathVariable String scCode)
	{

		try
		{
			IDS_ScSMASpread scSMASpread = StockPricesUtility.getSMASpreadforScrip(scCode, new int[]
			{ 18, 45, 80, 170 }, Calendar.YEAR, 1, Interval.DAILY);
			if (scSMASpread != null)
			{
				System.out.println(scSMASpread.getScCode());
				for (IDS_SMASpread smaSpread : scSMASpread.getPrSMAList())
				{
					System.out.println(smaSpread.getSMAI1() + "|" + smaSpread.getSMAI2() + "|" + smaSpread.getSMAI3()
							+ "|" + smaSpread.getSMAI4());
				}

			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/vp/{scCode}")
	public String vp(@PathVariable String scCode)
	{

		try
		{
			IDS_VPDetails vpDetails = vpSrv.getVolatilityProfileDetailsforScrip(scCode);
			if (vpDetails != null)
			{
				System.out.println(vpDetails.getSccode());
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/vptest")
	public String vpTest()
	{

		List<String> scrips = new ArrayList<String>();
		scrips.add("BAJFINANCE");
		scrips.add("LTI");
		scrips.add("MINDTREE");
		scrips.add("COFORGE");
		scrips.add("LTTS");
		scrips.add("GLAND");
		scrips.add("DIVISLAB");
		scrips.add("LAURUSLABS");
		scrips.add("ASTRAL");
		scrips.add("APLAPOLLO");
		scrips.add("RELAXO");
		scrips.add("LUXIND");

		try
		{
			List<IDS_VPDetails> vpList = new ArrayList<IDS_VPDetails>();

			for (String scrip : scrips)
			{
				IDS_VPDetails vpDetails = vpSrv.getVolatilityProfileDetailsforScrip(scrip);

				vpList.add(vpDetails);

			}

			for (IDS_VPDetails vpDetails : vpList)
			{
				if (vpDetails != null)
				{
					System.out.println(vpDetails.getSccode());
					System.out.println("_______________________________________");
					System.out.println(vpDetails.getSma1breaches() + "|" + vpDetails.getSma2breaches() + "|"
							+ vpDetails.getSma3breaches() + "|" + vpDetails.getSma4breaches() + ">>"
							+ vpDetails.getVolscore() + " -- " + vpDetails.getVolprofile());
					System.out.println("_______________________________________");
				}
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/vpcore")
	public String vpCore()
	{
		if (this.corePFSrv != null)
		{
			try
			{
				corePFSrv.refreshPFVolatilityProfiles();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/buyP")
	public String buyProposals()
	{
		if (this.corePFSrv != null)
		{
			try
			{
				List<IDS_SCBuyProposal> buyPropist = corePFSrv.getBuyProposals().getBuyP();
				if (buyPropist != null)
				{
					if (buyPropist.size() > 0)
					{
						double sumInv = 0;
						for (IDS_SCBuyProposal buyP : buyPropist)
						{
							System.out.println("Buy " + buyP.getScCode() + " : " + buyP.getNumUnitsBuy() + " Units "
									+ "each at PPU of " + buyP.getPpuBuy() + " ||SMA Breach trigerred :  "
									+ buyP.getSmaBreach());
							sumInv += buyP.getNumUnitsBuy() * buyP.getPpuBuy();
						}
						System.out.println("Total Investments for Today - Rs. " + Precision.round(sumInv, 0));
					} else
					{
						System.out.println("Nothing to Buy today!!");
					}
				} else
				{
					System.out.println("Nothing to Buy today!!");
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/buyPAuto")
	public String buyProposalsAuto()
	{
		if (this.corePFSrv != null)
		{
			try
			{
				List<IDS_SCBuyProposal> buyPropist = corePFSrv.autoProcessBuyProposals();
				if (buyPropist != null)
				{
					if (buyPropist.size() > 0)
					{
						double sumInv = 0;
						for (IDS_SCBuyProposal buyP : buyPropist)
						{
							System.out.println("Buy " + buyP.getScCode() + " : " + buyP.getNumUnitsBuy() + " Units "
									+ "each at PPU of " + buyP.getPpuBuy() + " ||SMA Breach trigerred :  "
									+ buyP.getSmaBreach());
							sumInv += buyP.getNumUnitsBuy() * buyP.getPpuBuy();
						}
						System.out.println("Total Investments for Today - Rs. " + Precision.round(sumInv, 0));
					} else
					{
						System.out.println("Nothing to Buy today!!");
					}
				} else
				{
					System.out.println("Nothing to Buy today!!");
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/mbtxn/{ttype}/{amount}")
	public String MBTxn(@PathVariable char ttype, @PathVariable String amount)
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(millis);
		MoneyBag mbTxn = new MoneyBag();

		if (ttype == 'D') // Deposit
		{
			mbTxn.setType(EnumTxnType.Deposit);
			mbTxn.setRemarks("Deposit");

		} else if (ttype == 'W') // Withdraw
		{
			mbTxn.setType(EnumTxnType.Withdraw);
			mbTxn.setRemarks("Withdraw");
		} else if (ttype == 'B') // Dividend
		{
			mbTxn.setType(EnumTxnType.Dividend);
			mbTxn.setRemarks("Dividend");
		}

		mbTxn.setDate(date);
		mbTxn.setAmount(Double.valueOf(amount));

		mbSrv.processMBagTxn(mbTxn);

		return "success";

	}

	@GetMapping("/mbtxn/{ttype}/{sccode}/{amount}")
	public String MBTxn(@PathVariable char ttype, @PathVariable String sccode, @PathVariable String amount)
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(millis);
		MoneyBag mbTxn = new MoneyBag();

		if (ttype == 'D') // Deposit
		{
			mbTxn.setType(EnumTxnType.Deposit);
			mbTxn.setRemarks("Deposit");

		} else if (ttype == 'W') // Withdraw
		{
			mbTxn.setType(EnumTxnType.Withdraw);
			mbTxn.setRemarks("Withdraw");
		} else if (ttype == 'B') // Dividend
		{
			mbTxn.setType(EnumTxnType.Dividend);
			mbTxn.setRemarks("Dividend");
			if (!sccode.isEmpty())
			{
				mbTxn.setRemarks(mbTxn.getRemarks() + ":" + sccode);
			}
		}

		mbTxn.setDate(date);
		mbTxn.setAmount(Double.valueOf(amount));

		mbSrv.processMBagTxn(mbTxn);

		return "success";

	}

	@GetMapping("/pftxn/{scCode}/{ttype}/{units}/{ppu}/{smaRank}")
	public String MBTxn(@PathVariable String scCode, @PathVariable char ttype, @PathVariable String units,
			@PathVariable String ppu, @PathVariable String smaRank)
	{
		// Get Today's Date
		long millis = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(millis);
		HCI txn = new HCI();
		txn.setDate(date);
		txn.setSccode(scCode);
		txn.setSmarank(Integer.valueOf(smaRank));

		if (ttype == 'B') // BUY
		{
			txn.setTxntype(stocktales.usersPF.enums.EnumTxnType.Buy);

		} else if (ttype == 'S') // SELL
		{
			txn.setTxntype(stocktales.usersPF.enums.EnumTxnType.Sell);
		}

		txn.setUnits(Integer.valueOf(units));
		txn.setTxnppu(Double.valueOf(ppu));

		try
		{
			corePFSrv.processCorePFTxn(txn);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";

	}

	@GetMapping("/depAmnt/{scCode}/{smaRank}")
	public String depAmnt(@PathVariable String scCode, @PathVariable char smaRank)
	{
		if (depAmntSrv != null)
		{
			double amnt = 0;
			if (scCode != null)
			{
				switch (smaRank)
				{
				case '1':
					amnt = depAmntSrv.getDeploymentAmountByScrip4mPF(scCode, EnumSMABreach.sma1);
					break;
				case '2':
					amnt = depAmntSrv.getDeploymentAmountByScrip4mPF(scCode, EnumSMABreach.sma2);
					break;
				case '3':
					amnt = depAmntSrv.getDeploymentAmountByScrip4mPF(scCode, EnumSMABreach.sma3);
					break;
				case '4':
					amnt = depAmntSrv.getDeploymentAmountByScrip4mPF(scCode, EnumSMABreach.sma4);
					break;

				default:
					break;
				}
			}
		}

		return "success";

	}

	@GetMapping("/pfSMA")
	public String pfSMA()
	{
		if (corePFSrv != null)
		{
			try
			{
				List<IDS_SMAPreview> smaPV = corePFSrv.getPFSchemaSMAPreview();
				if (smaPV != null)
				{
					if (smaPV.size() > 0)
					{

						for (IDS_SMAPreview smaI : smaPV)
						{
							System.out.println(
									smaI.getScCode() + " | " + smaI.getClosePrice() + " >> " + smaI.getSMAI1() + " | "
											+ smaI.getSMAI2() + " | " + smaI.getSMAI3() + " | " + smaI.getSMAI4());
						}

					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/allocChg")
	public String allocChg()
	{
		if (corePFSrv != null)
		{
			IDS_ScAllocMassUpdate scMassUpdate = new IDS_ScAllocMassUpdate();

			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("APLAPOLLO", "Building Materials", 12, 12));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("ASTRAL", "Building Materials", 12, 12));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("BAJFINANCE", "Financials", 15, 15));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("GLAND", "Pharmaceuticals", 11, 11));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("LTI", "IT-Consulting", 12, 12));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("LTTS", "IT-ERD", 10, 10));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("MOLDTKPAC", "Packaging", 7, 7));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("SAREGAMA", "MPaaS", 7, 7));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("AFFLE", "Dig. Marketing", 7, 7));
			scMassUpdate.getScAllocList().add(new IDS_SCAlloc("ROUTE", "CPaaS", 7, 7));

			scMassUpdate.setDepAmtMode(EnumSchemaDepAmntsUpdateMode.None);

			try
			{
				corePFSrv.processAllocationChanges(scMassUpdate);
			} catch (Exception e)
			{

				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/xirr")
	public String xirr()
	{
		if (corePFSrv != null)
		{
			try
			{
				XIRRContainer xirrCont = corePFSrv.calculateXIRRforPF();
				if (xirrCont != null)
				{
					System.out.println("PF XIRR: " + xirrCont.getXirr());
					System.out.println("------------ Itemized Details ---------------");

					for (DateAmount item : xirrCont.getTransactions())
					{
						System.out.println("Date: " + item.getDate() + "  Amount:  " + item.getAmount());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "success";
	}

	@GetMapping("/exitPF/{scCode}/{sellPPU}")
	public String depAmnt(@PathVariable String scCode, @PathVariable double sellPPU)
	{
		if (scCode != null && sellPPU >= 0)
		{
			try
			{
				corePFSrv.removeScrip4mSchemaPF(scCode, sellPPU);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/scDate/{scCode}")
	public String scDate(@PathVariable String scCode)
	{
		if (scCode != null)
		{
			try
			{
				System.out.println(UtilDurations.getQuarterNamefromNumber(scContSrv.getLatestQDateForScrip(scCode)));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/cagrret/{scCode}")
	public String cagrRet(@PathVariable String scCode)
	{
		int[] durations = new int[]
		{ 2, 3, 5 };

		try
		{
			ScripCMPHistReturns returns = StockPricesUtility.getHistoricalCAGRforScrip(scCode, Calendar.YEAR,
					durations);
			if (returns != null)
			{
				if (returns.getReturns() != null)
				{
					System.out.println("Returns for Scrip :  " + returns.getSccode() + " || available at CMP : Rs. "
							+ returns.getCmp());
					for (IntvPriceCAGR intv : returns.getReturns())
					{
						System.out.println("Interval : " + intv.getInterval() + " | Price on Interval : "
								+ intv.getPrice() + " | CAGR Returns : " + intv.getCAGR());
					}

				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/realPL/{scCode}")
	public String realzPL4Scrip(@PathVariable String scCode)
	{
		if (scCode.trim().length() > 0)
		{
			IDS_SC_PL scPL;
			try
			{
				scPL = corePFSrv.getRealizedPL4Scrip(scCode);
				if (scPL != null)
				{
					System.out.println("Realized P&L for Scrip - " + scCode + " : Rs.  " + scPL.getNettPLAmount());
					System.out.println("---------------- Itemized BreakUp -------------- ");
					for (IDS_SC_PL_Items plItem : scPL.getPlItems())
					{
						System.out.println(" Sell Date:  " + plItem.getSaleDate() + "Realization Amount : Rs.  "
								+ plItem.getPlAmount());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";

	}

	@GetMapping("/split/{scCode}/{ratio}")
	public String scSplit(@PathVariable String scCode, @PathVariable int ratio)
	{
		if (StringUtils.hasText(scCode) && ratio > 1)
		{
			IDS_SC_SplitIP splitP = new IDS_SC_SplitIP(scCode, ratio);

			try
			{
				corePFSrv.adjustPF4StockSplit(splitP);
				System.out.println("Stock Split Done for  -  " + scCode + " in Ratio 1:" + ratio);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/bonus/{scCode}/{numfor}/{numget}")
	public String scBonus(@PathVariable String scCode, @PathVariable int numfor, @PathVariable int numget)
	{
		if (StringUtils.hasText(scCode) && numfor > 1 && numget > 1)
		{
			IDS_SC_BonusIP scBonusIP = new IDS_SC_BonusIP(scCode, numfor, numget);

			try
			{
				corePFSrv.adjustPF4StockBonus(scBonusIP);
				System.out.println("Stock Bonus Adjusted for  -  " + scCode);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/ddscList")
	public String ddScList()
	{
		// Go with NFS PF Scrips list

		if (repoNFSPF != null && nfsDDSrv != null)
		{
			List<String> scrips = repoNFSPF.getScrips4mPF();
			if (scrips != null)
			{
				if (scrips.size() > 0)
				{
					NFS_DD4ListScrips ddSC;
					try
					{
						ddSC = nfsDDSrv.getDDByScrips(scrips);

						if (ddSC != null)
						{
							System.out.println("Max Drawdown with Current Scrips #  " + scrips.size() + ": "
									+ ddSC.getMaxPerLoss() + "%");
							for (NFS_DD4ListScripsI ddItem : ddSC.getScripDDItems())
							{
								System.out.println(ddItem.getScCode() + ": " + ddItem.getCmp() + ": " + ddItem.getSma()
										+ ": " + ddItem.getDelta() + " % || " + ddItem.getWtdPLPer() + " %");
							}
						}
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		return "success";
	}

	@GetMapping("/ddscListPPU")
	public String ddScListPPU()
	{
		// Go with NFS PF Scrips list

		List<ScripPPU> scPPUList = new ArrayList<ScripPPU>();

		if (repoNFSPF != null && nfsDDSrv != null)
		{
			List<Object[]> scrips = repoNFSPF.getScripsPPUList();
			if (scrips != null)
			{
				if (scrips.size() > 0)
				{
					int i = 0;
					for (Object obj : scrips)
					{
						if (obj != null)
						{
							ScripPPU scPPU = new ScripPPU();
							scPPU.setSccode((String) scrips.get(i)[0]);
							scPPU.setPpu((double) scrips.get(i)[1]);
							scPPUList.add(scPPU);
						}
						i++;
					}

					NFS_DD4ListScrips ddSC;
					try
					{
						ddSC = nfsDDSrv.getDDByScripsPPU(scPPUList);

						if (ddSC != null)
						{
							System.out.println("Max Drawdown with Current Scrips #  " + scrips.size() + ": "
									+ ddSC.getMaxPerLoss() + "%");
							for (NFS_DD4ListScripsI ddItem : ddSC.getScripDDItems())
							{
								System.out.println(ddItem.getScCode() + ": " + ddItem.getCmp() + ": " + ddItem.getSma()
										+ ": " + ddItem.getDelta() + " % || " + ddItem.getWtdPLPer() + " %");
							}
						}
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		return "success";
	}

	@GetMapping("/ddscListPPUUnits")
	public String ddScListPPUUNITS()
	{

		if (repoNFSPF != null)
		{
			List<NFSPF> holdings = repoNFSPF.findAll();
			if (holdings != null)
			{
				List<ScripPPUUnitsRank> scList = new ArrayList<ScripPPUUnitsRank>();
				for (NFSPF holding : holdings)
				{
					ScripPPUUnitsRank item = new ScripPPUUnitsRank();
					item.setSccode(holding.getSccode());
					item.setPpu(holding.getPriceincl());
					item.setRankCurr(holding.getRankcurr());
					item.setUnits(holding.getUnits());

					scList.add(item);
				}

				try
				{
					NFSPFExitSS exitSS = nfsDDSrv.getDDByScripsPPUUnits(scList);
					if (exitSS != null)
					{
						System.out.println(exitSS.getMaxLossPer() + "% ------- Amount Rs. " + exitSS.getMaxLossStr());
					}
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return "success";
	}

	@GetMapping("/btids")
	public String btids()
	{
		List<BT_ScripAllocs> scAllocs = new ArrayList<BT_ScripAllocs>();
		if (repoPFSchema != null && bt_IdsSrv != null)
		{
			for (PFSchema pfSchema : repoPFSchema.findAll())
			{
				scAllocs.add(new BT_ScripAllocs(pfSchema.getSccode(), pfSchema.getIncalloc()));
			}

			BT_IP_IDS ip_params = new BT_IP_IDS(2, scAllocs, 250000, 50000, 30, 5, false);
			try
			{
				BT_EP_IDS bt_data = bt_IdsSrv.backTestIDS(ip_params);
				if (bt_data != null)
				{
					if (bt_data.getCalcData() != null)
					{
						System.out.println("Simulation Start : " + bt_data.getAdminData().getStartDate());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/scPrice/{scCode}")
	public String testscPriceonDate(@PathVariable String scCode)
	{
		if (scCode.trim().length() > 0)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			java.util.Date date;
			try
			{
				date = dateFormat.parse("2020-03-20");
				if (date != null)
				{

					System.out.println("Price as on " + date.toString() + ": "
							+ StockPricesUtility.getHistoricalPricesforScrip4Date(scCode, date));
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/nfsPFCreate/{amount}")
	public String testNFSPfCreate(@PathVariable String amount)
	{
		double amountD = Double.valueOf(amount);

		if (amountD > 0)
		{

			try
			{

				if (nfsCBSrv != null)
				{
					nfsCBSrv.processCBTxn(amountD, -19.6);

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/nfsPFCB")
	public String testNFSPFCB()
	{

		try
		{

			if (nfsCBSrv != null)
			{

				/*
				 * NFSCB_IP cbtxn2 = new NFSCB_IP(EnumNFSTxnType.Deploy, 91202.300);
				 * nfsCBSrv.processCBTxn(cbtxn2);
				 */

				/*
				 * NFSCB_IP cbtxn3 = new NFSCB_IP(EnumNFSTxnType.Dividend, 210);
				 * nfsCBSrv.processCBTxn(cbtxn3);
				 * 
				 * NFSCB_IP cbtxn4 = new NFSCB_IP(EnumNFSTxnType.SalePartial, 25000);
				 * nfsCBSrv.processCBTxn(cbtxn4);
				 */

				NFSCB_IP cbtxn5 = new NFSCB_IP(EnumNFSTxnType.Exit, 270000);
				nfsCBSrv.processCBTxn(cbtxn5);

			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";

	}

	@GetMapping("/nfsBal")
	public String testNFSDepAmnt()
	{

		double amnt;
		try
		{
			amnt = nfsCBSrv.getDeployableBalance();
			System.out.println("NFS Deployable Cash : " + amnt);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/yahoo")
	public String testYahooAPI()
	{
		Stock curr;
		try
		{

			curr = YahooFinance.get("ASTRAL.NS");
			System.out.println(curr.getQuote().getPrice());
			System.out.println("Current Quote : ON");

			Calendar from = UtilDurations.getTodaysCalendarDateOnly();
			Calendar to = UtilDurations.getTodaysCalendarDateOnly();
			from.add(Calendar.YEAR, -6);

			try
			{
				List<yahoofinance.histquotes.HistoricalQuote> scHistory = StockPricesUtility.getHistory("3PLAND", from,
						to, Interval.DAILY, true);
				if (scHistory != null)
				{
					System.out.println("Historical Quote : ON");
					for (yahoofinance.histquotes.HistoricalQuote historicalQuote : scHistory)
					{
						System.out.println(historicalQuote.getDate().getTime() + " : " + historicalQuote.getAdjClose());
					}
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	@GetMapping("/scPrices/{scCode}")
	public String testSCPrices(@PathVariable String scCode)
	{
		if (StringUtils.hasText(scCode))
		{
			Calendar to = UtilDurations.getTodaysCalendarDateOnly();

			Calendar from = UtilDurations.getTodaysCalendarDateOnly();
			from.add(Calendar.MONTH, -2);

			List<DL_ScripPrice> scPrices = repoScPrices.findAllBySccodeAndDateBetweenOrderByDateDesc(scCode,
					from.getTime(), to.getTime());
			if (scPrices != null)
			{
				if (scPrices.size() > 0)
				{
					System.out.println(scPrices.size() + " Records found!");
				}
			}
		}

		return "success";

	}

	@GetMapping("/dbPrices/{scCode}")
	public String testDBAPI(@PathVariable String scCode)
	{
		if (hpDBSrv != null && StringUtils.hasText(scCode))

		{
			List<DL_ScripPrice> scPrices = hpDBSrv.getHistoricalPricesByScripPast1Yr(scCode);
			if (scPrices != null)
			{
				if (scPrices.size() > 0)
				{
					System.out.println(scPrices.size() + " Records found!");
				}
			}
		}

		return "success";
	}

	@GetMapping("/hubStatsIDS")
	public String getHubStats()
	{
		if (repoScPrices != null)
		{
			List<IDL_IDSStats> hubStats = repoScPrices.getIDSDataHubStats();
			if (hubStats != null)
			{
				if (hubStats.size() > 0)
				{
					for (IDL_IDSStats stats : hubStats)
					{
						System.out.println(
								stats.getSccode() + stats.getMindate() + stats.getMaxdate() + stats.getNumentries());
					}
				}
			}
		}

		return "success";
	}

	@GetMapping("/ath")
	public String getATHContainer()
	{
		if (ATHSrv != null)
		{
			try
			{
				CompletableFuture<ATHContainer> athContainer = ATHSrv.generateProposal(true);
				if (athContainer != null)
				{
					System.out.println("Container Filled");
				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "success";
	}

	@GetMapping("/athPool/{scCode}")
	public String testATHPool(@PathVariable String scCode)
	{

		if (StringUtils.hasText(scCode))
		{

			Calendar from = UtilDurations.getTodaysCalendarDateOnly();
			from.add(Calendar.YEAR, -1);

			try
			{
				SC_CMP_52wkPenultimatePrice_Delta athPool = StockPricesUtility.getSCATHDataPool4Scrip(scCode, from);
				System.out.println(athPool.toString());
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

	@GetMapping("/athDL_InitialLoad")
	public String athperformInitialLoad()
	{
		if (athDLSrv != null)
		{
			long start = System.currentTimeMillis();
			long elapsedMins = 0;

			CompletableFuture<UploadStats> stats = (CompletableFuture<UploadStats>) athDLSrv.refreshDataLake();
			if (stats != null)
			{
				elapsedMins = (System.currentTimeMillis() - start) / 60000;
				System.out.println("ATH Data Uploaded in total : " + elapsedMins + " mins.");
				try
				{
					System.out.println("Scrips Uploaded :  " + stats.get().getNumScrips());
					System.out.println("Entries Uploaded :  " + stats.get().getNumEntries());
					System.out.println("Error Scrips :  " + stats.get().getNumErrors());

				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return "success";
	}

	@GetMapping("/athDLTest")
	public String athSave()
	{
		if (repoATHDL != null)
		{

			List<DL_ScripPriceATH> list = new ArrayList<DL_ScripPriceATH>();

			DL_ScripPriceATH scP = new DL_ScripPriceATH();
			scP.setSccode("ASTRAL");
			scP.setDate(UtilDurations.getTodaysDateOnly());
			scP.setCloseprice(2451.34);
			list.add(scP);

			DL_ScripPriceATH scP2 = new DL_ScripPriceATH();
			scP2.setSccode("ASTRAL");
			scP2.setDate(UtilDurations.getTodaysDateOnly());
			scP2.setCloseprice(2675.34);
			list.add(scP2);

			try
			{
				repoATHDL.saveAll(list);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "success";
	}

}
