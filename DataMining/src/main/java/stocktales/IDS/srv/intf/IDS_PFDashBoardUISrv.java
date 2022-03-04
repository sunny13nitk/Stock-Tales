package stocktales.IDS.srv.intf;

import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.PFDBContainer;

public interface IDS_PFDashBoardUISrv
{
	public PFDBContainer getPFDashBoardContainer() throws Exception;

	/**
	 * Get the DashBoard Container without Refresh - for Certain scenario like Mass
	 * Purchases etc.
	 * 
	 * @return
	 * @throws Exception
	 */
	public PFDBContainer getPFDashBoardContainer4mSession() throws Exception;

	public void refreshContainer4SchemaChange() throws Exception;

	public void refreshContainer4RoundTrip() throws Exception;

	public void refreshContainer4RoundTrip(IDS_BuyProposalBO buyPBO) throws Exception;

	public void refreshContainer4Txn() throws Exception;

}
