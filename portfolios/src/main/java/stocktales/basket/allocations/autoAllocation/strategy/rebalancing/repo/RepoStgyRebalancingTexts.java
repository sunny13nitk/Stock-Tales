package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.entity.StgyRebalancingTexts;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalTxts_Simple;

public interface RepoStgyRebalancingTexts extends JpaRepository<StgyRebalancingTexts, Integer>
{
	public List<StgyRebalancingTexts> findAllByStid(
	        int StgyId
	);
	
	public List<StgyRebalancingTexts> findAllBySccode(
	        String scCode
	);
	
	List<IStgyRebalTxts_Simple> findSimpleStgRebalTxts_ByStid(
	        int StgyId
	);
}
