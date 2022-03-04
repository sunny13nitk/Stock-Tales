package stocktales.IDS.model.cfg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.cfg.entity.IDS_CF_VPRange;

@Repository
public interface IDS_RepoCF_VPRange extends JpaRepository<IDS_CF_VPRange, String>
{

}
