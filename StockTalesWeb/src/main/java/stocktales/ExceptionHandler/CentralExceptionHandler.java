package stocktales.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import stocktales.IDS.exceptions.CorePFException;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.srv.intf.IDS_PFSchema_REbalUI_Srv;
import stocktales.NFS.repo.RepoBseData;
import stocktales.exceptions.SchemaUpdateException;
import stocktales.exceptions.StockQuoteException;
import stocktales.services.intf.IVWManagerSrv;
import stocktales.strategy.helperPOJO.SectorAllocations;

@ControllerAdvice

public class CentralExceptionHandler
{

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private Environment environment;

	@Autowired
	private RepoBseData repoBseData;

	@Autowired
	private IDS_PFSchema_REbalUI_Srv pfSchRebalSrv;

	@Autowired
	private IVWManagerSrv vwMgrSrv;

	private final String prodProfile = "prod";

	private final String testProfile = "test";

	/**
	 * Handler for Stock Quote Exception - Invalid Sc Code or Connection Issue to
	 * Yahoo Finance
	 * 
	 */
	@ExceptionHandler(value = StockQuoteException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ModelAndView respondtoStockQuoteException(Exception e)
	{
		ModelAndView mv = new ModelAndView();

		mv.addObject("formError", e.getMessage());
		mv.setViewName("Error");
		return mv;
	}

	@ExceptionHandler(value = CorePFException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ModelAndView respondtoCorePFException(Exception e)
	{
		ModelAndView mv = new ModelAndView();

		if (vwMgrSrv != null)
		{
			if (vwMgrSrv.getViewName() != null)
			{
				mv.setViewName(vwMgrSrv.getViewName());
				vwMgrSrv.clearSession();
			}
		} else
		{
			mv.setViewName("Error");

		}

		mv.addObject("formError", e.getMessage());

		return mv;
	}

	@ExceptionHandler(value = SchemaUpdateException.class) // For Exception Class
	@ResponseStatus(value = HttpStatus.NOT_FOUND) // Clubbed as Http Status
	public ModelAndView handleAllocsTotalException(Exception e)
	{

		ModelAndView mv = new ModelAndView();
		if (vwMgrSrv != null)
		{
			if (vwMgrSrv.getViewName() != null)
			{
				mv.setViewName(vwMgrSrv.getViewName());
				vwMgrSrv.clearSession();

			}
		} else
		{
			mv.setViewName("Error");

		}

		mv.addObject("scrips", repoBseData.findAllNseCodes());
		mv.addObject("schmPOJO", pfSchRebalSrv.getRebalContainer());
		mv.addObject("formError", e.getMessage());

		/**
		 * Exception Handling by Active Profile - Test: the Complete Exception Trace for
		 * better troubleshooting - Prod : User verbiage generalized message
		 */
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(x -> x.equalsIgnoreCase(prodProfile)))
		{
			mv.addObject("formError", e.getMessage());
		} else if (Arrays.stream(environment.getActiveProfiles()).anyMatch(x -> x.equalsIgnoreCase(testProfile)))
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			mv.addObject("formError", sw.toString());
		}
		mv.addObject("formSucc", null); // Remove Success Message if prev validated succ.
		if (pfSchRebalSrv.getRebalContainer().getStats() != null)
		{

			if (pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs().size() > 0)
			{
				List<SectorAllocations> mCapchartData = new ArrayList<SectorAllocations>();
				for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getMCapAllocs())
				{
					mCapchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

				}

				mv.addObject("mCapData", mCapchartData);
			}

			if (pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs().size() > 0)
			{
				List<SectorAllocations> secchartData = new ArrayList<SectorAllocations>();
				for (SectorAllocations secAlloc : pfSchRebalSrv.getRebalContainer().getStats().getSecAllocs())
				{
					secchartData.add(new SectorAllocations(secAlloc.getSector(), secAlloc.getAlloc()));

				}

				mv.addObject("secData", secchartData);
			}

			mv.addObject("seriesval", pfSchRebalSrv.getRebalContainer().getStats().getDateVals());
		}
		return mv;
	}

}
