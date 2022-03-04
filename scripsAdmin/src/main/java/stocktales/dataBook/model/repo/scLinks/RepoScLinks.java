package stocktales.dataBook.model.repo.scLinks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.model.entity.scLinks.ScLinks;

@Repository
public interface RepoScLinks extends JpaRepository<ScLinks, String>
{
	public Optional<ScLinks> findBySccode(
	        String scCode
	);
}
