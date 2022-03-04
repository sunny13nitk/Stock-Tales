package stocktales.dataBook.model.repo.scJournalM;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.enums.EnumCategory;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.enums.EnumSource;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.PlaceHolderLong;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJID;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.ScJSummary;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.Tag;
import stocktales.dataBook.model.entity.scJournalM.ScripJournalM;

@Repository
public interface RepoScJournalM extends JpaRepository<ScripJournalM, Long>
{
	public List<ScripJournalM> findAllBySccodeOrderByDateofentryDesc(
	        String scCode
	);
	
	public List<ScripJournalM> findAllBySccodeOrderByDateofentryAsc(
	        String scCode
	);
	
	public List<ScripJournalM> findAllBySccodeAndTagContainingOrderByDateofentryDesc(
	        String scCode, String tagtext
	);
	
	public List<ScripJournalM> findAllBySccodeAndCategoryAndTagContainingOrderByDateofentryDesc(
	        String scCode, EnumCategory category, String tagtext
	);
	
	public List<ScripJournalM> findAllBySccodeAndEffectOrderByDateofentryDesc(
	        String scCode, EnumEffect effect
	);
	
	public List<ScripJournalM> findAllBySccodeAndCategoryOrderByDateofentryDesc(
	        String scCode, String category
	);
	
	public List<ScripJournalM> findAllBySccodeAndCategoryAndEffectAndTagContainingOrderByDateofentryDesc(
	        String scCode, EnumCategory category, EnumEffect effect, String tagtext
	);
	
	public List<ScripJournalM> findAllBySccodeAndIntervalAndValdAndValmAndCategoryOrderByDateofentryDesc(
	        String scCode, EnumInterval interval, int valm, int vald, String category
	);
	
	public List<ScripJournalM> findAllBySccodeAndSourceOrderByDateofentryDesc(
	        String scCode, EnumSource source
	);
	
	@Query(
	    "select MAX(s.dateofentry) as lastEntryDate, COUNT(s.id) as numEntries from ScripJournalM s where s.sccode = ?1"
	)
	//Put it in List of Numand LastENtry in Service
	public List<Object[]> findByAsArray(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, category as placeholder from ScripJournalM s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.category ORDER BY numEntries DESC"
	)
	
	public List<PlaceHolderLong> countEntriesByCategory(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, source as placeholder from ScripJournalM s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.source ORDER BY numEntries DESC"
	)
	
	public List<PlaceHolderLong> countEntriesBySource(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, effect as placeholder from ScripJournalM s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.effect ORDER BY numEntries DESC"
	)
	
	public List<PlaceHolderLong> countEntriesByEffect(
	        String scCode
	);
	
	@Query(
	    "select COUNT(DISTINCT category) as numCatgs, COUNT(DISTINCT source) as numSources,"
	            + " COUNT(DISTINCT effect) as numEffects, COUNT(DISTINCT tag) as numTags from ScripJournalM s  "
	            + "WHERE s.sccode = ?1"
	)
	
	public List<ScJSummary> getSummaryByScCode(
	        String scCode
	);
	
	//Tag Projection
	public List<Tag> findAllBySccode(
	        String scCode
	);
	
	@Query("select DISTINCT(s.category)  from ScripJournalM s WHERE s.sccode = ?1")
	public List<String> findCatgsBySccode(
	        String scCode
	);
	
	//Only Scrip Journals ID list for a Scrip Code and a Tag Containing
	public List<SCJID> findAllBySccodeAndTagContaining(
	        String scCode, String tagtext
	);
	
}
