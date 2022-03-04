package stocktales.IDS.srv.intf;

import stocktales.BackTesting.IDS.pojo.BT_PFSchema;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumVolatilityProfile;

public interface IDS_DeploymentAmntSrv
{
	public double getDeploymentAmountByScrip4mPF(String scCode, EnumSMABreach smaBreach);

	public double getDeploymentAmountByScrip4mPF(String scCode, EnumSMABreach smaBreach, BT_PFSchema pfSchema,
			EnumVolatilityProfile VP);
}
