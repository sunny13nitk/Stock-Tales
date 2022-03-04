package stocktales.rest.cagr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stocktales.cagrEval.helperPoJo.CAGRResult;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;
import stocktales.cagrEval.intf.ICAGRCalcSrv;

@RestController
@RequestMapping("/cagr")
public class CAGRCalculatorRestController
{
	@Autowired
	private ICAGRCalcSrv cagrSrv;
	
	@GetMapping("/{stgyId}/{from}/{intv}/{length}")
	public List<CAGRResult> getCAGRforStrategy(
	        @PathVariable int stgyId, @PathVariable int from, @PathVariable int intv, @PathVariable int length,
	        @PathVariable boolean toLastUpdate
	)
	{
		List<CAGRResult> results = null;
		
		if (stgyId > 0)
		{
			try
			{
				cagrSrv.Initialize(stgyId, false);
				cagrSrv.calculateCAGR(new RollOverDurationsParam(from, intv, length, toLastUpdate
				));
				results = cagrSrv.getCagrResults();
				
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	@GetMapping("/{stgyId}")
	public List<CAGRResult> getCAGRforStgy(
	        @PathVariable String stgyId
	)
	{
		List<CAGRResult> results = null;
		int              stID    = new Integer(stgyId);
		if (stID > 0)
		{
			try
			{
				cagrSrv.Initialize(stID, false);
				cagrSrv.calculateCAGR(new RollOverDurationsParam(2010, 5, 10, true));
				results = cagrSrv.getCagrResults();
				
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
}
