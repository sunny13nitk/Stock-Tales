package stocktales.dataBook.model.repo.scJournalM;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.model.entity.scJournalM.Snippet;

@Repository
public interface RepoSnippet extends JpaRepository<Snippet, Long>
{
	public List<RepoSnippet> findAllByScripJorunalId(
	        long scripJournalId
	);
	
	public List<RepoSnippet> findAllByNotesContainingIgnoreCase(
	        String notetext
	);
}
