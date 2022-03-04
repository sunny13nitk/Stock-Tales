package stocktales.IDS.model.cfg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.cfg.entity.IDS_CF_SMAWts;

@Repository
public interface IDS_RepoCF_SMAWts extends JpaRepository<IDS_CF_SMAWts, Integer>
{

}
