package stocktales.dataBook.model.repo.config;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.config.entity.CfgScripsFieldsPool;

@Repository
public interface RepoCfgScripFP extends JpaRepository<CfgScripsFieldsPool, Integer>
{
	public Optional<CfgScripsFieldsPool> findBySccode(
	        String scCode
	);
}
