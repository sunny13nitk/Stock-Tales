package stocktales.cagrEval.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.cagrEval.model.NiftyTracker;

@Repository
public interface IRepoNiftyTracker extends JpaRepository<NiftyTracker, Integer>
{
	public Optional<NiftyTracker> findByYear(
	        int year
	);
}
