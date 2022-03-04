package stocktales.IDS.model.cfg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPDeployments;

@Repository
public interface IDS_RepoCF_VPDeployments extends JpaRepository<IDS_CF_VPDeployments, EnumVolatilityProfile>
{

}
