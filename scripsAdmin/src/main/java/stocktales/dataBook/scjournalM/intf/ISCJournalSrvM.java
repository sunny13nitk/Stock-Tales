package stocktales.dataBook.scjournalM.intf;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.model.entity.scJournalM.ScripJournalM;
import stocktales.dataBook.model.entity.scJournalM.Snippet;
import stocktales.dataBook.model.pojo.EditSnippet;
import stocktales.dataBook.model.pojo.ScripJournalPoJoM;

public interface ISCJournalSrvM
{
	public ScripJournalPoJoM createNewJournalforScrip(
	        String scCode
	);
	
	public ScripJournalPoJoM getScJPoJoM(
	);
	
	public ScripJournalM getScripJournal(
	);
	
	public void clearBuffer(
	);
	
	public NumandLastEntry getQStats_New_ByScrip(
	        String scCode
	);
	
	public List<PlaceHolderLongPJ> getTagDetailsOvw(
	        String scCode
	);
	
	public List<String> getUniqueTagsforscCode(
	        String scCode
	);
	
	public List<String> getUniqueCatgsforscCode(
	        String scCode
	);
	
	public EditSnippet addSnippet(
	);
	
	public EditSnippet getEditSnippet(
	);
	
	public void clearSnippet(
	);
	
	public Object initialize(
	        ScripJournalPoJoM scJPM
	) throws Exception;
	
	public void uploadImage(
	        MultipartFile file
	);
	
	public void validateCleanseAddSnippet(
	        EditSnippet snippet
	) throws Exception;
	
	public ScripJournalM saveNewJournal(
	);
	
	public EditSnippet setEditSnippetfromSnippet(
	        Snippet snippet
	);
	
	public String getTagsPlain(
	);
	
	public List<String> getTagsPlainList(
	);
	
	public Snippet getSnippetfromBuffer(
	        long sid
	);
	
	List<String> getUniqueCatgsforJournals(
	        List<ScripJournalM> je
	);
}
