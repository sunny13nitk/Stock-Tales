package stocktales.dataBook.model.repo.config;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.config.entity.CfgSectorsFieldsPool;

@Repository
public interface RepoCfgSecFP extends JpaRepository<CfgSectorsFieldsPool, Integer>
{
	public Optional<CfgSectorsFieldsPool> findBySector(
	        String sector
	);
}
