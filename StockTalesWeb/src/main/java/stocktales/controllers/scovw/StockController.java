package stocktales.controllers.scovw;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;

@Controller
@RequestMapping("/scOvw")
public class StockController
{
	@Autowired
	private IStockSnapshotSrv ssSrv;
	
	@GetMapping("/{scCode}")
	public String showScOvw(
	        @PathVariable String scCode, Model model
	)
	{
		if (ssSrv != null)
		{
			StockSnapshot ss;
			try
			{
				ss = ssSrv.getStockSnapshot(scCode);
				if (ss != null)
				{
					model.addAttribute("ss", ss);
					
					/**
					 * ------------ Charts Data Model Populate
					 */
					
					/*
					 * Individual Chart Data Objects Addition
					 */
					model.addAttribute("chartData2", ss.getTrends().getRevWCTrends());
					
					/*
					 * Trends General except TTM
					 */
					model.addAttribute("trendsGen", ss.getTrends().getTrendsGen());
					
					/*
					 * FCF per Units of PAT and Sales - Fundamentals
					 */
					if (ss.getFundamentals() != null)
					{
						Map<String, Double> fcfFundData = new TreeMap<>();
						fcfFundData.put("FCF/Rupee of PAT", ss.getFundamentals().getPATtoFCF());
						fcfFundData.put("FCF/Rupee of Revenue", ss.getFundamentals().getREVtoFCF());
						
						model.addAttribute("fcfFundData", fcfFundData);
					}
					/*
					 * Prices
					 */
					
					if (ss.getValuations() != null)
					{
						model.addAttribute("priceTrends", ss.getValuations());
					}
					if (ss.getTrends() != null)
					{
						model.addAttribute("PETrends", ss.getTrends().getPeTrends());
					}
					
					/*
					 * Working Capital Details
					 */
					if (ss.getWcDetails() != null)
					{
						model.addAttribute("wcDetails", ss.getWcDetails());
					}
					
					/*
					 * Balance Sheet Numbers - Last 10 yrs
					 */
					if (ss.getBalSheetData() != null)
					{
						model.addAttribute("bsGrNos", ss.getBalSheetData().getBalSheetGrData());
						model.addAttribute("bsEPSNos", ss.getBalSheetData().getEpsData());
						model.addAttribute("bsRatios", ss.getBalSheetData().getBalSheetRatios());
					}
					
				}
			} catch (Exception e)
			{
				model.addAttribute("formError", "Invalid Scrip Code ! - " + scCode + e.getMessage());
				return "Error";
			}
			
		}
		
		return "scsnapshot/scOvwTabs";
	}
	
}
