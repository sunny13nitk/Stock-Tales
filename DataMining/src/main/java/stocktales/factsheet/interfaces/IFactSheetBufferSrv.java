package stocktales.factsheet.interfaces;

import java.util.List;

import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;

public interface IFactSheetBufferSrv
{
	// Get All Scrips In Comparison Data Containers
	public List<scDataContainer> getScripDataContainers(
	);
	
	//Get a Handle to Data Container for a particular Scrip
	public scDataContainer getDataContainerforScrip(
	        String scCode
	);
	
	//Get Names of Peer Scrips
	public List<String> getPeerScripNames(
	);
	
	//Get Name of Reference Scrip
	public String getRefScripName(
	);
	
	//Clear the Buffer and Initialize for a Scrip
	public void Initialize(
	        String scCode, boolean no_peers
	) throws Exception;
	
	//Clear the Buffer and Initialize for a Scrip and said Peers
	public void Initialize(
	        String scCode, List<String> peers
	) throws Exception;
	
	//Clear the Buffer and Initialize for a Scrip and exclude specified Peers from Sector Scrips
	public void InitializeAndExclude(
	        String scCode, List<String> peersToExclude
	) throws Exception;
	
	/*
	 * Refer Sheet Name from SheetNames Class
	 */
	public void Initialize(
	        List<String> scrips, String sheetName, boolean loadComplete
	) throws Exception;
	
}
