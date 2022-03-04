package stocktales.topgun.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.topgun.model.entity.DataPool;

@Repository
public interface RepoDataPool extends JpaRepository<DataPool, String>
{
	@Query("select nsecode from DataPool")
	public List<String> findAllNseCodes(
	);
	
	@Query("select nsecode, rank from DataPool d where d.rank <= ?1")
	public List<DataPool> getTopN(
	        int uptoRank
	);
	
	public List<DataPool> findAll(
	);
	
	@Modifying
	@Query("update DataPool d set d.rank = ?2 where d.nsecode = ?1")
	public void updateRank(
	        String scCode, int rank
	);
}
