package stocktales.healthcheck.intf;

import java.util.List;

import stocktales.healthcheck.model.helperpojo.HCComboResult;

public interface IHC_Srv
{
	public void Initialize(
	        String scCode
	) throws Exception;
	
	public void processScripHealthCheck(
	);
	
	public List<HCComboResult> getResults(
	);
	
	public String getScCode(
	);
	
}
