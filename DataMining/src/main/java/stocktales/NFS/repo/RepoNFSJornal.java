package stocktales.NFS.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.NFSJournal;

@Repository
public interface RepoNFSJornal extends JpaRepository<NFSJournal, Integer>
{
	public List<NFSJournal> findAll(
	);
	
	@Query("select SUM( realplamnt) as realPl from NFSJournal")
	public double getRealzPl(
	);
}
