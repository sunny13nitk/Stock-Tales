package stocktales.DataLake.srv.intf;

import java.util.Date;
import java.util.List;

import stocktales.DataLake.model.entity.DL_ScripPrice;
import stocktales.DataLake.model.repo.intf.IDL_IDSStats;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.historicalPrices.pojo.StockHistory;

public interface DL_HistoricalPricesSrv
{
	public List<DL_ScripPrice> getHistoricalPricesByScripBetweenDates(String scCode, Date from, Date to);

	public List<DL_ScripPrice> getHistoricalPricesByScripPast1Yr(String scCode);

	public List<DL_ScripPrice> getPricesHistoryContainer();

	/**
	 * Only Suitable for DEfault Duration of PAst 1 year - Use
	 * getStocksHistory4mRepo for more backward durations exceeding 1 year
	 * 
	 * @param from - Date
	 * @param to   - Date
	 * @return
	 */
	public List<StockHistory> getStocksHistory4mContainer(Date from, Date to);

	/**
	 * Get Stock History Bypassing Session Buffer Directly from DB- Upto 5 years for
	 * IDS
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public List<StockHistory> getStocksHistory4mRepo(Date from, Date to);

	public IDS_ScSMASpread getSMASpreadforScrip(String scrip, int[] smaIntervals);

	/**
	 * GET Data hub Stats by Scrip
	 * 
	 * @return - List<IDL_IDSStats>
	 */
	public List<IDL_IDSStats> getStats();

	/**
	 * Update Daily Prices for Scrips - Handled Separately in respective IDS and NFS
	 * Implementations
	 * 
	 * @throws Exception
	 */
	public void updateDailyPrices() throws Exception;

}
