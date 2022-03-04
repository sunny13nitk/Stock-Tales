package stocktales.scripsEngine.uploadEngine.tools.services.headersConvSrv;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IHeadersConvSrv;
import stocktales.scripsEngine.utilities.dateStringutils.interfaces.IMonthYearInfoSrv;
import stocktales.scripsEngine.utilities.dateStringutils.types.MonthYearInfo;

@Service("DataSheet_HeadersConvSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataSheet_HeadersConvSrv implements IHeadersConvSrv
{
	
	@Autowired
	private IMonthYearInfoSrv monYrInfoSrv;
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> ArrayList<T> convertHeaderVals(
	        ArrayList<T> unConvHeaders
	) throws EX_General
	{
		
		ArrayList<T>      convlist  = new ArrayList<T>();
		ArrayList<String> yrQtrVals = new ArrayList<String>();
		String            val       = null;
		if (unConvHeaders != null)
		{
			if (unConvHeaders.size() > 0)
			{
				if (monYrInfoSrv != null)
				{
					
					ArrayList<String> headers = (ArrayList<String>) unConvHeaders;
					if (headers != null)
					{
						for (String header : headers)
						{
							val = null;
							MonthYearInfo monYrInfo = monYrInfoSrv.getMonthYearInfoforString(header, "-");
							if (monYrInfo != null)
							{
								val = monYrInfo.getYear() + "|" + monYrInfo.getMonthInfo().getQuarter();
								yrQtrVals.add(val);
							}
						}
						
						convlist = (ArrayList<T>) yrQtrVals;
					}
				}
			}
		}
		return convlist;
	}
	
}
