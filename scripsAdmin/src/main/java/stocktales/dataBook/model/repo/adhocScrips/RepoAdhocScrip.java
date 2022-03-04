package stocktales.dataBook.model.repo.adhocScrips;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.model.entity.adhocScrips.AdhocScrip;

@Repository
public interface RepoAdhocScrip extends JpaRepository<AdhocScrip, String>
{
	@Query("select sccode from AdhocScrip")
	public List<String> getAllAdhoScripNames(
	);
	
	public Optional<AdhocScrip> findBySccodeIgnoreCase(
	        String scCode
	);
	
}
