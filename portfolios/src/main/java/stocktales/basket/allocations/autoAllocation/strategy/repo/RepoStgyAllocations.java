package stocktales.basket.allocations.autoAllocation.strategy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyAllocShort;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;

@Repository
public interface RepoStgyAllocations extends JpaRepository<StAllocItem, Integer>
{
	@Query(
	    "Select s.sccode as sccode, g.Sector as sector, s.alloc as alloc "
	            + "from StAllocItem s inner join EN_SC_General g ON s.sccode = g.SCCode where s.strategy.stid = ?1 "
	)
	public List<Object[]> getAllocationsBySector(
	        int StrategyId
	);
	
	//@Query(" select s.sccode as sccode, s.alloc as alloc from StAllocItem s  where s.strategy.stid = ?1 ")
	public List<IStgyAllocShort> findAllByStrategyStid(
	        int strategyId
	);
	
}
