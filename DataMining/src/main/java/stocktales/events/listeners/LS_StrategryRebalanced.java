package stocktales.events.listeners;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.basket.allocations.autoAllocation.strategy.repo.RepoStgyAllocations;
import stocktales.events.main.EV_StrategyRebalanced;
import stocktales.usersPF.model.UserPFConfig;
import stocktales.usersPF.model.UserStrategy;
import stocktales.usersPF.repo.RepoUserPFConfig;

@Service
public class LS_StrategryRebalanced implements ApplicationListener<EV_StrategyRebalanced>
{
	
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private RepoUserPFConfig repoUserPfConfig;
	
	@Autowired
	private RepoStgyAllocations repoStgyAlloc;
	
	@Autowired
	private IStgyRebalanceSrv rebalStgySrv;
	
	@Override
	public void onApplicationEvent(
	        EV_StrategyRebalanced ev_stgyRebal
	)
	{
		if (ev_stgyRebal != null)
		{
			saveStrategyAndPropogatetoSubscribedUsers(ev_stgyRebal.getStrategy());
		}
		
	}
	
	/*
	 *  ------------------------- PRIVATE SECTION --------------------
	 */
	@Transactional
	private void saveStrategyAndPropogatetoSubscribedUsers(
	        Strategy stgy
	)
	{
		if (stgy != null && repoStgy != null && repoUserPfConfig != null)
		{
			if (stgy.getStid() > 0)
			{
				//SAve Updated Strategy
				repoStgy.save(stgy);
				
				//Remove Orphan Allocations
				
				if (rebalStgySrv != null)
				{
					if (rebalStgySrv.getRblPojo() != null && repoStgyAlloc != null)
					
					{
						if (rebalStgySrv.getRblPojo().getTobeDeleted() != null)
						{
							if (rebalStgySrv.getRblPojo().getTobeDeleted().size() > 0)
							{
								for (int allocId : rebalStgySrv.getRblPojo().getTobeDeleted())
								{
									repoStgyAlloc.deleteById(allocId);
								}
							}
						}
					}
				}
				
				/*
				 * Update the User Strategies Subscribed which have active investments
				 */
				
				List<UserPFConfig> userConfigs = repoUserPfConfig.findAll();
				for (UserPFConfig userPFConfig : userConfigs)
				{
					Optional<UserStrategy> userStgyO = userPFConfig.getUserStrategies().stream()
					        .filter(x -> x.getStid() == stgy.getStid()).findFirst();
					if (userStgyO.isPresent())
					{
						UserStrategy userStgy = userStgyO.get();
						if (userStgy.getHoldings() != null && userStgy.isActive() == true)
						{
							if (userStgy.getHoldings().size() > 0)
							{
								//This User Strategy needs to be rebalanced
								userStgy.setNeedsrebalance(true);
								
								//Save the User Pf Config to Commit
								repoUserPfConfig.save(userPFConfig);
								
							}
						}
					}
				}
				
			}
		}
	}
	
}
