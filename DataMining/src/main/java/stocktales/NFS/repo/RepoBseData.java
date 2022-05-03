package stocktales.NFS.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.BseDataSet;
import stocktales.NFS.repo.intfPOJO.IScCodeSeries;

@Repository
public interface RepoBseData extends JpaRepository<BseDataSet, String>
{
	@Query("select nsecode from BseDataSet")
	public List<String> findAllNseCodes();

	@Query("select count (*) from BseDataSet")
	public int getNumberofScrips();

	@Query("select nsecode as nsecode, series as series from BseDataSet")
	public List<IScCodeSeries> getAllNseCodesSeries();

	public Optional<BseDataSet> findByNsecode(String sccode);

}
