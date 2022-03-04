package stocktales.basket.allocations.autoAllocation.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStrategySrv;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StQuickStats;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.helperPOJO.NameValDouble;
import stocktales.helperPOJO.StrategyListPOJO;
import stocktales.predicates.manager.PredicateManager;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StrategySrv implements IStrategySrv
{
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private PredicateManager predMgr;
	
	private Strategy strategy;
	
	@Override
	public void Initialize(
	)
	{
		this.strategy = new Strategy();
		
	}
	
	@Override
	public void loadAllocationItems(
	        List<ScAllocation> allocItems
	)
	{
		if (allocItems != null)
		{
			if (allocItems.size() > 0)
			{
				this.strategy.setAllocItems(new ArrayList<StAllocItem>());
				for (ScAllocation scAllocation : allocItems)
				{
					
					StAllocItem scallocItem = new StAllocItem();
					
					scallocItem.setSccode(scAllocation.getScCode());
					scallocItem.setMos(scAllocation.getMoS());
					scallocItem.setAlloc(scAllocation.getAllocation());
					
					this.strategy.addAllocationItem(scallocItem);
				}
			}
		}
		
	}
	
	@Override
	public void saveHeaderInfo(
	        Strategy stgyHeader
	)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Strategy save(
	        Strategy strategyPojo
	)
	{
		if (strategyPojo != null)
		{
			this.strategy.setName(strategyPojo.getName());
			this.strategy.setConcept(strategyPojo.getConcept());
			this.strategy.setPredicatebean(predMgr.getActivePredicateBeanName(null));
			this.strategy.setActive(strategyPojo.isActive());
			this.strategy.setRebalanceallowed(strategyPojo.isRebalanceallowed());
			this.strategy = repoStgy.save(this.strategy);
		}
		
		return this.strategy;
	}
	
	@Override
	public StQuickStats getQuickStats(
	)
	{
		StQuickStats qStats = new StQuickStats();
		
		if (this.getStrategy().getAllocItems() != null)
		{
			if (this.getStrategy().getAllocItems().size() > 0)
			{
				qStats.setNumScrips(strategy.getAllocItems().size());
				
				StAllocItem maxItem = Collections.max(strategy.getAllocItems(),
				        Comparator.comparing(x -> x.getAlloc()));
				if (maxItem != null)
				{
					qStats.setMaxAlloc(new NameValDouble(maxItem.getSccode(), maxItem.getAlloc()));
				}
				
				StAllocItem minItem = Collections.min(strategy.getAllocItems(),
				        Comparator.comparing(x -> x.getAlloc()));
				if (minItem != null)
				{
					qStats.setMinAlloc(new NameValDouble(minItem.getSccode(), minItem.getAlloc()));
				}
			}
		}
		
		return qStats;
	}
	
	@Override
	public List<StrategyListPOJO> getStrategiesList(
	)
	{
		List<StrategyListPOJO> strategies = null;
		
		List<Strategy> stgyDB = repoStgy.findAll();
		
		List<Strategy> stgyDBActive = null;
		
		if (stgyDB != null)
		{
			if (stgyDB.size() > 0)
			{
				
				stgyDBActive = stgyDB.stream().filter(x -> x.isActive() == true).collect(Collectors.toList());
				
				strategies = new ArrayList<StrategyListPOJO>();
				for (Strategy strategy : stgyDBActive)
				{
					StrategyListPOJO strategyI = new StrategyListPOJO(strategy.getStid(), strategy.getName(),
					        strategy.getConcept(), strategy.getAllocItems().size(), strategy.isRebalanceallowed());
					strategies.add(strategyI);
				}
			}
		}
		return strategies;
	}
	
	@Override
	public List<ScAllocation> getValuationsSimulationforStrategy(
	        int StgId
	)
	{
		List<ScAllocation> allocList = new ArrayList<ScAllocation>();
		
		//Get the Strategy and its allocItems
		
		Optional<Strategy> stgyO = repoStgy.findById(StgId);
		
		if (stgyO.isPresent())
		{
			if (stgyO.get().getAllocItems() != null)
			{
				if (stgyO.get().getAllocItems().size() > 0)
				{
					for (StAllocItem allocI : stgyO.get().getAllocItems())
					{
						//Compute Valuation for Each AllocItem
						ScValuation scVal = scValSrv.getValuationforScrip(allocI.getSccode(), 0, allocI.getMos());
						//Create Allocation for Each Valuation with allocation percentage from allocItem being looped
						ScAllocation scAlloc = new ScAllocation(scVal);
						scAlloc.setAllocation(allocI.getAlloc());
						//Add to Repo List Bean the allocation
						allocList.add(scAlloc);
					}
				}
			}
			
		}
		
		return allocList;
	}
	
}
