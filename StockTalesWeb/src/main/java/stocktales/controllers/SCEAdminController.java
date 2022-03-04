package stocktales.controllers;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IXLS_Scrip_Upload_Srv;
import stocktales.siteconfig.interfaces.INavigationRedirectSrv;

@Controller
@Slf4j
@RequestMapping("/admin/sc")
public class SCEAdminController
{
	@Autowired
	private IXLS_Scrip_Upload_Srv scUploadSrv;

	@Autowired
	private INavigationRedirectSrv navSrv;

	private final String uploadScripForm = "scrips/upload";

	@GetMapping("/upload")
	public String uploadScrip(

	)
	{
		return uploadScripForm;
	}

	@GetMapping("/upload/{scCode}/{sitePath}")
	public String uploadScrip(@PathVariable("scCode") String scCode, @PathVariable("sitePath") String sitePath,
			Model model

	)
	{
		if (scCode != null && scCode.trim().length() > 3)
		{
			model.addAttribute("scCode", scCode);
		}

		if (sitePath != null && sitePath.trim().length() > 3)
		{
			if (navSrv != null)
			{
				navSrv.setPathTitle(sitePath); // Set Destination Path from Source HTML
				navSrv.setScCode(scCode);
			}
		}
		return "scrips/uploadForScrip";
	}

	@PostMapping("/upload")
	public String handleImagePost(

			@RequestParam("file") MultipartFile file, Model model)
	{
		if (file != null)
		{
			try
			{
				XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
				if (wb != null)
				{
					if (scUploadSrv.Upload_Scrip_from_XLS_WBCtxt(wb))
					{
						log.debug("Scrip Successfully Uploaded");
						// to be replaced with properties messages
						model.addAttribute("formSucc", "Scrip Successfully Uploaded!");
					}

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				// to be replaced with properties messages
				model.addAttribute("formError", e.getMessage());
			}
		}

		return uploadScripForm;
	}

	@PostMapping("/uploadScrip")
	public String handlescripUpload(

			@RequestParam("file") MultipartFile file, Model model)
	{
		if (file != null)
		{
			try
			{
				XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
				if (wb != null)
				{
					if (scUploadSrv.Upload_Scrip_from_XLS_WBCtxt(wb))
					{
						log.debug("Scrip Successfully Uploaded");
						// to be replaced with properties messages
						model.addAttribute("formSucc", "Scrip Successfully Uploaded!");

						// Raise the Event For Scrip Change Also here

						// Navigate to Source - Calling Url
						if (navSrv.getPathUrl() != null)
						{
							String tgtUrl = "redirect:" + navSrv.getPathUrl();
							navSrv.clear();
							return tgtUrl;
						}
					}

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				// to be replaced with properties messages
				model.addAttribute("formError", e.getMessage());
			}
		}

		return "scrips/uploadForScrip";
	}

}
