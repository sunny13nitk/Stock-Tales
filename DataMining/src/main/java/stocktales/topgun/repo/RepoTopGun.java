package stocktales.topgun.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.topgun.model.entity.TopGun;

@Repository
public interface RepoTopGun extends JpaRepository<TopGun, String>
{
	
	public List<TopGun> findAll(
	);
	
	public Optional<TopGun> findByNsecode(
	        String scCode
	);
	
	@Modifying
	@Query("update TopGun t set  t.rankcurr = ?2 where t.nsecode = ?1")
	public void updateTopGunHoldingRank(
	        String scCode, int RankCurr
	);
	
}
