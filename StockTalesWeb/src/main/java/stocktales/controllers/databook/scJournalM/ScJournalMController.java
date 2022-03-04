package stocktales.controllers.databook.scJournalM;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumSource;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.model.entity.scJournalM.ScripJournalM;
import stocktales.dataBook.model.entity.scJournalM.Snippet;
import stocktales.dataBook.model.pojo.EditSnippet;
import stocktales.dataBook.model.pojo.ScripJournalPoJoM;
import stocktales.dataBook.model.repo.scJournalM.RepoScJournalM;
import stocktales.dataBook.model.repo.scJournalM.RepoSnippet;
import stocktales.dataBook.scjournalM.intf.ISCJournalSrvM;
import stocktales.utilities.DurationUtilities;

@Controller
@RequestMapping("/scJournalM")
public class ScJournalMController
{
	@Autowired
	private ISCJournalSrvM scJSrv;
	
	@Autowired
	private RepoScJournalM repoScJ;
	
	@Autowired
	private RepoSnippet repoSnippet;
	
	@GetMapping("/new/{scCode}")
	public String shownewJournal(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
			model.addAttribute("scJP", scJSrv.createNewJournalforScrip(scCode));
			model.addAttribute("catgs", scJSrv.getUniqueCatgsforscCode(scCode));
			model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scCode));
		}
		return "scrips/dataBook/scJournalM/newScJournal";
	}
	
	@GetMapping("/newSnippet")
	public String showAddNewSnippet(
	        Model model
	)
	{
		if (scJSrv.getScripJournal() != null)
		{
			List<String> tags = scJSrv.getUniqueTagsforscCode(scJSrv.getScripJournal().getSccode());
			if (tags != null)
			{
				if (tags.size() == 0)
				{
					tags = scJSrv.getTagsPlainList();
				}
			}
			
			model.addAttribute("scCode", scJSrv.getScripJournal().getSccode());
			model.addAttribute("catg", scJSrv.getScripJournal().getCategory());
			model.addAttribute("snippet", scJSrv.addSnippet());
			model.addAttribute("tags", tags);
			
		}
		return "scrips/dataBook/scJournalM/newSnippet";
	}
	
	@GetMapping("/snippet/imgupload")
	public String showImgUpload(
	)
	{
		return "scrips/dataBook/scJournalM/Imageupload";
	}
	
	@GetMapping("/editsnippet/image")
	public void showEditSnippetImg(
	        HttpServletResponse response
	) throws IOException
	{
		if (scJSrv.getEditSnippet().getImage() != null)
		{
			byte[] byteArray = new byte[scJSrv.getEditSnippet().getImage().length];
			int    i         = 0;
			
			for (Byte wrappedByte : scJSrv.getEditSnippet().getImage())
			{
				byteArray[i++] = wrappedByte; //auto unboxing
			}
			
			//Images
			response.setContentType("image/jpeg");
			
			InputStream is = new ByteArrayInputStream(byteArray);
			IOUtils.copy(is, response.getOutputStream());
			
		}
	}
	
	@GetMapping("/image/{sid}")
	public void showSnippetImg(
	        @PathVariable("sid") String sid, HttpServletResponse response
	) throws IOException
	{
		if (sid != null)
		{
			Long snippetId = new Long(sid);
			if (snippetId > 0)
			{
				Optional<Snippet> snippetO = repoSnippet.findById(snippetId);
				if (snippetO.isPresent())
				{
					Snippet snippet = snippetO.get();
					if (snippet.getImage() != null)
					{
						byte[] byteArray = new byte[snippet.getImage().length];
						int    i         = 0;
						
						for (Byte wrappedByte : snippet.getImage())
						{
							byteArray[i++] = wrappedByte; //auto unboxing
						}
						
						//Images
						response.setContentType("image/jpeg");
						
						InputStream is = new ByteArrayInputStream(byteArray);
						IOUtils.copy(is, response.getOutputStream());
						
					}
				}
			}
		}
		
	}
	
	@GetMapping("/snippet/imageBuff/{sid}")
	public void showEditSnippetImg(
	        @PathVariable("sid") String sid, HttpServletResponse response
	) throws IOException
	{
		if (sid != null)
		{
			long snippetId = new Long(sid);
			if (snippetId >= 0)
			{
				Optional<Snippet> snippetO = this.scJSrv.getScripJournal().getSnippets().stream()
				        .filter(x -> x.getSid() == snippetId).findFirst();
				if (snippetO.isPresent())
				{
					if (snippetO.get().getImage() != null)
					{
						byte[] byteArray = new byte[snippetO.get().getImage().length];
						int    i         = 0;
						
						for (Byte wrappedByte : snippetO.get().getImage())
						{
							byteArray[i++] = wrappedByte; //auto unboxing
						}
						
						//Images
						response.setContentType("image/jpeg");
						
						InputStream is = new ByteArrayInputStream(byteArray);
						IOUtils.copy(is, response.getOutputStream());
						
					}
				}
			}
		}
	}
	
	@GetMapping("/editBuff/{sid}")
	public String showEditSnippetfromBuffer(
	        @PathVariable("sid") String sid, Model model
	)
	{
		if (sid != null)
		{
			long snippetId = new Long(sid);
			
			if (snippetId > 0) //Ones loaded in buffer can only be edited here
			{
				
				Snippet snippetSel = scJSrv.getSnippetfromBuffer(snippetId);
				if (snippetSel != null)
				{
					
					List<String> tags = scJSrv.getUniqueTagsforscCode(scJSrv.getScripJournal().getSccode());
					if (tags != null)
					{
						if (tags.size() == 0)
						{
							tags = scJSrv.getTagsPlainList();
						}
					}
					model.addAttribute("scCode", scJSrv.getScripJournal().getSccode());
					model.addAttribute("catg", scJSrv.getScripJournal().getCategory());
					model.addAttribute("snippet", scJSrv.setEditSnippetfromSnippet(snippetSel));
					model.addAttribute("tags", tags);
				}
				
			}
		}
		
		return "scrips/dataBook/scJournalM/newSnippet";
	}
	
	@GetMapping("/details/{scCode}")
	public String showJournalDetailsByScCode(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeOrderByDateofentryDesc(scCode);
			
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("stats", scJSrv.getQStats_New_ByScrip(scCode));
				
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal";
	}
	
	@GetMapping("woTags/{scCode}")
	public String showJournalDetailsByScCodewoTags(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeOrderByDateofentryAsc(scCode);
			
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("stats", scJSrv.getQStats_New_ByScrip(scCode));
				
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal_wo_tags";
	}
	
	@GetMapping("/pf/{scCode}")
	public String showJournalDetailsByScCodePrinterFriendly(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeOrderByDateofentryAsc(scCode);
			
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("stats", scJSrv.getQStats_New_ByScrip(scCode));
				
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournalPF";
	}
	
	@GetMapping("/save")
	public String saveSCJournalfromBuffer(
	        Model model
	)
	{
		String scCode = scJSrv.getScripJournal().getSccode();
		scJSrv.saveNewJournal();
		return "redirect:/scJournalM/details/" + scCode;
	}
	
	@GetMapping("/edit/{sid}")
	public String showEditSnippet(
	        @PathVariable("sid") String sid, Model model
	)
	{
		
		if (sid != null)
		{
			long snippetId = new Long(sid);
			
			if (snippetId > 0)
			{
				Optional<Snippet> snippetO = repoSnippet.findById(snippetId);
				if (snippetO.isPresent())
				{
					Snippet snippet = snippetO.get();
					if (snippet != null)
					{
						
						model.addAttribute("snippet", snippet);
					}
				}
			}
		}
		return "scrips/dataBook/scJournalM/editNotes";
		
	}
	
	@GetMapping("/ovw/{scCode}")
	public String showOvwJournal(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			
			try
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("snippet", scJSrv.getQStats_New_ByScrip(scCode));
				model.addAttribute("summary", repoScJ.getSummaryByScCode(scCode).get(0));
				model.addAttribute("catgClass", repoScJ.countEntriesByCategory(scCode));
				model.addAttribute("srcClass", repoScJ.countEntriesBySource(scCode));
				model.addAttribute("tagDetails", scJSrv.getTagDetailsOvw(scCode));
				model.addAttribute("effectClass", repoScJ.countEntriesByEffect(scCode));
			} catch (Exception e)
			{
				return "redirect:/scJournalM/new/" + scCode;
			}
		}
		
		return "scrips/dataBook/scJournalM/scJOvw";
	}
	
	@GetMapping("/detailsByCatg/{scCode}/{category}")
	public String showJournalDetailsByCatg(
	        @PathVariable("scCode") String scCode, @PathVariable("category") String catg, Model model
	)
	{
		if (scCode != null && catg != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeAndCategoryOrderByDateofentryDesc(scCode, catg);
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Category - " + catg, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal";
	}
	
	@GetMapping("/detailsByTag/{scCode}/{tag}")
	public String showJournalDetailsByTag(
	        @PathVariable("scCode") String scCode, @PathVariable("tag") String tag, Model model
	)
	{
		if (scCode != null && tag != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeAndTagContainingOrderByDateofentryDesc(scCode, tag);
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Tag Contains - " + tag, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal";
	}
	
	@GetMapping("/detailsBySource/{scCode}/{source}")
	public String showJournalDetailsBySource(
	        @PathVariable("scCode") String scCode, @PathVariable("source") String source, Model model
	)
	{
		if (scCode != null && source != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeAndSourceOrderByDateofentryDesc(scCode,
			        EnumSource.valueOf(source));
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Source - " + source, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal";
	}
	
	@GetMapping("/detailsByEffect/{scCode}/{effect}")
	public String showJournalDetailsByEffect(
	        @PathVariable("scCode") String scCode, @PathVariable("effect") String effect, Model model
	)
	{
		if (scCode != null && effect != null)
		{
			List<ScripJournalM> je = repoScJ.findAllBySccodeAndEffectOrderByDateofentryDesc(scCode,
			        EnumEffect.valueOf(effect));
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Effect - " + effect, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournalM/detailsScJournal";
	}
	
	/*
	 *   ---------------------   POST MAPPINGS -------------------------
	 */
	/*
	 * Initialize after New ScJournal PoJO Indentification details are filled in 
	 */
	@PostMapping("/initialize")
	public String showInitialize(
	        @ModelAttribute("scJP") ScripJournalPoJoM scJPM, Model model
	)
	{
		String desUrl = "scrips/dataBook/scJournalM/scJMain";
		if (scJPM != null)
		{
			try
			{
				model.addAttribute("scJP", scJSrv.initialize(scJPM));
				model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scJPM.getSccode()));
			} catch (Exception e)
			{
				model.addAttribute("years", DurationUtilities.getYearsList(-5));
				model.addAttribute("scJP", scJSrv.getScJPoJoM());
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforscCode(scJPM.getSccode()));
				model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scJPM.getSccode()));
				model.addAttribute("formError", e.getMessage());
				desUrl = "scrips/dataBook/scJournalM/newScJournal";
			}
			
		}
		
		return desUrl;
	}
	
	/*
	 * Process Snippet Image UPload
	 */
	@PostMapping("/snippet/procimgupload")
	public String handleImagePost(
	        
	        @RequestParam("file") MultipartFile file, Model model
	)
	{
		if (file != null)
		{
			
			scJSrv.uploadImage(file);
			
			model.addAttribute("scCode", scJSrv.getScripJournal().getSccode());
			model.addAttribute("catg", scJSrv.getScripJournal().getCategory());
			model.addAttribute("snippet", scJSrv.getEditSnippet());
			model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scJSrv.getScripJournal().getSccode()));
			
		}
		return "scrips/dataBook/scJournalM/newSnippet";
		
	}
	
	/*
	 * Process Snippet Submission and subsequent Addition to ScripJournalM
	 */
	
	@PostMapping("/confirmSnippet")
	public String processSnippetConfirm(
	        @ModelAttribute("snippet") EditSnippet snippet, Model model
	)
	{
		String desUrl = "scrips/dataBook/scJournalM/scJMain";
		
		if (snippet != null)
		{
			try
			{
				scJSrv.validateCleanseAddSnippet(snippet);
				model.addAttribute("scJP", scJSrv.getScripJournal());
				model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scJSrv.getScripJournal().getSccode()));
			} catch (Exception e)
			{
				model.addAttribute("fromError", e.getMessage());
				model.addAttribute("scCode", scJSrv.getScripJournal().getSccode());
				model.addAttribute("catg", scJSrv.getScripJournal().getCategory());
				model.addAttribute("snippet", scJSrv.getEditSnippet());
				model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scJSrv.getScripJournal().getSccode()));
				desUrl = "scrips/dataBook/scJournalM/newSnippet";
			}
		}
		
		return desUrl;
	}
	
	@PostMapping("/editSnippetNotes")
	public String processeditNotes(
	        @ModelAttribute("snippet") Snippet snippet, Model model
	)
	{
		String scCode = null;
		if (snippet != null)
		{
			if (snippet.getScripJorunal() != null)
			{
				scCode = snippet.getScripJorunal().getSccode();
				
				Optional<Snippet> snippetO = repoSnippet.findById(snippet.getSid());
				if (snippetO.isPresent())
				{
					Snippet snippetDB = snippetO.get();
					if (snippetDB != null)
					{
						snippetDB.setNotes(snippet.getNotes());
						repoSnippet.save(snippetDB);
					}
				}
				
			}
		}
		return "redirect:/scJournalM/details/" + scCode;
	}
}
