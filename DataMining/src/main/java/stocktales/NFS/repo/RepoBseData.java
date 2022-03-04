package stocktales.NFS.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.BseDataSet;

@Repository
public interface RepoBseData extends JpaRepository<BseDataSet, String>
{
	@Query("select nsecode from BseDataSet")
	public List<String> findAllNseCodes(
	);
	
	@Query("select count (*) from BseDataSet")
	public int getNumberofScrips(
	);
	
	public Optional<BseDataSet> findByNsecode(
	        String sccode
	);
	
}
