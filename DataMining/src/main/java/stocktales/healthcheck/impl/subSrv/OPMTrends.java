package stocktales.healthcheck.impl.subSrv;

import java.util.List;

import org.springframework.stereotype.Service;

import stocktales.healthcheck.annotations.HealthCheckCriteria;
import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.intf.IHC_SubSrvRes;
import stocktales.healthcheck.model.helperpojo.HCSubSrvRes;

@Service
@HealthCheckCriteria(criteria = EnumHCCriteria.OperatingMargins)
public class OPMTrends implements IHC_SubSrvRes
{
	
	@Override
	public List<HCSubSrvRes> getSubServiceResults(
	        String scCode
	)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
