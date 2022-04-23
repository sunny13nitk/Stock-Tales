package stocktales.IDS.srv.intf;

import stocktales.IDS.pojo.UI.IDSOverAllocList;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.IDS_PFTxn_UI;
import stocktales.IDS.pojo.UI.IDS_PF_OverAllocsContainer;
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

	/**
	 * Clears and refills the IDS_PF_OverAllocsContainer - overAllocsContainer in
	 * IDS_PFDashBoardUISrv Session Bean instance of PFDBContainer
	 * 
	 * Select and Units sell are editable
	 */
	public IDS_PF_OverAllocsContainer fetchOverAllocations();

	/**
	 * REfresh Over Allocations Container as per User Inputs
	 * 
	 * @param viewList - IDSOverAllocList
	 * @return - IDS_PF_OverAllocsContainer
	 */
	public IDS_PF_OverAllocsContainer refreshOverAllocationsPL(IDSOverAllocList viewList);

	/**
	 * Commit The Over Allocation List in @Transactional Context to Commit
	 * 
	 * @param viewList - IDSOverAllocList
	 * @throws Exception
	 *
	 */
	public void commitOverAllocationsSells(IDSOverAllocList viewList) throws Exception;

	public boolean areOverAllocationsPresent();

	public void refreshContainer4SchemaChange() throws Exception;

	public void refreshContainer4RoundTrip() throws Exception;

	public void refreshContainer4RoundTrip(IDS_BuyProposalBO buyPBO) throws Exception;

	public void refreshContainer4Txn() throws Exception;

	public void refreshSchemaPostTxn() throws Exception;

	/**
	 * Process UI Adhoc Transaction that might include Buy/Sell/Dividend/Split/Bonus
	 * 
	 * @param pfTxnUI: UI Container for Txn. Commit Filled In
	 * @throws Exception
	 */
	public void processUIPFTxn(IDS_PFTxn_UI pfTxnUI) throws Exception;

}
