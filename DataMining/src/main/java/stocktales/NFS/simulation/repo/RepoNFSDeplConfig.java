package stocktales.NFS.simulation.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.NFSDeploymentConfig;

@Repository
public interface RepoNFSDeplConfig extends JpaRepository<NFSDeploymentConfig, Integer>
{

	@Query("select slabid, ddmin, ddmax from NFSDeploymentConfig N where N.ddmin <= ?1 AND N.ddmax >= ?1")
	public Optional<NFSDeploymentConfig> getCustomizingforCurrDD(double currDrawdowninDecimal);

}
