package stocktales.IDS.model.pf.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.HC;

@Repository
public interface RepoHC extends JpaRepository<HC, String>
{
	@Modifying
	@Query("update HC h set  h.dividend = ?2  where h.sccode = ?1")
	public void updateDividendforScrip(String scCode, double amnt);

	@Modifying
	@Query("update HC h set  h.units = ?2, h.ppu = ?3  where h.sccode = ?1")
	public void updatePPUUnitsforScrip(String scCode, int units, double ppu);

	@Query("select ppu * units from HC where sccode = ?1")
	public double getTotalInvestmentforScrip(String scCode);

	@Query("Select SUM(ppu * units) from HC")
	public double getTotalInvestments();

}
