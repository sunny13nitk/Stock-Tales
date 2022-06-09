package stocktales.DataLake.srv.intf;

import java.util.Calendar;
import java.util.List;

import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;
import stocktales.DataLake.model.entity.DL_ScripPriceATH;

public interface IDataLakeAccessSrv
{
	public List<DL_ScripPriceATH> getPricesAllByDateRange(Calendar from, Calendar to) throws Exception;

	public List<DL_ScripPriceATH> getPricesForScripByDateRange(String scCode, Calendar from, Calendar to)
			throws Exception;

	public List<DL_ScripPriceATH> getPricesForScripsByDateRange(List<String> scCodes, Calendar from, Calendar to)
			throws Exception;

	public SC_CMP_52wkPenultimatePrice_Delta getLastYrPrice_SMA_DeltaByScrip(String scCode, Calendar startDate);

	/**
	 * Performance enhanced call to Scan for All Scrips at one go to determine
	 * Proposals Later
	 * 
	 * @param scCodes   - List of Scrips
	 * @param startDate - Date of Trigger
	 * @return - List<SC_CMP_52wkPenultimatePrice_Delta>
	 */
	public List<SC_CMP_52wkPenultimatePrice_Delta> getLastYrPrice_SMA_DeltasByScripCodes(List<String> scCodes,
			Calendar startDate);

}
