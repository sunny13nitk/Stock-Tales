package stocktales.healthcheck.intf;

import java.util.List;

import stocktales.healthcheck.model.helperpojo.HCSubSrvRes;

public interface IHC_SubSrvRes
{
	public List<HCSubSrvRes> getSubServiceResults(
	        String scCode
	);
}
