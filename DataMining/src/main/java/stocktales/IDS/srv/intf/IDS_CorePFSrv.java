package stocktales.IDS.srv.intf;

import java.util.List;

import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;

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

}
