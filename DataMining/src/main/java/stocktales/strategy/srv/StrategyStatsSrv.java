package stocktales.strategy.srv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyAllocShort;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.RepoStgyAllocations;
import stocktales.cagrEval.enums.EnumDurationType;
import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.intf.ICAGRCalcSrv;
import stocktales.durations.UtilDurations;
import stocktales.helperpojos.TY_YearFromTo;
import stocktales.strategy.helperPOJO.NiftyStgyCAGR;
import stocktales.strategy.helperPOJO.ScripSectorAllocations;
import stocktales.strategy.helperPOJO.SectorAllocations;
import stocktales.strategy.helperPOJO.StgyStatsShortSummary;
import stocktales.strategy.helperPOJO.StgyStatsSummary;
import stocktales.strategy.intf.IStrategyStatsSrv;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StrategyStatsSrv implements IStrategyStatsSrv
{
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private RepoStgyAllocations repoStgyAlloc;
	
	@Autowired
	private ICAGRCalcSrv cagrCalcSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Value("${ERR_CAGRCALC_STGY}")
	private final String errCAGR = "";
	
	private final int[] intervals = new int[]
	{ 3, 5, 7, 10 };
	
	private final int maxYr = Calendar.getInstance().get(Calendar.YEAR);
	
	private StgyStatsSummary      stgyStats;
	private StgyStatsShortSummary stgyStatsShort;
	
	@Override
	public StgyStatsSummary getStatsforStrategy(
	        int strategyId
	) throws Exception
	{
		
		this.stgyStats = new StgyStatsSummary();
		
		//Populate basic Details
		this.stgyStats.setStgyDetails(repoStgy.findByStidShort(strategyId));
		
		//Populate the CAGR
		populateCAGR(strategyId);
		
		//Populate Allocations
		this.stgyStats.setScripAllocations(repoStgyAlloc.findAllByStrategyStid(strategyId));
		
		//Populate Sectors Split Up
		this.stgyStats.setSectorAllocations(this.getSectorSplitUpforStrategy(strategyId));
		
		return this.stgyStats;
	}
	
	@Override
	public StgyStatsShortSummary getShortStatsforStrategy(
	        int strategyId
	)
	{
		this.stgyStatsShort = new StgyStatsShortSummary();
		
		//Populate basic Details
		this.stgyStatsShort.setStgyDetails(repoStgy.findByStidShort(strategyId));
		
		//Populate Allocations
		List<IStgyAllocShort> perAllocs = repoStgyAlloc.findAllByStrategyStid(strategyId);
		if (perAllocs != null)
		{
			this.stgyStatsShort.setNumScrips(perAllocs.size());
			
		}
		
		//Populate Sectors Split Up
		List<SectorAllocations> secAllocs = this.getSectorSplitUpforStrategy(strategyId);
		if (secAllocs != null)
		{
			this.stgyStatsShort.setNumSectors(secAllocs.size());
		}
		
		return this.stgyStatsShort;
	}
	
	@Override
	public List<SectorAllocations> getSectorSplitUpforStrategy(
	        int strategyId
	)
	{
		List<Object[]>               vals           = repoStgyAlloc.getAllocationsBySector(strategyId);
		List<ScripSectorAllocations> scSecAllocList = new ArrayList<ScripSectorAllocations>();
		List<SectorAllocations>      secAlloc       = null;
		int                          i              = 0;
		for (Object Obj : vals)
		{
			
			if (Obj != null)
			{
				ScripSectorAllocations newalloc = new ScripSectorAllocations((String) vals.get(i)[0],
				        (String) vals.get(i)[1], (Double) vals.get(i)[2]);
				
				scSecAllocList.add(newalloc);
			}
			i++;
			
		}
		
		if (scSecAllocList != null)
		{
			if (scSecAllocList.size() > 0)
			{
				
				Map<String, Double> allocsPerSector = scSecAllocList.stream().collect(
				        Collectors.groupingBy(ScripSectorAllocations::getSector,
				                Collectors.summingDouble(ScripSectorAllocations::getAlloc)));
				if (allocsPerSector.size() > 0)
				{
					List<SectorAllocations> secAllocP = new ArrayList<SectorAllocations>();
					allocsPerSector.forEach((k, v) -> secAllocP.add(new SectorAllocations(k, Precision.round(v, 1))));
					secAlloc = secAllocP;
				}
				
			}
		}
		
		return secAlloc;
	}
	
	/*
	 * PRIVATE Section
	 * 
	 */
	
	private void populateCAGR(
	        int stgyId
	) throws Exception
	{
		for (int i = 0; i < intervals.length; i++)
		{
			//For Each Interval
			
			TY_YearFromTo duration = UtilDurations.getYearsFromHistory(intervals[i]);
			if (duration != null)
			{
				try
				{
					cagrCalcSrv.Initialize(stgyId, true);
					
					//In Strategy Statistics Service we would always compute to last update data from Previous Years
					RollOverDurationsParam durationParam = new RollOverDurationsParam(duration.getYearFrom(), 1,
					        (duration.getYearTo() - duration.getYearFrom()), true);
					cagrCalcSrv.calculateCAGR(durationParam);
					
					List<CAGRResult> results = cagrCalcSrv.getCagrResults();
					if (results != null)
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
								
								NiftyStgyCAGR niftyStgyCagr = new NiftyStgyCAGR();
								niftyStgyCagr.setDurationVal(niftyStgyCagr.getDurationPrefix() + intervals[i]
								        + niftyStgyCagr.getDurationSuffix());
								niftyStgyCagr.setStgyCAGR(results.get(0).getSummary().getNettCAGR());
								niftyStgyCagr.setNiftyCAGR(results.get(0).getSummary().getNiftyCAGR());
								this.stgyStats.getStgyNiftyCagrVals().add(niftyStgyCagr);
								
							}
						}
					}
				}
				
				catch (Exception e)
				{
					
					throw new Exception(msgSrc.getMessage(errCAGR, new Object[]
					{ stgyId, duration.getYearFrom(), duration.getYearTo(), e.getMessage() }, Locale.ENGLISH));
				}
			}
		}
	}
	
}
