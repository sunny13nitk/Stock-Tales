package stocktales.NFS.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.repo.intfPOJO.ScripUnits;

@Repository
public interface RepoNFSPF extends JpaRepository<NFSPF, String>
{
	public List<NFSPF> findAll();

	public Optional<NFSPF> findBySccode(String scCode);

	@Query("select SUM( priceincl * units ) as totalInvestments from NFSPF ")
	public double getTotalInvestedValue();

	@Modifying
	@Query("update NFSPF n set  n.rankcurr = ?2, n.priceincl = ?3, n.units = ?4, n.datelasttxn = ?5 where n.sccode = ?1")
	public void updateNFSPFHolding(String scCode, int RankCurr, double ppu, int units, Date datetxn);

	@Modifying
	@Query("update NFSPF n set  n.priceincl = ?2, n.units = ?3  where n.sccode = ?1")
	public void updateHoldingUnitsPPU(String scCode, double ppu, int units);

	@Modifying
	@Query("update NFSPF n set  n.rankcurr = ?2 where n.sccode = ?1")
	public void updateNFSPFHoldingRank(String scCode, int RankCurr);

	@Query("select s.sccode, s.units from NFSPF s ")
	public List<ScripUnits> getScripsUnitsList();

	@Query("select s.sccode, s.priceincl from NFSPF s ")
	public List<Object[]> getScripsPPUList();

	@Query("select s.sccode from NFSPF s ")
	public List<String> getScrips4mPF();
}
