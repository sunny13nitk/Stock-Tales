package stocktales.NFS.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.NFS.model.entity.NFSRunTmp;

@Repository
public interface RepoNFSTmp extends JpaRepository<NFSRunTmp, String>
{
	public List<NFSRunTmp> findAllByOrderByRank();

	public Optional<NFSRunTmp> findBySccode(String scCode);

	public void deleteAll();

	@Query("select max (date) from NFSRunTmp ")
	public Date getProposalDate();

}
