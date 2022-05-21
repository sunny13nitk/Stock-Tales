package stocktales.DataLake.srv.intf;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import stocktales.DataLake.model.pojo.UploadStats;

public interface DL_ATH_ImplicitLoadSrv
{
	/**
	 * Load 6 year Old Data for All NSE listed Scrips
	 */
	public CompletableFuture<UploadStats> performInitialLoad();

	/**
	 * Perform Only Delta Load - for Last 1 Year prices as per last Saved Date Only
	 * to be Executed Post Market Close for Correct Prices for Last Closing Day
	 */
	public CompletableFuture<UploadStats> performDeltaLoad();

	public List<String> getErroredScrips();
}
