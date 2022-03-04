package stocktales.NFS.simulation.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.simulation.entities.NFSJournalSM;

@Repository
public interface RepoNFSJornalSM extends JpaRepository<NFSJournalSM, Integer>
{
	public List<NFSJournalSM> findAll();

	@Query("select SUM( realplamnt) as realPl from NFSJournalSM")
	public double getRealzPl();
}
