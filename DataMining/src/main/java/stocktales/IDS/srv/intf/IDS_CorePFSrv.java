package stocktales.IDS.srv.intf;

import java.util.List;

import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.IDS_SC_BonusIP;
import stocktales.IDS.pojo.IDS_SC_PL;
import stocktales.IDS.pojo.IDS_SC_SplitIP;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.pojo.XIRRContainer;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.IDS_PFTxn_UI;
import stocktales.IDS.pojo.UI.IDS_Scrip_Details;

public interface IDS_CorePFSrv
{
	/**
	 * REfresh Volatility Profile(s) of Core Portfolio
	 * 
	 * @throws Exception
	 */
	public List<IDS_VPDetails> refreshPFVolatilityProfiles() throws Exception;

	/**
	 * Process core PF Buy/Sell Txn : Dividends handled via Money Bag Srv
	 * 
	 * @param txn - HCI POJO
	 * @throws Exception
	 */
	public void processCorePFTxn(HCI txn) throws Exception;

	/**
	 * Get the SMA Preview for scrips in PFSchema
	 * 
	 * @return - PF Schema Scrips SMA preview
	 * @throws Exception
	 */
	public List<IDS_SMAPreview> getPFSchemaSMAPreview() throws Exception;

	/**
	 * Get the Buy Proposals- if any for The Core PF Scrips
	 * 
	 * @return - Basic Details of Buy Proposals (ScCode, Units, PPU) for Proposals
	 * @throws Exception
	 */
	public IDS_BuyProposalBO getBuyProposals() throws Exception;

	/**
	 * Auto Process & Commit the Buy Proposals- if any for The Core PF Scrips
	 * 
	 * @return - Basic Details of Buy Proposals (ScCode, Units, PPU) for Proposals
	 * 
	 * @throws Exception
	 */
	public List<IDS_SCBuyProposal> autoProcessBuyProposals() throws Exception;

	/**
	 * Push the List of PF Transaction(s) after conducting at Broker Platform
	 * 
	 * 1. Capture any deviations for each Scrip in terms of DEp Amnt and Curr Inv.
	 * 2. Update/Increment the Dep Amnts in Schema by deviations for scrips they
	 * occur 3. Perform the PF transactions 4. Decrement the Dep Amnts in Schema by
	 * deviations for scrips as captured in 1.
	 * 
	 * @param pfTxns - List<HCI>
	 * @throws Exception
	 */
	public void pushandSyncPFTxn(List<HCI> pfTxns) throws Exception;

	// add new Scrip

	/**
	 * Process Mass Update/Create of Scrip Allocation(s) in PFSchema
	 * 
	 * @param allocMassUpdate - List of Scrip(s) and resp. Ideal/Incremental
	 *                        allocations & Flag - true Updates the existing Dep
	 *                        Amnts in Pf Schema as per latest allocations
	 * @throws Exception
	 */
	public void processAllocationChanges(IDS_ScAllocMassUpdate allocMassUpdate) throws Exception;

	/**
	 * Delete the Scrip from Schema & the PF - If you wish to Still Hold the Scrip
	 * and Not Allocate incrementally to It- Set the Inc. allocation for the Scrip
	 * to 0 and not delete it
	 * 
	 * @param scCode       - Scrip Code to be Deleted
	 * @param sellPricePPU - Per unit Selling Price for Scrip to be removed from
	 *                     Schema/PF (if Present in PF)
	 * @throws Exception
	 */
	public void removeScrip4mSchemaPF(String scCode, double sellPricePPU) throws Exception;

	/**
	 * Get Realized P&L amount and Itemized preview of Scrip Sale(s) if any
	 * 
	 * @param scCode - Scrip to be Queried for P&L Realized
	 * @return - IDS_SC_PL
	 * @throws Exception
	 */
	public IDS_SC_PL getRealizedPL4Scrip(String scCode) throws Exception;

	/**
	 * Calculate XIRR Returns for Portfolio
	 * 
	 * @return - XIRRContainer
	 * @throws Exception
	 */
	public XIRRContainer calculateXIRRforPF() throws Exception;

	/**
	 * Get Scrip Details for Existing Scrip in PF- 360 degree view
	 * 
	 * @param scCode - Scrip Code in PF
	 * @return - IDS_Scrip_Details
	 * @throws Exception
	 */
	public IDS_Scrip_Details getScripDetails4Scrip(String scCode) throws Exception;

	/**
	 * Adjust Stock Split in Portfolio
	 * 
	 * @param scSplitIP- IDS_SC_SplitIP {ScripCode|Ratio}
	 * @throws Exception
	 */
	public void adjustPF4StockSplit(IDS_SC_SplitIP scSplitIP) throws Exception;

	/**
	 * Adjust PF for Stock Bonus
	 * 
	 * @param scSplitIP - IDS_SC_SplitIP {ScripCode|ForNShares|TogetMShares}
	 * @throws Exception
	 */
	public void adjustPF4StockBonus(IDS_SC_BonusIP scBonusIP) throws Exception;

	/**
	 * Get Transaction specific Details for Scrip for UI Adhoc Transaction that
	 * might include Buy/Sell/Dividend/Split/Bonus
	 * 
	 * @param scCode - Scrip to be Transacted
	 * @return - IDS_PFTxn_UI : UI Container for Txn. Initiation
	 * @throws Exception
	 */
	public IDS_PFTxn_UI getScripTxnDetails(String scCode) throws Exception;

}
