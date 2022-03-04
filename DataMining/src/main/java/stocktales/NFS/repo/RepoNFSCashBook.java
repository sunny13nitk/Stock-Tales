package stocktales.NFS.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import stocktales.NFS.model.entity.NFSCashBook;

public interface RepoNFSCashBook extends JpaRepository<NFSCashBook, Integer>
{
	@Query("select c from NFSCashBook c where c.date = ( select max(cc.date) from NFSCashBook cc where cc.date = c.date ) ")
	public NFSCashBook getLastestCashPosition();
}
