package stocktales.cagrEval.srv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.cagrEval.enums.EnumAggFxn;
import stocktales.cagrEval.enums.EnumDurationType;
import stocktales.cagrEval.enums.EnumStgyMode;
import stocktales.cagrEval.helperPoJo.BalSheetSrvParam;
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.DurationHeader;
import stocktales.cagrEval.helperPoJo.NiftyCAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.helperPoJo.XIRRItems;
import stocktales.cagrEval.helperPoJo.XIRRSummary;
import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.helperPoJo.YearsRollOverResults;
import stocktales.cagrEval.intf.IBalSheetUtilSrv;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.cagrEval.intf.INiftyCAGRSrv;
import stocktales.cagrEval.intf.IRollOverYrs;
import stocktales.dataBook.model.entity.adhocScrips.AdhocScrip;
import stocktales.dataBook.model.repo.adhocScrips.RepoAdhocScrip;
import stocktales.durations.UtilDurations;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.helperpojos.TY_YearFromTo;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;
import stocktales.strategy.helperPOJO.NiftyStgyCAGR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CAGRCalcSrv implements ICAGRCalcSrv
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private RepoAdhocScrip repoAdhocSc;
	
	@Autowired
	private IRollOverYrs rollOverYrsSrv;
	
	@Autowired
	private IBalSheetUtilSrv bSheetUtilSrv;
	
	@Autowired
	private INiftyCAGRSrv niftyCAGRSrv;
	
	@Autowired
	private IScAllocationListRepo scAllocListSrv;
	
	private EnumStgyMode stgyMode;
	
	private boolean e2eOnly;
	
	private YearsRollOverResults durations;
	
	private List<CAGRResult> cagrResults = new ArrayList<CAGRResult>();
	
	private List<NiftyStgyCAGR> scAllocCAGRResults = new ArrayList<NiftyStgyCAGR>();
	
	private List<String> scrips = new ArrayList<String>();
	
	private List<NameVal> stgyAllocations = new ArrayList<NameVal>();
	
	private final int[] intervals = new int[]
	{ 3, 5, 7, 10 };
	
	private final int maxYr = Calendar.getInstance().get(Calendar.YEAR);
	
	@Override
	public void Initialize(
	        List<String> scrips, boolean calcEndToEndOnly
	) throws Exception
	{
		clear();
		if (scrips != null)
		{
			if (scrips.size() > 0)
			{
				fsBuffSrv.Initialize(scrips, SheetNames.Analysis, false);
				stgyMode     = EnumStgyMode.None;
				this.e2eOnly = calcEndToEndOnly;
				this.scrips  = scrips;
			}
		}
		
	}
	
	@Override
	public void InitializeAdHoc(
	        List<NameVal> scripAllocationsList, boolean calcEndToEndOnly
	) throws Exception
	{
		clear();
		if (scripAllocationsList != null)
		{
			if (scripAllocationsList.size() > 0)
			{
				//Create a Method to Validate allocations if Not Directly coming from a Saved Strategy
				// - Method TBD
				/*
				 * - Must Include
				 *  -- Duplicate Scrips Detection
				 *  -- Total Allocations Total to be 100%
				 *  -- All Scrips Must Exist in DB
				 */
				
				List<String> scrips   = new ArrayList<String>();
				List<String> scripsDB = new ArrayList<String>();
				
				stgyMode     = EnumStgyMode.Adhoc;
				this.e2eOnly = calcEndToEndOnly;
				
				scrips      = scripAllocationsList.stream().map(x -> x.getName()).distinct()
				        .collect(Collectors.toList());
				this.scrips = scrips;
				
				double totalAlloc = Precision.round(scripAllocationsList.stream().mapToDouble(NameVal::getValue).sum(),
				        0);
				if (totalAlloc == 100)
				{
					
					for (NameVal nameVal : scripAllocationsList)
					{
						this.stgyAllocations.add(new NameVal(nameVal.getName(), nameVal.getValue()));
					}
					
					//Load Fact Sheet Buffer for allocation Scrips
					// consider AdHoc Scrips Too Here
					
					for (String scrip : scrips)
					{
						Optional<AdhocScrip> adSCO = repoAdhocSc.findBySccodeIgnoreCase(scrip);
						if (!adSCO.isPresent())
						{
							scripsDB.add(scrip);
						}
					}
					
					if (scripsDB.size() > 0)
					{
						
						fsBuffSrv.Initialize(scripsDB, SheetNames.Analysis, false);
					}
					
				}
				
			}
		}
		
	}
	
	@Override
	public void Initialize(
	        int strategyId, boolean calcEndToEndOnly
	) throws Exception
	{
		clear();
		//Get Allocation Items from Strategy
		
		if (strategyId > 0)
		{
			if (repoStgy != null)
			{
				Optional<Strategy> stgyO = repoStgy.findById(strategyId);
				if (stgyO.isPresent())
				{
					Strategy stgy = stgyO.get();
					if (stgy.getAllocItems() != null)
					{
						if (stgy.getAllocItems().size() > 0)
						{
							
							for (StAllocItem allocI : stgy.getAllocItems())
							{
								this.scrips.add(allocI.getSccode());
								
								this.stgyAllocations.add(new NameVal(allocI.getSccode(), allocI.getAlloc()));
							}
							
							//Set Strategy Mode ON - If Stgy Valid
							stgyMode     = EnumStgyMode.DB;
							this.e2eOnly = calcEndToEndOnly;
							
							//Load Fact Sheet Buffer for allocation Scrips
							fsBuffSrv.Initialize(scrips, SheetNames.Analysis, false);
						}
					}
					
				}
			}
			
		}
		
	}
	
	@Override
	public void calculateCAGR(
	        RollOverDurationsParam durationsParam
	) throws Exception
	{
		// Load the Roll Over Durations as per Calculations Needed
		
		this.durations = rollOverYrsSrv.generateRollOverYrs(durationsParam.getBaseYr(),
		        durationsParam.getIntervalinYrs(), durationsParam.getLength());
		
		//Loop over each of the Roll Over Durations and E2E Durations to perform stage 1 Calculations
		if (!e2eOnly)
		{
			for (YearsFromTo rolloverDuration : this.durations.getRollOverYrs())
			{
				CAGRResult cagrRes = new CAGRResult();
				cagrRes.setDurationH(new DurationHeader(rolloverDuration.getFrom(), rolloverDuration.getTo(),
				        EnumDurationType.RollOver));
				
				for (String scrip : this.getScrips())
				{
					if (!repoAdhocSc.findBySccodeIgnoreCase(scrip).isPresent())
					{
						if (bSheetUtilSrv != null)
						{
							//Trigger CAGR Calculation for Each Scrip for current loop pass calculation period
							BalSheetSrvParam bSheetParam = new BalSheetSrvParam();
							//Load the Param
							bSheetParam.setYrsFromToFilter(rolloverDuration);
							bSheetParam.setScCode(scrip);
							bSheetParam.setAttrName("MCap");
							bSheetParam.setFxntoTrigger(EnumAggFxn.CAGR);
							bSheetParam.setCurrentData(durationsParam.isTolastUpdate());
							
							//Call CAGR Calculation
							double cagrVal = Precision.round(bSheetUtilSrv.getFromBalSheetByParam(bSheetParam), 1);
							
							//Populate CAGR Items - Stage 1
							cagrRes.getItems().add(new XIRRItems(scrip, 0, cagrVal, 0));
						}
					}
				}
				
				this.cagrResults.add(cagrRes); //Add to Srv
			}
		}
		
		//Do it for End to End Duration Too
		CAGRResult cagrRes = new CAGRResult();
		cagrRes.setDurationH(new DurationHeader(durations.getE2eYrs().getFrom(), durations.getE2eYrs().getTo(),
		        EnumDurationType.EndToEnd));
		for (String scrip : this.getScrips())
		{
			if (!repoAdhocSc.findBySccodeIgnoreCase(scrip).isPresent())
			{
				if (bSheetUtilSrv != null)
				{
					//Trigger CAGR Calculation for Each Scrip for current loop pass calculation period
					BalSheetSrvParam bSheetParam = new BalSheetSrvParam();
					//Load the Param
					bSheetParam.setYrsFromToFilter(
					        new YearsFromTo(durations.getE2eYrs().getFrom(), durations.getE2eYrs().getTo()));
					bSheetParam.setScCode(scrip);
					bSheetParam.setAttrName("MCap");
					bSheetParam.setFxntoTrigger(EnumAggFxn.CAGR);
					
					bSheetParam.setCurrentData(durationsParam.isTolastUpdate());
					
					//Call CAGR Calculation
					double cagrVal = Precision.round(bSheetUtilSrv.getFromBalSheetByParam(bSheetParam), 1);
					
					//Populate CAGR Items - Stage 1
					cagrRes.getItems().add(new XIRRItems(scrip, 0, cagrVal, 0));
				}
			}
		}
		this.cagrResults.add(cagrRes); //Add to Srv
		
		//Do it for To Last Update Duration Too
		CAGRResult cagrResLU = new CAGRResult();
		cagrResLU.setDurationH(new DurationHeader(durations.getE2eYrs().getFrom(),
		        Calendar.getInstance().get(Calendar.YEAR), EnumDurationType.ToLastUpdate));
		for (String scrip : this.getScrips())
		{
			if (!repoAdhocSc.findBySccodeIgnoreCase(scrip).isPresent())
			{
				if (bSheetUtilSrv != null)
				{
					//Trigger CAGR Calculation for Each Scrip for current loop pass calculation period
					BalSheetSrvParam bSheetParam = new BalSheetSrvParam();
					//Load the Param
					bSheetParam.setYrsFromToFilter(
					        new YearsFromTo(durations.getE2eYrs().getFrom(),
					                Calendar.getInstance().get(Calendar.YEAR)));
					bSheetParam.setScCode(scrip);
					bSheetParam.setAttrName("MCap");
					bSheetParam.setFxntoTrigger(EnumAggFxn.CAGR);
					
					//Very Important to get Last Saved Data
					bSheetParam.setCurrentData(true);
					
					//Call CAGR Calculation
					double cagrVal = Precision.round(bSheetUtilSrv.getFromBalSheetByParam(bSheetParam), 1);
					
					//Populate CAGR Items - Stage 1
					cagrResLU.getItems().add(new XIRRItems(scrip, 0, cagrVal, 0));
				}
			}
		}
		this.cagrResults.add(cagrResLU); //Add to Srv
		
		//Perform Stage 2 -  Allocation weighted CAGR and Summarize findings for Each Rollover and E2E
		if (this.cagrResults.size() > 0)
		{
			switch (this.stgyMode)
			{
				case None:
					calcWtCAGRForNoneMode();
					break;
				
				case DB:
					calcWtCAGRforStrategy();
					
				case Adhoc: //From SC Allocation List REpo Session Bean
					calcWtCAGRforStrategy();
					
				default:
					break;
			}
		}
		
		//Perform Stage 3 - Adjust for No Data Found & Summarize
		adjustNoData_Summarize(durationsParam.isTolastUpdate());
		
	}
	
	@Override
	public void Initialize4mSCAllocationBuffer(
	) throws Exception
	{
		if (this.getScAllocListSrv() != null)
		{
			if (this.getScAllocListSrv().getScAllocList() != null)
			{
				if (this.getScAllocListSrv().getScAllocList().size() > 0)
				{
					List<NameVal> namevals = new ArrayList<NameVal>();
					
					for (ScAllocation scAllocation : this.getScAllocListSrv().getScAllocList())
					{
						/*//Do not Include Adhoc Scrips If Any in Strategy
						Optional<AdhocScrip> adSCO = repoAdhocSc.findBySccodeIgnoreCase(scAllocation.getScCode());*/
						
						namevals.add(new NameVal(scAllocation.getScCode(), scAllocation.getAllocation()));
						
					}
					
					this.InitializeAdHoc(namevals, true);
					for (int i = 0; i < intervals.length; i++)
					{
						//For Each Interval
						
						TY_YearFromTo duration = UtilDurations.getYearsFromHistory(intervals[i]);
						if (duration != null)
						{
							RollOverDurationsParam durationParam = new RollOverDurationsParam(duration.getYearFrom(), 1,
							        (duration.getYearTo() - duration.getYearFrom()), true);
							this.calculateCAGR(durationParam);
							
							List<CAGRResult> results = this.getCagrResults();
							
							if (results != null)
							{
								if (results.size() > 0)
								{
									
									/*
									 * Filter for Last Update result Enum in duration Type and then the one where to val is maximum 
									 */
									
									List<CAGRResult> cagrResultsLU = results.stream().filter(
									        x -> x.getDurationH().getDurationType() == EnumDurationType.ToLastUpdate)
									        .collect(Collectors.toList());
									if (cagrResultsLU != null)
									{
										
										if (cagrResultsLU.size() > 0)
										{
											
											CAGRResult cagrResult = cagrResultsLU.get(cagrResultsLU.size() - 1);
											if (cagrResult.getSummary() != null)
											{
												NiftyStgyCAGR res = new NiftyStgyCAGR();
												
												//Subtract additional 1 for current year - you are talking Since
												int deltaDur = cagrResult.getDurationH().getTo()
												        - cagrResult.getDurationH().getFrom() - 1;
												
												res.setDurationVal(
												        res.getDurationPrefix() + deltaDur + res.getDurationSuffix());
												
												res.setStgyCAGR(cagrResult.getSummary().getNettCAGR());
												res.setNiftyCAGR(cagrResult.getSummary().getNiftyCAGR());
												
												this.scAllocCAGRResults.add(res);
											}
										}
									}
									
								}
								
							}
						}
					}
				}
			}
			
		}
		
	}
	
	/*
	 * ---------------------------------- PRIVATE SECTION ---------------------------
	 */
	
	private void calcWtCAGRForNoneMode(
	)
	{
		int numScrips = this.scrips.size();
		
		double perscAlloc = (100 / numScrips); //In Percentage terms
		
		//Loop through CAGR REsults to Maintain Allocations and WtCAGR for Each XIRR ITEM within each CAGR Result
		
		for (CAGRResult cagrResult : cagrResults)
		{
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				xirrItem.setAllocation(perscAlloc);
				xirrItem.setWtCAGR(Precision.round((perscAlloc * xirrItem.getCAGR()) / 100, 1));
			}
		}
		
	}
	
	private void calcWtCAGRforStrategy(
	)
	{
		//Loop through CAGR REsults to Maintain Allocations and WtCAGR for Each XIRR ITEM within each CAGR Result
		
		for (CAGRResult cagrResult : cagrResults)
		{
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				Optional<NameVal> scAllocO = this.stgyAllocations.stream()
				        .filter(x -> x.getName().equals(xirrItem.getScCode())).findFirst();
				if (scAllocO.isPresent())
				{
					NameVal scAlloc = scAllocO.get();
					xirrItem.setAllocation(scAlloc.getValue());
					xirrItem.setWtCAGR(Precision.round((scAlloc.getValue() * xirrItem.getCAGR()) / 100, 1));
				}
				
			}
		}
	}
	
	private void adjustNoData_Summarize(
	        boolean tolastUpdate
	)
	{
		for (CAGRResult cagrResult : cagrResults)
		{
			double          sumzeroCAGRAlloc = 0;
			double          nettCAGR         = 0;
			NiftyCAGRResult nfRes            = null;
			
			for (XIRRItems xirrItem : cagrResult.getItems())
			{
				nettCAGR += xirrItem.getWtCAGR();
				if (xirrItem.getCAGR() == 0)
				{
					sumzeroCAGRAlloc += xirrItem.getAllocation();
				}
			}
			
			double deltatobase100 = 100 - sumzeroCAGRAlloc;
			
			double boostFactor = 1;
			
			if (deltatobase100 > 0)
			{
				boostFactor = 100 / deltatobase100;
			}
			
			XIRRSummary summary = new XIRRSummary();
			summary.setSumzeroCAGRAlloc(sumzeroCAGRAlloc);
			if (sumzeroCAGRAlloc > 0)
			{
				summary.setAdjusted(true);
			}
			summary.setBoostFactor(Precision.round(boostFactor, 1));
			summary.setNettCAGR(Precision.round(nettCAGR * boostFactor, 1));
			
			/*
			 * ---NIFTY CAGR
			 */
			if (cagrResult.getDurationH().getDurationType() != EnumDurationType.ToLastUpdate)
			{
				if (tolastUpdate == true)
				{
					nfRes = niftyCAGRSrv.calculateNiftyCAGRToDate(cagrResult.getDurationH().getFrom());
				} else
				{
					nfRes = niftyCAGRSrv.calculateNiftyCAGR(cagrResult.getDurationH().getFrom(),
					        cagrResult.getDurationH().getTo());
				}
			} else
			{
				if (tolastUpdate == true)
				{
					nfRes = niftyCAGRSrv.calculateNiftyCAGRToDate(cagrResult.getDurationH().getFrom());
					
				} else
				{
					nfRes = niftyCAGRSrv.calculateNiftyCAGR(cagrResult.getDurationH().getFrom(),
					        cagrResult.getDurationH().getTo());
				}
			}
			
			if (nfRes != null)
			{
				summary.setNiftyCAGR(nfRes.getCAGR());
			}
			
			cagrResult.setSummary(summary);
		}
	}
	
	private void clear(
	)
	{
		this.cagrResults        = new ArrayList<CAGRResult>();
		this.scrips             = new ArrayList<String>();
		this.stgyAllocations    = new ArrayList<NameVal>();
		this.scAllocCAGRResults = new ArrayList<NiftyStgyCAGR>();
	}
	
}
