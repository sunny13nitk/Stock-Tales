package stocktales.siteconfig.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.siteconfig.model.entity.SitePaths;

@Repository
public interface RepoSitePaths extends JpaRepository<SitePaths, Integer>
{
	public Optional<SitePaths> findByTitleIgnoreCase(
	        String title
	);
	
	public Optional<SitePaths> findByUrlIgnoreCase(
	        String title
	);
}
