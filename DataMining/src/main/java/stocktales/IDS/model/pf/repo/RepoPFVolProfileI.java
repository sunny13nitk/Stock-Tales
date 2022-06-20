package stocktales.IDS.model.pf.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.IDS.model.pf.entity.PFVolProfileI;

@Repository
public interface RepoPFVolProfileI extends JpaRepository<PFVolProfileI, Integer>
{
	public List<PFVolProfileI> findAllBySccode(String scCode);
}
