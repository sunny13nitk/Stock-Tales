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

@Service("Analysis_HeadersConvSrv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Analysis_HeadersConvSrv implements IHeadersConvSrv
{
	
	@Autowired
	private IMonthYearInfoSrv monYrInfoSrv;
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> ArrayList<T> convertHeaderVals(
	        ArrayList<T> unConvHeaders
	) throws EX_General
	{
		
		ArrayList<T>       convlist = new ArrayList<T>();
		ArrayList<Integer> yrvals   = new ArrayList<Integer>();
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
							MonthYearInfo monYrInfo = monYrInfoSrv.getMonthYearInfoforString(header, "-");
							if (monYrInfo != null)
							{
								yrvals.add(monYrInfo.getYear());
							}
						}
						
						convlist = (ArrayList<T>) yrvals;
					}
				}
			}
		}
		return convlist;
	}
	
}
