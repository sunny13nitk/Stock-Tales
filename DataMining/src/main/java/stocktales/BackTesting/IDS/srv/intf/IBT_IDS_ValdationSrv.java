package stocktales.BackTesting.IDS.srv.intf;

import stocktales.BackTesting.IDS.exceptions.IDSBackTestingException;
import stocktales.BackTesting.IDS.pojo.BT_AD_IDS;
import stocktales.BackTesting.IDS.pojo.BT_IP_IDS;

public interface IBT_IDS_ValdationSrv
{
	public BT_AD_IDS validateParams(BT_IP_IDS ip_params) throws IDSBackTestingException;
}
