package stocktales.DataLake.model.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.DataLake.model.entity.DL_ScripPrice;
import stocktales.DataLake.model.repo.intf.IDates;

@Repository
public interface RepoScripPrices extends JpaRepository<DL_ScripPrice, Long>
{
	public List<DL_ScripPrice> findAllBySccode(String scCode);

	@Query("select MAX(date) from DL_ScripPrice where sccode = ?1")
	public Date getLatestEntryDate4Scrip(String scCode);

	@Query("select MAX(date) as maxdate, MIN(date) as mindate from DL_ScripPrice where sccode = ?1")
	public IDates getMaxMinDatesforScrip(String scCode);

	@Query("select COUNT(id) from DL_ScripPrice where sccode = ?1")
	public long getNumberofEntries4Scrip(String scCode);

	@Modifying
	@Query("Delete DL_ScripPrice where sccode = ?1")
	public void delete4SCrip(String scCode);

	public List<DL_ScripPrice> findAllBySccodeAndDateBetweenOrderByDateDesc(String scCode, Date from, Date to);
}
