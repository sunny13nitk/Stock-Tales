package stocktales.healthcheck.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.model.entity.Cfg_ScripHealthCheck;
import stocktales.healthcheck.repo.intf.IRepoCfgSrvStext;

@Repository
public interface Repo_CfgHC extends JpaRepository<Cfg_ScripHealthCheck, Integer>
{
	public List<Cfg_ScripHealthCheck> findByStext(
	        EnumHCCriteria criteria
	);
	
	@Query(
	    " select DISTINCT stext as stext, srvname as srvname , forFinancials as forFinancials from Cfg_ScripHealthCheck"
	)
	public List<IRepoCfgSrvStext> getServicesListUnique(
	);
	
}
