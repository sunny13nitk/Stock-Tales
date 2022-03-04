package stocktales.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;

@Repository
public interface SC10YearRepository extends JpaRepository<EN_SC_10YData, Integer>
{
	public Optional<EN_SC_10YData> findBySCCode(
	        String scCode
	);
}
