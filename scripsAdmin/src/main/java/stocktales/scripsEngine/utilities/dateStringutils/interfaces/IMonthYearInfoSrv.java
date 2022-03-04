package stocktales.scripsEngine.utilities.dateStringutils.interfaces;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.utilities.dateStringutils.types.MonthYearInfo;

/**
 * Month Year Info. Service Interface
 *
 */
public interface IMonthYearInfoSrv
{
	MonthYearInfo getMonthYearInfoforString(
	        String MonYearTxt, String separator
	) throws EX_General;
	
}
