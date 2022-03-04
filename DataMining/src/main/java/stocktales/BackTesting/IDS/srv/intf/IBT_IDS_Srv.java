package stocktales.BackTesting.IDS.srv.intf;

import stocktales.BackTesting.IDS.pojo.BT_EP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_IP_IDS;

public interface IBT_IDS_Srv
{
	/**
	 * BackTest IDS Deployments Strategy
	 * 
	 * @param ip_params - BT_IP_IDS { List of <Scrips, Allocations> , Lump Sum
	 *                  Amount, SIP Amount, SIP Frequency, Vol. Profile Refresh
	 *                  Frequency, Number of Years in Past to Begin the test}
	 * @return
	 * @throws Exception
	 */
	public BT_EP_IDS backTestIDS(BT_IP_IDS ip_params) throws Exception;
}
