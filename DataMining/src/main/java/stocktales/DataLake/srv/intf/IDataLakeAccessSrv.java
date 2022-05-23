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

}
