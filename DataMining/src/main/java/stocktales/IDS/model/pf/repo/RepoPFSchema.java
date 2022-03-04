package stocktales.IDS.model.pf.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.intf.IPFSchemaIdealAlloc;
import stocktales.IDS.model.pf.repo.intf.IPFSchemaScripSector;

@Repository
public interface RepoPFSchema extends JpaRepository<PFSchema, String>
{

	@Query("select SUM( idealalloc ) as totalAlloc from PFSchema ")
	public double getSumIdealAllocations();

	@Query("select sccode as sccode, sector as sector from PFSchema")
	public List<IPFSchemaScripSector> getScripSectors();

	@Query("select SUM( incalloc ) as totalIncallocations from PFSchema ")
	public double getSumIncrementalAllocations();

	@Query("select SUM( depamnt ) as totalDepamnt from PFSchema ")
	public double getSumDeploymentAmount();

	@Query("select depamnt from PFSchema where sccode = ?1 ")
	public double getDeployableAmountforScrip(String scCode);

	@Query("select sccode from PFSchema ")
	public List<String> getPFScripCodes();

	@Modifying
	@Query("update PFSchema p set  p.depamnt = ?2  where p.sccode = ?1")
	public void updateDeployableAmountforScrip(String scCode, double amnt);

	@Modifying
	@Query("update PFSchema p set  p.idealalloc = ?2  where p.sccode = ?1")
	public void updateIdealAllocforScrip(String scCode, double idealAlloc);

	@Modifying
	@Query("update PFSchema p set  p.incalloc = ?2  where p.sccode = ?1")
	public void updateIncAllocforScrip(String scCode, double incAlloc);

	@Modifying
	@Query("update PFSchema p set  p.sector = ?2  where p.sccode = ?1")
	public void updateSectorforScrip(String scCode, String sector);

	@Modifying
	@Query("update PFSchema p set  p.idealalloc = ?2, p.incalloc =?3, p.depamnt=?4  where p.sccode = ?1")
	public void updateSchemEntityforScrip(String scCode, double idealAlloc, double incAlloc, double depAmnt);

	@Modifying
	@Query("update PFSchema p set  p.depamnt = 0 ")
	public void clearDeployableAmounts();

	@Query("select sccode as sccode, idealalloc as alloc from PFSchema")
	public List<IPFSchemaIdealAlloc> getIdealAllocations();

}
