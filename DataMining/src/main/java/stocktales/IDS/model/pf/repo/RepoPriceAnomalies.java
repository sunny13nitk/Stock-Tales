package stocktales.IDS.model.pf.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.PriceAnomalies;

@Repository
public interface RepoPriceAnomalies extends JpaRepository<PriceAnomalies, String>
{

}
