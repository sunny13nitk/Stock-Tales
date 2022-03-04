package stocktales.dataBook.scjournalM.srv;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.enums.EnumSource;
import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJID;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.Tag;
import stocktales.dataBook.model.entity.scJournalM.ScripJournalM;
import stocktales.dataBook.model.entity.scJournalM.Snippet;
import stocktales.dataBook.model.pojo.EditSnippet;
import stocktales.dataBook.model.pojo.ScripJournalPoJoM;
import stocktales.dataBook.model.repo.scJournalM.RepoScJournalM;
import stocktales.dataBook.model.repo.scJournalM.RepoSnippet;
import stocktales.dataBook.scjournalM.intf.ISCJournalSrvM;
import stocktales.scripsEngine.utilities.types.CurrentFinancialPeriod;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScJournalSrvM implements ISCJournalSrvM
{
	
	private ScripJournalPoJoM scJPoJoM;
	
	private ScripJournalM scripJournal;
	
	private EditSnippet editSnippet;
	
	@Autowired
	private RepoScJournalM repoScJournal;
	
	@Autowired
	private RepoSnippet repoSnippet;
	
	@Value("${CATG_NOTFOUND}")
	private String catg_error;
	
	@Value("${CATG_TAG_NOTFOUND}")
	private String catg_tag_notfound;
	
	@Value("${IMG_NOTES_NOTFOUND}")
	private String img_notes_notfound;
	
	@Override
	public ScripJournalPoJoM createNewJournalforScrip(
	        String scCode
	)
	{
		this.clearBuffer();
		this.scJPoJoM = new ScripJournalPoJoM();
		scJPoJoM.setSccode(scCode);
		CurrentFinancialPeriod cfp = new CurrentFinancialPeriod();
		scJPoJoM.setValm(cfp.getYear());
		scJPoJoM.setVald(cfp.getQuarter());
		scJPoJoM.setInterval(EnumInterval.Quarterly);
		scJPoJoM.setEffect(EnumEffect.Information);
		scJPoJoM.setSource(EnumSource.Concall);
		
		return scJPoJoM;
	}
	
	@Override
	public ScripJournalPoJoM getScJPoJoM(
	)
	{
		
		return this.scJPoJoM;
	}
	
	@Override
	public ScripJournalM getScripJournal(
	)
	{
		return this.scripJournal;
	}
	
	@Override
	public void clearBuffer(
	)
	{
		this.scJPoJoM     = null;
		this.scripJournal = null;
		this.editSnippet  = null;
		
	}
	
	@Override
	public NumandLastEntry getQStats_New_ByScrip(
	        String scCode
	)
	{
		NumandLastEntry stats = null;
		
		if (repoScJournal != null && scCode != null)
		{
			List<Object[]> vals = repoScJournal.findByAsArray(scCode);
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					
					stats = new NumandLastEntry();
					stats.setLastEntryDate((Date) vals.get(0)[0]);
					stats.setNumEntries((Long) vals.get(0)[1]);
				}
			}
			
		}
		
		return stats;
	}
	
	@Override
	public List<PlaceHolderLongPJ> getTagDetailsOvw(
	        String scCode
	)
	{
		List<String>            tagsInd       = new ArrayList<String>();
		List<String>            tagsUnique    = null;
		List<PlaceHolderLongPJ> tagDetails    = null;
		List<PlaceHolderLongPJ> tagDetailsRet = null;
		
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
		tagDetailsRet = tagDetails.stream()
		        .sorted(Comparator.comparingLong(PlaceHolderLongPJ::getNumEntries).reversed())
		        .collect(Collectors.toList());
		return tagDetailsRet;
	}
	
	@Override
	public List<String> getUniqueCatgsforJournals(
	        List<ScripJournalM> je
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
	public EditSnippet addSnippet(
	)
	{
		clearSnippet();
		this.editSnippet = new EditSnippet();
		//Assign Value of Tag(s) from scPOJOM
		if (this.getScJPoJoM() != null)
		{
			editSnippet.setTag(this.getTagsPlain());
			editSnippet.setTagOther(scJPoJoM.getTagOther());
			
		}
		return editSnippet;
	}
	
	@Override
	public void clearSnippet(
	)
	{
		this.editSnippet = null;
		
	}
	
	@Override
	public ScripJournalM initialize(
	        ScripJournalPoJoM scPoJo
	) throws Exception
	{
		
		if (scPoJo != null)
		{
			this.scJPoJoM     = scPoJo;
			this.scripJournal = new ScripJournalM();
			
			if (scJPoJoM.getCategory() == null && scJPoJoM.getCatgOther().trim().length() == 0)
			{
				throw new Exception(
				        catg_error);
			}
			
			if (scJPoJoM.getCategory() != null)
			{
				scripJournal.setCategory(scJPoJoM.getCategory());
			} else if (scJPoJoM.getCatgOther().trim().length() > 0)
			{
				scripJournal.setCategory(scJPoJoM.getCatgOther());
			}
			
			scripJournal.setEffect(scJPoJoM.getEffect());
			scripJournal.setSccode(scJPoJoM.getSccode());
			scripJournal.setInterval(scJPoJoM.getInterval());
			scripJournal.setSource(scJPoJoM.getSource());
			
			//Interval Values Only makes Sense when Interval is Not NA
			if (scripJournal.getInterval() != EnumInterval.NA)
			{
				scripJournal.setVald(scJPoJoM.getVald());
				scripJournal.setValm(scJPoJoM.getValm());
			}
			
		}
		
		return this.scripJournal;
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
		
		editSnippet.setImage(byteObjects);
		
	}
	
	@Override
	public void validateCleanseAddSnippet(
	        EditSnippet snippet
	) throws Exception
	{
		if (snippet != null)
		{
			/*
			 * Logic placeholder to bifurcate Edit - new snippet case
			 * depending on sid = 0 or  >0
			 */
			if (snippet.getSid() > 0)
			{
				// change case- Route to change handler 
				processSnippetChange(snippet);
			} else
			{
				//REset Edit Snippet - In case Exception thrown to reload from this edit snippet
				this.editSnippet.setNotes(snippet.getNotes());
				this.editSnippet.setTag(snippet.getTag());
				this.editSnippet.setTagOther(snippet.getTagOther());
				this.editSnippet.setUrl(snippet.getUrl());
				
				if (editSnippet.getImage() == null && editSnippet.getNotes().trim().length() == 0)
				{
					throw new Exception(img_notes_notfound);
				}
				
				//Set the Tag for ScripJournalM
				this.scripJournal.setTag(this.getTagTobeSet(snippet));
				
				//Push Snippet to Scrip Journal and clear snippet as it is no longer needed
				Snippet newSnippet = new Snippet();
				
				/*-------------------------------------------------------------------------------
				 * Set Pseudo Id in the Buffer for Snippet ID - All to be set to zero before Saving
				 * A sperate Method for save new needs to be created- wherein all snippet ids of the 
				 * ScJournal need to be set to zero before saving
				 * -------------------------------------------------------------------------------
				 */
				long count = this.scripJournal.getSnippets().size() + 1;
				newSnippet.setSid(count);
				
				newSnippet.setImage(editSnippet.getImage());
				newSnippet.setNotes(editSnippet.getNotes());
				newSnippet.setUrl(editSnippet.getUrl());
				scripJournal.addsnippet(newSnippet);
				
			}
			clearSnippet();
		}
		
	}
	
	@Override
	public ScripJournalM saveNewJournal(
	)
	{
		ScripJournalM scJournal = null;
		/*
		 * all snippet ids of the ScJournal need to be set to zero before saving
		 */
		
		for (Snippet snippet : this.scripJournal.getSnippets())
		{
			snippet.setSid(0);
		}
		scripJournal.setDateofentry(new java.sql.Date(System.currentTimeMillis()));
		
		scJournal = repoScJournal.save(scripJournal);
		clearBuffer(); //Clear All buffer
		
		return scJournal;
	}
	
	public EditSnippet setEditSnippetfromSnippet(
	        Snippet snippet
	)
	{
		this.clearSnippet();
		
		//Move the Properties alongwith Sid & Tag
		EditSnippet edSnippet = new EditSnippet();
		
		//Set Properties
		edSnippet.setSid(snippet.getSid());
		edSnippet.setImage(snippet.getImage());
		edSnippet.setNotes(snippet.getNotes());
		edSnippet.setTag(this.getTagsPlain());
		edSnippet.setTagOther(new String()); //Blank Tag Other
		edSnippet.setUrl(snippet.getUrl());
		
		//Move to Buffer Edit Snippet
		this.editSnippet = edSnippet;
		
		return this.editSnippet;
	}
	
	@Override
	//Get Tags as plain Tags from Scrip Journal seperated by a single comma to be set in Edit Scenario
	public String getTagsPlain(
	)
	{
		String tagPlain = new String();
		
		if (scripJournal.getTag() != null)
		{
			String[] arrOfTagsPrev = scripJournal.getTag().split("\\s*,\\s*");
			tagPlain = arrOfTagsPrev[0];
			for (int i = 1; i < arrOfTagsPrev.length; i++)
			{
				tagPlain = tagPlain + ", " + arrOfTagsPrev[i];
			}
		}
		
		return tagPlain;
		
	}
	
	@Override
	public List<String> getTagsPlainList(
	)
	{
		List<String> tags = new ArrayList<String>();
		
		if (scripJournal.getTag() != null)
		{
			String[] arrOfTagsPrev = scripJournal.getTag().split("\\s*,\\s*");
			if (arrOfTagsPrev.length > 0)
			{
				tags = Arrays.asList(arrOfTagsPrev);
			}
		}
		
		return tags;
	}
	
	public Snippet getSnippetfromBuffer(
	        long sid
	)
	{
		Snippet snippet = null;
		
		Optional<Snippet> snipO = this.getScripJournal().getSnippets().stream().filter(x -> x.getSid() == sid)
		        .findFirst();
		if (snipO.isPresent())
		{
			snippet = snipO.get();
		}
		
		return snippet;
	}
	
	/*
	 *  -------------------------------------------------------------------------
	 *      PRIVATE SECTION
	 *  -------------------------------------------------------------------------
	 */
	
	private String getTagTobeSet(
	        EditSnippet snippet
	)
	{
		List<String> tagsUnique   = null;
		String       tagFormatted = "";
		List<String> listTags     = new ArrayList<String>();
		List<String> tagsInd      = new ArrayList<String>();
		List<String> tagsIndOther = new ArrayList<String>();
		List<String> tagsExis     = new ArrayList<String>();
		
		//Get Existing Tags in List
		if (this.getScripJournal().getTag() != null)
		{
			tagFormatted = this.getScripJournal().getTag();
			if (tagFormatted.length() > 0)
			{
				String[] arrOfTagsExis = tagFormatted.split("\\s*,\\s*");
				
				tagsExis = Arrays.asList(arrOfTagsExis);
				listTags.addAll(tagsExis);
			}
		}
		
		//Get Tags
		if (snippet.getTag() != null)
		{
			if (snippet.getTag().trim().length() > 0)
			{
				String[] arrOfTags = snippet.getTag().split(",");
				tagsInd = Arrays.asList(arrOfTags);
				listTags.addAll(tagsInd);
			}
		}
		
		//Get Other Tags
		if (snippet.getTagOther() != null)
		{
			if (snippet.getTagOther().trim().length() > 0)
			{
				String[] arrOfTags = snippet.getTagOther().split(",");
				
				tagsIndOther = Arrays.asList(arrOfTags);
				
				listTags.addAll(tagsIndOther);
				
			}
		}
		
		if (tagsInd.size() > 0 || tagsIndOther.size() > 0)
		{
			//Add All tags together - existing, new tags and Other Tags in single list
			
			tagsUnique = listTags.stream().distinct().collect(Collectors.toList());
			
			int i = 0;
			for (String tagU : tagsUnique)
			{
				if (i == 0)
				{
					tagFormatted = tagU;
				} else
				{
					tagFormatted = tagFormatted + ", " + tagU;
				}
				i++;
			}
		}
		
		return tagFormatted;
		
	}
	
	private void processSnippetChange(
	        EditSnippet edSnippet
	)
	{
		//Get the Snippet
		Snippet snippet = this.getSnippetfromBuffer(edSnippet.getSid());
		if (snippet != null)
		{
			snippet.setNotes(edSnippet.getNotes());
			scripJournal.setTag(this.getTagTobeSet(edSnippet));
			snippet.setUrl(edSnippet.getUrl());
		}
	}
	
}
