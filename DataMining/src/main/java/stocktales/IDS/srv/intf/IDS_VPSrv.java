package stocktales.IDS.srv.intf;

import java.util.Calendar;

import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;

public interface IDS_VPSrv
{
	/**
	 * Get Volatility Profile Details for a Scrip incl. #sma Breaches, Vol Score and
	 * Profile
	 * 
	 * @param scCode - Scrip NSE Code
	 * @return - Volatility profile Details
	 * @throws Exception
	 */
	public IDS_VPDetails getVolatilityProfileDetailsforScrip(String scCode) throws Exception;

	/**
	 * Get Volatility Profile Details for a Scrip incl. #sma Breaches, Vol Score and
	 * Profile - Always based on 1 year past data
	 * 
	 * @param scCode    - Scrip NSE Code
	 * @param intervals - Int [] of Intervals
	 * @param from      - Calendar Instance from
	 * @param to        - Calendar Instance to
	 * @return - Volatility profile Details
	 * @throws Exception
	 */
	public IDS_VPDetails getVolatilityProfileDetails4Scrip4Durations(String scCode, Calendar from, Calendar to)
			throws Exception;

	public IDS_VPDetails getVolatilityProfileDetails4Scrip4PriceHistory(String scCode,
			NFSStockHistoricalQuote scHistoricaPrices) throws Exception;

}
