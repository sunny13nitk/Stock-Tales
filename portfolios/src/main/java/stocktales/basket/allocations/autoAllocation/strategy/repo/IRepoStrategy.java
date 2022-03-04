package stocktales.basket.allocations.autoAllocation.strategy.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyId;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyShort;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;

public interface IRepoStrategy extends JpaRepository<Strategy, Integer>
{
	public List<Strategy> findAllByConceptIgnoreCase(
	        String concept
	);
	
	@Query("Select s.stid as stid, s.name as name, s.concept as concept from Strategy s where stid = ?1")
	public IStgyShort findByStidShort(
	        int id
	);
	
	public Optional<Strategy> findByStid(
	        int stid
	);
	
	@Query("Select DISTINCT(stid) as stid from Strategy")
	public List<IStgyId> getAllStrategyIds(
	);
	
	@Query("Select COUNT(stid) from Strategy")
	public int getNumberofStrategies(
	);
	
}
