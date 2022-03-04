package stocktales.IDS.srv.intf;

import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.PFSchemaRebalUI;
import stocktales.IDS.pojo.PFSchemaRebalUIStats;
import stocktales.exceptions.SchemaUpdateException;

public interface IDS_PFSchema_REbalUI_Srv
{
	/**
	 * Create a New Schema REbalance container
	 * 
	 * @return - return the same
	 */
	public PFSchemaRebalUI createSchema();

	/**
	 * Upload Current Schema in Rebalance Container Format and REturn the same
	 * 
	 * @return
	 * @throws Exception
	 */
	public PFSchemaRebalUI uploadSchemaforUpdate() throws Exception;

	/**
	 * REfurbish and Add the Selected Tagged Scrips to REbal Container
	 * 
	 * @param scripsStr - String f Tagged Scrip Codes
	 */
	public void addValidateScrips(String scripsStr);

	/**
	 * REmove Scrip from REbal Container
	 * 
	 * @param scCode - Scrip to be removed
	 * @throws Exception
	 */
	public void removeScrip4mSchema(String scCode) throws Exception;

	/**
	 * Get handle to REbalance Container POJO
	 * 
	 * @return - REbalance Container POJO
	 */
	public PFSchemaRebalUI getRebalContainer();

	/**
	 * Clear the Scrips Selected in DDLB
	 */
	public void clearSelScrips();

	/**
	 * REfresh in Re-balance Container POJO the Scrips and Allocations as set in UI
	 * Form
	 * 
	 * @param allocMassUpdate - IDS_ScAllocMassUpdate POJO from Form
	 * @throws Exception
	 */
	public void refurbishAllocations(IDS_ScAllocMassUpdate allocMassUpdate) throws Exception;

	/**
	 * Get Quick Pf Statistics for the PF Schema
	 * 
	 * @return - POJO of Type PFSchemaRebalUIStats
	 * @throws Exception
	 */
	public PFSchemaRebalUIStats getQuickStatsforSchema() throws Exception;

	public void commitValidatedSchema(IDS_ScAllocMassUpdate allocMassUpdate) throws SchemaUpdateException, Exception;

}
