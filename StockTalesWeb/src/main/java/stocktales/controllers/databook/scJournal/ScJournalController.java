package stocktales.controllers.databook.scJournal;

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
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJImage;
import stocktales.dataBook.helperPojo.scjournal.edit.ScJEdit;
import stocktales.dataBook.model.entity.scjournal.ScripJournal;
import stocktales.dataBook.model.pojo.ScripJournalPoJo;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.dataBook.scjournal.intf.IScJournalSrv;
import stocktales.utilities.DurationUtilities;

@Controller
@RequestMapping("/scJournal")
public class ScJournalController
{
	
	@Autowired
	private IScJournalSrv scJSrv;
	
	@Autowired
	private RepoScJournal repoScJ;
	
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
			model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scCode));
			model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scCode));
		}
		return "scrips/dataBook/scJournal/newScJournal";
	}
	
	@GetMapping("/load")
	public String showloadJournal(
	        Model model
	)
	{
		
		{
			model.addAttribute("years", DurationUtilities.getYearsList(-5));
			model.addAttribute("scJP", scJSrv.getScJournal());
			model.addAttribute("catgs", scJSrv.getUniqueCatgsforscCode(scJSrv.getScJournal().getSccode()));
			model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scJSrv.getScJournal().getSccode()));
			
			model.addAttribute("numLast", null);
		}
		return "scrips/dataBook/scJournal/newScJournal";
	}
	
	@GetMapping("/image")
	public void renderImageFromDB(
	        HttpServletResponse response
	) throws IOException
	{
		
		ScripJournalPoJo scJPojo = scJSrv.getScJournal();
		
		if (scJPojo.getImage() != null)
		{
			byte[] byteArray = new byte[scJPojo.getImage().length];
			int    i         = 0;
			
			for (Byte wrappedByte : scJPojo.getImage())
			{
				byteArray[i++] = wrappedByte; //auto unboxing
			}
			
			//Images
			response.setContentType("image/jpeg");
			
			InputStream is = new ByteArrayInputStream(byteArray);
			IOUtils.copy(is, response.getOutputStream());
			
		}
	}
	
	@GetMapping("/image/{jeid}")
	public void showImageforJournal(
	        @PathVariable("jeid") String jeid, HttpServletResponse response
	) throws IOException
	{
		if (jeid != null)
		{
			SCJImage img = repoScJ.getImageforJournalId(new Long(jeid));
			if (img != null)
			{
				byte[] byteArray = new byte[img.getImage().length];
				int    i         = 0;
				
				for (Byte wrappedByte : img.getImage())
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
	
	@GetMapping("/upload")
	public String uploadScrip(
	
	)
	{
		return "scrips/dataBook/scJournal/Imageupload";
	}
	
	@GetMapping("/edit/{id}")
	public String showEditJournal(
	        @PathVariable("id") String id, Model model
	)
	{
		if (id != null)
		{
			Long jid = new Long(id);
			if (jid > 0)
			{
				Optional<ScripJournal> scJO = repoScJ.findById(jid);
				if (scJO.isPresent())
				{
					ScripJournal scJ = scJO.get();
					
					ScJEdit scPJ = new ScJEdit();
					scPJ.setId(scJ.getId()); //Hidden in model
					scPJ.setScCode(scJ.getSccode()); //Hidden in model
					scPJ.setNotes(scJ.getNotes());
					scPJ.setPrevTags(scJ.getTag()); //Hidden in model
					
					model.addAttribute("je", scJ);
					model.addAttribute("scJP", scPJ);
					model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scPJ.getScCode()));
					
				}
			}
		}
		
		return "scrips/dataBook/scJournal/editScJournal";
	}
	
	@GetMapping("/ovw/{scCode}")
	public String showOvwJournal(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			model.addAttribute("scCode", scCode);
			model.addAttribute("snippet", scJSrv.getQStats_New_ByScrip(scCode));
			model.addAttribute("summary", repoScJ.getSummaryByScCode(scCode).get(0));
			model.addAttribute("catgClass", repoScJ.countEntriesByCategory(scCode));
			model.addAttribute("srcClass", repoScJ.countEntriesBySource(scCode));
			model.addAttribute("tagDetails", scJSrv.getTagDetailsOvw(scCode));
			model.addAttribute("effectClass", repoScJ.countEntriesByEffect(scCode));
		}
		
		return "scrips/dataBook/scJournal/scJOvw";
	}
	
	@GetMapping("/details/{scCode}")
	public String showJournalDetailsByScCode(
	        @PathVariable("scCode") String scCode, Model model
	)
	{
		if (scCode != null)
		{
			List<ScripJournal> je = repoScJ.findAllBySccodeOrderByDateofentryDesc(scCode);
			
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("snippet", scJSrv.getQStats_New_ByScrip(scCode));
				//Append Financial Sector Specific Data Notes
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournal/detailsScJournal";
	}
	
	@GetMapping("/detailsByCatg/{scCode}/{category}")
	public String showJournalDetailsByCatg(
	        @PathVariable("scCode") String scCode, @PathVariable("category") String catg, Model model
	)
	{
		if (scCode != null && catg != null)
		{
			List<ScripJournal> je = repoScJ.findAllBySccodeAndCategoryOrderByDateofentryDesc(scCode, catg);
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Category - " + catg, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournal/detailsScJournal";
	}
	
	@GetMapping("/detailsByTag/{scCode}/{tag}")
	public String showJournalDetailsByTag(
	        @PathVariable("scCode") String scCode, @PathVariable("tag") String tag, Model model
	)
	{
		if (scCode != null && tag != null)
		{
			List<ScripJournal> je = repoScJ.findAllBySccodeAndTagContainingOrderByDateofentryDesc(scCode, tag);
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Tag Contains - " + tag, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournal/detailsScJournal";
	}
	
	@GetMapping("/detailsBySource/{scCode}/{source}")
	public String showJournalDetailsBySource(
	        @PathVariable("scCode") String scCode, @PathVariable("source") String source, Model model
	)
	{
		if (scCode != null && source != null)
		{
			List<ScripJournal> je = repoScJ.findAllBySccodeAndSourceOrderByDateofentryDesc(scCode,
			        EnumSource.valueOf(source));
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Source - " + source, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournal/detailsScJournal";
	}
	
	@GetMapping("/detailsByEffect/{scCode}/{effect}")
	public String showJournalDetailsByEffect(
	        @PathVariable("scCode") String scCode, @PathVariable("effect") String effect, Model model
	)
	{
		if (scCode != null && effect != null)
		{
			List<ScripJournal> je = repoScJ.findAllBySccodeAndEffectOrderByDateofentryDesc(scCode,
			        EnumEffect.valueOf(effect));
			if (je != null)
			{
				model.addAttribute("scCode", scCode);
				model.addAttribute("filter", new PlaceHolderLongPJ("Effect - " + effect, (long) je.size()));
				model.addAttribute("jeList", je);
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforJournals(je));
			}
		}
		
		return "scrips/dataBook/scJournal/detailsScJournal";
	}
	
	@GetMapping("editReload/{jeid}")
	public String reloadEdit(
	        @PathVariable("jeid") String jeid, Model model
	)
	{
		
		return "scrips/dataBook/scJournal/editScJournal";
	}
	
	/*
	 * ----------------------------------------------------------------
	 *                  POST MAPPING
	 * ----------------------------------------------------------------                 
	 */
	
	@PostMapping("/upload")
	public String handleImagePost(
	        
	        @RequestParam("file") MultipartFile file, Model model
	)
	{
		if (file != null)
		{
			
			scJSrv.uploadImage(file);
			
		}
		return "redirect:/scJournal/load";
		
	}
	
	@PostMapping("/save")
	public String saveJournal(
	        @ModelAttribute("scJP") ScripJournalPoJo scJP, Model model
	)
	{
		
		String desurl = "scrips/dataBook/scJournal/newScJournal";
		
		if (scJP != null)
		{
			try
			{
				//Validate & REfresh Buffer From View Model
				scJSrv.refreshScJournal(scJP);
				
				scJSrv.saveJournal(); //SAve the Journal
				
				desurl = "redirect:/scJournal/details/" + scJSrv.getScJournal().getSccode();
				
				scJSrv.clearBuffer(); //Clear the buffer
			} catch (Exception e)
			{
				model.addAttribute("formError", e.getMessage());
				model.addAttribute("years", DurationUtilities.getYearsList(-5));
				model.addAttribute("scJP", scJSrv.getScJournal());
				model.addAttribute("catgs", scJSrv.getUniqueCatgsforscCode(scJSrv.getScJournal().getSccode()));
				model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scJSrv.getScJournal().getSccode()));
				model.addAttribute("numLast", scJSrv.getQStats_New_ByScrip(scJSrv.getScJournal().getSccode()));
				
			}
			
		}
		
		//Navigate to Details for Scrip Journal with an Option to Switch to Overview
		return desurl;
	}
	
	@PostMapping("/editSave")
	public String saveChangeinSCJ(
	        @ModelAttribute("scJP") ScJEdit scJP, Model model
	)
	{
		String desUrl = "scrips/dataBook/scJournal/editScJournal";
		if (scJP != null)
		{
			try
			{
				scJSrv.saveEditSCJ(scJP);
				//SAve and Navigate to Overview for Scrip Journals Details
				desUrl = "redirect:/scJournal/details/" + scJP.getScCode();
			} catch (Exception e)
			{
				//Reload Edit SCJ and populate Error Message - Reset Model
				Optional<ScripJournal> scJO = repoScJ.findById(scJP.getId());
				if (scJO.isPresent())
				{
					ScripJournal scJ = scJO.get();
					
					model.addAttribute("je", scJ);
					model.addAttribute("scJP", scJSrv.getScJEdit());
					model.addAttribute("tags", scJSrv.getUniqueTagsforscCode(scJP.getScCode()));
					model.addAttribute("formError", e.getMessage());
					
				}
				
			}
		}
		return desUrl;
	}
	
}
