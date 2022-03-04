package stocktales.topgun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.topgun.model.entity.TGJournal;

@Repository
public interface RepoTopGunJournal extends JpaRepository<TGJournal, Integer>
{
	
}
