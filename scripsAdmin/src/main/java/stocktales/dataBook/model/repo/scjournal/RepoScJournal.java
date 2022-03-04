package stocktales.dataBook.model.repo.scjournal;

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
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJImage;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.ScJSummary;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.Tag;
import stocktales.dataBook.model.entity.scjournal.ScripJournal;

@Repository
public interface RepoScJournal extends JpaRepository<ScripJournal, Long>
{
	public List<ScripJournal> findAllBySccodeOrderByDateofentryDesc(
	        String scCode
	);
	
	public List<ScripJournal> findAllBySccodeAndTagContainingOrderByDateofentryDesc(
	        String scCode, String tagtext
	);
	
	public List<ScripJournal> findAllBySccodeAndCategoryAndTagContainingOrderByDateofentryDesc(
	        String scCode, EnumCategory category, String tagtext
	);
	
	public List<ScripJournal> findAllBySccodeAndEffectOrderByDateofentryDesc(
	        String scCode, EnumEffect effect
	);
	
	public List<ScripJournal> findAllBySccodeAndCategoryOrderByDateofentryDesc(
	        String scCode, String category
	);
	
	public List<ScripJournal> findAllBySccodeAndCategoryAndEffectAndTagContainingOrderByDateofentryDesc(
	        String scCode, EnumCategory category, EnumEffect effect, String tagtext
	);
	
	public List<ScripJournal> findAllBySccodeAndIntervalAndValdAndValmAndCategoryOrderByDateofentryDesc(
	        String scCode, EnumInterval interval, int valm, int vald, String category
	);
	
	public List<ScripJournal> findAllBySccodeAndSourceOrderByDateofentryDesc(
	        String scCode, EnumSource source
	);
	
	@Query(
	    "select MAX(s.dateofentry) as lastEntryDate, COUNT(s.id) as numEntries from ScripJournal s where s.sccode = ?1"
	)
	//Put it in List of Numand LastENtry in Service
	public List<Object[]> findByAsArray(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, category as placeholder from ScripJournal s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.category "
	)
	
	public List<PlaceHolderLong> countEntriesByCategory(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, source as placeholder from ScripJournal s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.source "
	)
	
	public List<PlaceHolderLong> countEntriesBySource(
	        String scCode
	);
	
	@Query(
	    "select COUNT(s.id) as numEntries, effect as placeholder from ScripJournal s  "
	            + "WHERE s.sccode = ?1 GROUP BY s.effect"
	)
	
	public List<PlaceHolderLong> countEntriesByEffect(
	        String scCode
	);
	
	@Query(
	    "select COUNT(DISTINCT category) as numCatgs, COUNT(DISTINCT source) as numSources,"
	            + " COUNT(DISTINCT effect) as numEffects, COUNT(DISTINCT tag) as numTags from ScripJournal s  "
	            + "WHERE s.sccode = ?1"
	)
	
	public List<ScJSummary> getSummaryByScCode(
	        String scCode
	);
	
	//Tag Projection
	public List<Tag> findAllBySccode(
	        String scCode
	);
	
	@Query("select DISTINCT(s.category)  from ScripJournal s WHERE s.sccode = ?1")
	public List<String> findCatgsBySccode(
	        String scCode
	);
	
	//Only Scrip Journals ID list for a Scrip Code and a Tag Containing
	public List<SCJID> findAllBySccodeAndTagContaining(
	        String scCode, String tagtext
	);
	
	@Query("select s.image as image from ScripJournal s WHERE s.id = ?1")
	public SCJImage getImageforJournalId(
	        long id
	);
	
}
