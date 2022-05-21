package stocktales.DataLake.model.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.DataLake.model.repo.intf.IDL_IDSStats;
import stocktales.DataLake.model.repo.intf.IDates;
import stocktales.DataLake.model.repo.intf.IScMaxDate;

@Repository
public interface RepoATHScripPrices extends JpaRepository<DL_ScripPriceATH, Long>
{
	public List<DL_ScripPriceATH> findAllBySccode(String scCode);

	@Query("select MAX(date) from DL_ScripPriceATH where sccode = ?1")
	public Date getLatestEntryDate4Scrip(String scCode);

	@Query("select MAX(date) as maxdate, MIN(date) as mindate from DL_ScripPriceATH where sccode = ?1")
	public IDates getMaxMinDatesforScrip(String scCode);

	@Query("select DISTINCT(d.sccode) as sccode,  MIN(d.date) as mindate, MAX(d.date) as maxdate, COUNT(d.id) as numentries from DL_ScripPriceATH d"
			+ " inner join NFSPF s ON d.sccode = s.sccode  GROUP BY d.sccode")
	public List<IDL_IDSStats> getNFSDataHubStats();

	@Query("select DISTINCT(d.sccode) as sccode,  MAX(d.date) as maxdate, MAX(d.id) as id from DL_ScripPriceATH d"
			+ " inner join NFSPF s ON d.sccode = s.sccode  GROUP BY d.sccode")
	public List<IScMaxDate> getNFSDataHubLatestSripDate();

	@Query("select DISTINCT(sccode) as sccode,  MIN(date) as mindate, MAX(date) as maxdate, COUNT(id) as numentries from DL_ScripPriceATH GROUP BY sccode")
	public List<IDL_IDSStats> getGlobalDataHubStats();

	@Query("select COUNT( DISTINCT sccode ) from DL_ScripPriceATH ")
	public List<IDL_IDSStats> getNumberofScrips();

	@Query("select COUNT(id) from DL_ScripPriceATH where sccode = ?1")
	public long getNumberofEntries4Scrip(String scCode);

	@Query("select closeprice from DL_ScripPriceATH where id = ?1")
	public double getClosePricebyId(long id);

	@Modifying
	@Query("Delete DL_ScripPriceATH where sccode = ?1")
	public void delete4SCrip(String scCode);

	@Modifying
	@Query("update DL_ScripPriceATH p set  p.closeprice = ?2  where p.id = ?1")
	public void updateClosePrice(long id, double closePrice);

	public List<DL_ScripPriceATH> findAllBySccodeAndDateBetweenOrderByDateDesc(String scCode, Date from, Date to);
}
