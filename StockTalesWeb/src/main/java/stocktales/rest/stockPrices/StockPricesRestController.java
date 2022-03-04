package stocktales.rest.stockPrices;

import java.util.Calendar;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stocktales.historicalPrices.pojo.StockCurrQuote;
import stocktales.historicalPrices.pojo.StockHistory;
import stocktales.historicalPrices.utility.StockPricesUtility;
import yahoofinance.histquotes.Interval;

@RestController
@RequestMapping("/scQuote")
public class StockPricesRestController
{
	@GetMapping("/listH")
	public List<StockHistory> showPriceHistoryforScrips(
	)
	{
		String[] stocks = new String[]
		{ "BAJFINANCE", "ALKYLAMINE", "LTI", "AFFLE" };
		
		List<StockHistory> scripsHistory = null;
		
		try
		{
			scripsHistory = StockPricesUtility.getHistoricalPricesforScrips(stocks, Calendar.MONTH, 3, Interval.DAILY);
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return scripsHistory;
	}
	
	@GetMapping("/currH")
	public List<StockCurrQuote> showPriceCurrentforScrips(
	)
	{
		String[] stocks = new String[]
		{ "BAJFINANCE", "ALKYLAMINE", "LTI", "AFFLE" };
		
		List<StockCurrQuote> scripsHistory = null;
		
		try
		{
			scripsHistory = StockPricesUtility.getCurrentPricesforScrips(stocks);
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return scripsHistory;
	}
}
