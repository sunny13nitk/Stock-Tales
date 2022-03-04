package stocktales.NFS.simulation.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.simulation.entities.NFSRunTmpSM;

@Repository
public interface RepoNFSTmpSM extends JpaRepository<NFSRunTmpSM, String>
{
	public List<NFSRunTmpSM> findAllByOrderByRank();

	public Optional<NFSRunTmpSM> findBySccode(String scCode);

	public void deleteAll();

	@Query("select max (date) from NFSRunTmpSM ")
	public Date getProposalDate();

}
