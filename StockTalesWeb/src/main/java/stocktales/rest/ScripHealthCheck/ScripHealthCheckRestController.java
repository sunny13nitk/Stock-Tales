package stocktales.rest.ScripHealthCheck;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCComboResult;

@RestController
@RequestMapping("/healthCheck")
public class ScripHealthCheckRestController
{
	@Autowired
	private IHC_Srv scripHCSrv;
	
	@GetMapping("/{scCode}")
	public List<HCComboResult> doScripHealthCheck(
	        @PathVariable String scCode
	)
	{
		List<HCComboResult> results = null;
		if (scCode != null)
		{
			if (scCode.trim().length() > 5)
			{
				try
				{
					scripHCSrv.Initialize(scCode);
					scripHCSrv.processScripHealthCheck();
					results = scripHCSrv.getResults();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return results;
	}
}
