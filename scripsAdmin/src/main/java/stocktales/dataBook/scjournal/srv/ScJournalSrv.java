package stocktales.dataBook.scjournal.srv;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJID;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.Tag;
import stocktales.dataBook.helperPojo.scjournal.edit.ScJEdit;
import stocktales.dataBook.model.entity.scjournal.ScripJournal;
import stocktales.dataBook.model.pojo.PJIntervalSimple;
import stocktales.dataBook.model.pojo.ScripJournalPoJo;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.dataBook.scjournal.intf.IScJournalSrv;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScJournalSrv implements IScJournalSrv
{
	@Autowired
	private RepoScJournal repoScJournal;
	
	@Autowired
	private MessageSource msgSrc;
	
	private ScripJournalPoJo scJournal;
	
	private ScJEdit scJEdit;
	
	@Value("${CATG_CARDINALITY}")
	private String catg_error;
	
	@Value("${CATG_TAG_NOTFOUND}")
	private String catg_tag_notfound;
	
	@Value("${IMG_NOTES_NOTFOUND}")
	private String img_notes_notfound;
	
	@Override
	public ScripJournalPoJo createNewJournalforScrip(
	        String scCode
	)
	{
		this.clearBuffer();
		this.scJournal = new ScripJournalPoJo();
		scJournal.setSccode(scCode);
		
		return scJournal;
	}
	
	@Override
	public ScripJournalPoJo createNewJournalforScripandInterval(
	        String scCode, PJIntervalSimple intervalPoJo
	)
	{
		this.scJournal = new ScripJournalPoJo();
		scJournal.setSccode(scCode);
		
		scJournal.setInterval(intervalPoJo.getInterval());
		scJournal.setValm(intervalPoJo.getValm());
		scJournal.setVald(intervalPoJo.getVald());
		return scJournal;
	}
	
	@Override
	public void uploadImage(
	        MultipartFile file
	)
	{
		Byte[] byteObjects = null;
		
		try
		{
			//Get byte length of file
			byteObjects = new Byte[file.getBytes().length];
			
			int i = 0;
			
			for (byte b : file.getBytes())
			{
				byteObjects[i++] = b;
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scJournal.setImage(byteObjects);
		
	}
	
	@Override
	public ScripJournal saveJournal(
	)
	{
		ScripJournal sj = null;
		if (this.scJournal != null)
		{
			//Either of Notes or Image Maintained
			if (scJournal.getNotes() != null || scJournal.getImage() != null)
			{
				ScripJournal scJ = new ScripJournal();
				
				scJ.setCategory(scJournal.getCategory());
				scJ.setEffect(scJournal.getEffect());
				scJ.setImage(scJournal.getImage());
				scJ.setInterval(scJournal.getInterval());
				scJ.setValm(scJournal.getValm());
				scJ.setVald(scJournal.getVald());
				scJ.setNotes(scJournal.getNotes());
				scJ.setSccode(scJournal.getSccode());
				scJ.setSource(scJournal.getSource());
				scJ.setTag(scJournal.getTag());
				
				scJ.setUrl(scJournal.getUrl());
				scJ.setDateofentry(new java.sql.Date(System.currentTimeMillis()));
				
				sj = repoScJournal.save(scJ);
			}
			
		}
		
		return sj;
	}
	
	@Override
	public void clearBuffer(
	)
	{
		this.scJournal = null;
		this.scJEdit   = null;
		
	}
	
	@Override
	public void refreshScJournal(
	        ScripJournalPoJo scJP
	) throws Exception
	{
		//Refresh Everything but Scrip Code and Image in Buffer Attribute
		
		if (scJP != null && scJournal != null)
		{
			
			validateSCJPojo(scJP);
			
			if (scJP.getCatgOther().trim().length() > 0)
			{
				scJournal.setCategory(scJP.getCatgOther());
			} else
			{
				scJournal.setCategory(scJP.getCategory());
			}
			
			scJournal.setEffect(scJP.getEffect());
			
			scJournal.setInterval(scJP.getInterval());
			if (scJournal.getInterval() != EnumInterval.NA)
			{
				scJournal.setValm(scJP.getValm());
				scJournal.setVald(scJP.getVald());
				
			}
			
			if (scJP.getTagOther().trim().length() > 0)
			{
				if (scJP.getTag() != null)
				{
					scJournal.setTag(scJP.getTag() + "," + scJP.getTagOther());
				} else
				{
					scJournal.setTag(scJP.getTagOther());
				}
			} else
			{
				scJournal.setTag(scJP.getTag());
			}
			
			formatTags();
			
			scJournal.setNotes(scJP.getNotes());
			
			scJournal.setSource(scJP.getSource());
			scJournal.setUrl(scJP.getUrl());
		}
		
	}
	
	@Override
	public NumandLastEntry getQStats_New_ByScrip(
	        String scCode
	)
	{
		NumandLastEntry snippet = null;
		
		if (repoScJournal != null && scCode != null)
		{
			List<Object[]> vals = repoScJournal.findByAsArray(scCode);
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					
					snippet = new NumandLastEntry();
					snippet.setLastEntryDate((Date) vals.get(0)[0]);
					snippet.setNumEntries((Long) vals.get(0)[1]);
				}
			}
			
		}
		
		return snippet;
	}
	
	@Override
	public List<PlaceHolderLongPJ> getTagDetailsOvw(
	        String scCode
	)
	{
		
		List<String>            tagsInd    = new ArrayList<String>();
		List<String>            tagsUnique = null;
		List<PlaceHolderLongPJ> tagDetails = null;
		
		//1. Get the Tags as Such First from DB for the Scrip
		
		List<Tag> tagsDB = repoScJournal.findAllBySccode(scCode);
		if (tagsDB != null)
		{
			//2. Get Tags Names separating the comma from each tag
			for (Tag tag : tagsDB)
			{
				String[] arrOfTags = tag.getTag().split("\\s*,\\s*");
				
				for (String tagVal : arrOfTags)
				{
					tagsInd.add(tagVal);
				}
			}
			
			//3. Collect Unique Tags in a List
			tagsUnique = tagsInd.stream().distinct().collect(Collectors.toList());
			
			if (tagsUnique != null)
			{
				//Get Journal Entries that contain each tag in Loop
				
				for (String uqtag : tagsUnique)
				{
					List<SCJID> scJournalsFound = repoScJournal.findAllBySccodeAndTagContaining(scCode, uqtag);
					if (scJournalsFound != null)
					{
						PlaceHolderLongPJ phL = new PlaceHolderLongPJ(uqtag, (long) scJournalsFound.size());
						
						if (tagDetails == null)
						{
							tagDetails = new ArrayList<PlaceHolderLongPJ>();
						}
						tagDetails.add(phL);
					}
				}
			}
		}
		
		return tagDetails;
	}
	
	@Override
	public List<String> getUniqueCatgsforJournals(
	        List<ScripJournal> je
	)
	{
		List<String> catgs = null;
		
		if (je != null)
		{
			if (je.size() > 0)
			{
				catgs = je.stream().map(x -> x.getCategory()).distinct().collect(Collectors.toList());
			}
		}
		
		return catgs;
	}
	
	@Override
	public List<String> getUniqueTagsforscCode(
	        String scCode
	)
	{
		List<String> tagsInd    = new ArrayList<String>();
		List<String> tagsUnique = null;
		
		//1. Get the Tags as Such First from DB for the Scrip
		
		List<Tag> tagsDB = repoScJournal.findAllBySccode(scCode);
		if (tagsDB != null)
		{
			//2. Get Tags Names separating the comma from each tag
			for (Tag tag : tagsDB)
			{
				String[] arrOfTags = tag.getTag().split("\\s*,\\s*");
				for (String tagVal : arrOfTags)
				{
					tagsInd.add(tagVal);
				}
			}
			
			//3. Collect Unique Tags in a List
			tagsUnique = tagsInd.stream().distinct().collect(Collectors.toList());
			
		}
		
		return tagsUnique;
	}
	
	@Override
	public List<String> getUniqueCatgsforscCode(
	        String scCode
	)
	{
		
		return repoScJournal.findCatgsBySccode(scCode);
	}
	
	@Override
	//Param Obj to be passed via POST call in controller
	public void saveEditSCJ(
	        ScJEdit scJEdit
	) throws Exception
	{
		if (scJEdit != null)
		{
			//save in buffer for easier reload before validation
			this.scJEdit = scJEdit;
			ScripJournal je = null;
			
			validate_DuplicateTags(); //Throw an Error for Duplicate Tag is any found
			
			//Get the Journal, format the tags , update Notes and Save
			
			Optional<ScripJournal> scJ = repoScJournal.findById(scJEdit.getId());
			if (scJ.isPresent())
			{
				je = scJ.get();
				//Combine the Tags
				if (scJEdit.getTagOther().trim().length() > 0)
				{
					scJEdit.setTag(scJEdit.getTag() + "," + scJEdit.getTagOther());
				}
				
				//format tags
				if (scJEdit.getTag() != null)
				{
					String[] arrOfTags    = scJEdit.getTag().split(",");
					String   tagFormatted = null;
					if (arrOfTags != null)
					{
						tagFormatted = arrOfTags[0];
						for (int i = 1; i < arrOfTags.length; i++)
						{
							tagFormatted = tagFormatted + ", " + arrOfTags[i];
						}
					}
					
					if (tagFormatted != null)
					{
						je.setTag(je.getTag() + ", " + tagFormatted);
					}
				}
				je.setNotes(scJEdit.getNotes());
				je.setDateofentry(new java.sql.Date(System.currentTimeMillis()));
				
				repoScJournal.save(je);
				clearBuffer();
				
			}
			
		}
		
	}
	
	/*
	 *  -------------------------------------------------------------------------
	 *      PRIVATE SECTION
	 *  -------------------------------------------------------------------------
	 */
	
	private void validate_DuplicateTags(
	) throws Exception
	{
		
		//Get Existing Tags
		String[] arrOfTagsPrev  = scJEdit.getPrevTags().split("\\s*,\\s*");
		String[] arrOfTagsCurr1 = null;
		String[] arrOfTagsCurr2 = null;
		
		if (scJEdit.getTag() != null)
		{
			//Get Current Tags - Predefined 
			arrOfTagsCurr1 = scJEdit.getTag().split("\\s*,\\s*");
		}
		
		if (scJEdit.getTagOther() != null)
		{
			//Get Current Tags - Custom
			arrOfTagsCurr2 = scJEdit.getTagOther().split("\\s*,\\s*");
		}
		List<String> prevTags = Arrays.asList(arrOfTagsPrev);
		
		//checking Through Predefined Tags
		if (arrOfTagsCurr1 != null)
		{
			for (String string : arrOfTagsCurr1)
			{
				Optional<String> duplTag = prevTags.stream().filter(x -> x.equals(string)).findFirst();
				if (duplTag.isPresent())
				{
					throw new Exception(msgSrc.getMessage("DUPLICATE_TAG", new Object[]
					{ duplTag.get() }, Locale.ENGLISH));
				}
			}
		}
		
		//checking Through Custom Tags
		if (arrOfTagsCurr2 != null)
		{
			for (String string : arrOfTagsCurr2)
			{
				Optional<String> duplTag = prevTags.stream().filter(x -> x.equals(string)).findFirst();
				if (duplTag.isPresent())
				{
					throw new Exception(msgSrc.getMessage("DUPLICATE_TAG", new Object[]
					{ duplTag.get() }, Locale.ENGLISH));
				}
			}
		}
	}
	
	private void validateSCJPojo(
	        ScripJournalPoJo scJP
	) throws Exception
	{
		if (scJP != null)
		{
			if ((scJP.getCategory() != null && scJP.getCatgOther() != null))
			
			{
				if (scJP.getCategory().trim().length() > 0 && scJP.getCatgOther().trim().length() > 0)
				{
					throw new Exception(
					        catg_error);
				}
			}
			
			if (
			    (scJP.getCategory() == null && scJP.getCatgOther().trim().length() == 0)
			            || (scJP.getTag() == null && scJP.getTagOther().trim().length() == 0)
			)
			{
				throw new Exception(
				        catg_tag_notfound);
			}
			
			if ((scJP.getImage() == null) && (scJP.getNotes().trim().length() == 0))
			{
				throw new Exception(
				        img_notes_notfound);
			}
			
		}
	}
	
	/*
	 * Format Tags before SAve
	 */
	private void formatTags(
	)
	{
		String tagFormatted = new String();
		if (scJournal.getTag() != null)
		{
			//Currently tags will be comma separated - Format to ' ,' from ','
			List<String> tagsInd = new ArrayList<String>();
			
			//2. Get Tags Names separating the comma from each tag
			
			String[] arrOfTags = scJournal.getTag().split(",");
			if (arrOfTags != null)
			{
				tagFormatted = arrOfTags[0];
				for (int i = 1; i < arrOfTags.length; i++)
				{
					tagFormatted = tagFormatted + ", " + arrOfTags[i];
				}
			}
			
			scJournal.setTag(tagFormatted);
		}
	}
	
}
