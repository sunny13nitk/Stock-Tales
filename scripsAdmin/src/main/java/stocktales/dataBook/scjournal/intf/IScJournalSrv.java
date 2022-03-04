package stocktales.dataBook.scjournal.intf;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.helperPojo.scjournal.edit.ScJEdit;
import stocktales.dataBook.model.entity.scjournal.ScripJournal;
import stocktales.dataBook.model.pojo.PJIntervalSimple;
import stocktales.dataBook.model.pojo.ScripJournalPoJo;

public interface IScJournalSrv
{
	public ScripJournalPoJo createNewJournalforScrip(
	        String scCode
	);
	
	public ScripJournalPoJo getScJournal(
	);
	
	public ScJEdit getScJEdit(
	);
	
	public ScripJournalPoJo createNewJournalforScripandInterval(
	        String scCode, PJIntervalSimple intervalPoJo
	);
	
	public void uploadImage(
	        MultipartFile file
	);
	
	public void refreshScJournal(
	        ScripJournalPoJo scJP
	) throws Exception;
	
	public ScripJournal saveJournal(
	);
	
	public void clearBuffer(
	);
	
	public NumandLastEntry getQStats_New_ByScrip(
	        String scCode
	);
	
	public List<PlaceHolderLongPJ> getTagDetailsOvw(
	        String scCode
	);
	
	public List<String> getUniqueCatgsforJournals(
	        List<ScripJournal> je
	);
	
	public List<String> getUniqueTagsforscCode(
	        String scCode
	);
	
	public List<String> getUniqueCatgsforscCode(
	        String scCode
	);
	
	public void saveEditSCJ(
	        ScJEdit scJEdit
	) throws Exception;
}
