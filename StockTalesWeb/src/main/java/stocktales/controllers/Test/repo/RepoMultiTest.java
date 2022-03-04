package stocktales.controllers.Test.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.controllers.Test.entity.MultiTest;

@Repository
public interface RepoMultiTest extends JpaRepository<MultiTest, Integer>
{
	public List<MultiTest> findAllByTagContainingIgnoreCase(
	        String tag
	);
}
