package stocktales.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;

@Repository
public interface Repo_SCBalSheet extends JpaRepository<EN_SC_BalSheet, Integer>
{
	public List<EN_SC_BalSheet> findAllBySCCode(
	        String scCode
	);
	
	public Optional<EN_SC_BalSheet> findBySCCodeAndYear(
	        String scCode, int year
	);
}
