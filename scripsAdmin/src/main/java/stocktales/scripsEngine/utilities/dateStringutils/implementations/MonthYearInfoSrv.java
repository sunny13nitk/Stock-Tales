package stocktales.scripsEngine.utilities.dateStringutils.implementations;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.Months;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.PathsJAXBMO;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.monthsConfig;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.implementations.monthsConfig_JAXB;
import stocktales.scripsEngine.utilities.dateStringutils.interfaces.IMonthYearInfoSrv;
import stocktales.scripsEngine.utilities.dateStringutils.types.MonthYearInfo;

@Service
public class MonthYearInfoSrv implements IMonthYearInfoSrv
{
	
	@Autowired
	private PathsJAXBMO pathsMO;
	
	private Months months_mdt;
	private int    BaseYear = 2000;
	
	@Override
	public MonthYearInfo getMonthYearInfoforString(
	        String MonYearTxt, String separator
	) throws EX_General
	{
		
		String        uCaseMonYearText = null;
		String[]      nameSplit        = new String[]
		{};
		MonthYearInfo monYrInfo        = null;
		
		if (MonYearTxt != null)
		{
			if (this.months_mdt == null)
			{
				Initialize_Metadata();
			}
			
			uCaseMonYearText = MonYearTxt.toUpperCase();
			
			if (separator != null)
			{
				nameSplit = uCaseMonYearText.split(separator);
				monYrInfo = new MonthYearInfo();
				monYrInfo.setYear(this.BaseYear + Integer.parseInt(nameSplit[1]));
			} else
			{
				nameSplit[0] = uCaseMonYearText;
			}
			
			if (nameSplit[0] != null && months_mdt != null)
			{
				String monNametoCmp = nameSplit[0];
				
				try
				{
					monthsConfig monConfig = this.months_mdt.getMonthConfig().stream().filter(
					        
					        x ->
					        {
						        
						        // Filtering when the First Name or Last Name in the Month Matches UpperCase
						        // Provided
						        // Name
						        if (
						            x.getMonthNames().getLname().equals(monNametoCmp)
						                    || x.getMonthNames().getSname().equals(monNametoCmp)
						        )
						        {
							        return true;
						        }
						        return false;
						        
					        }).findFirst().get();
					
					if (monConfig != null)
					{
						
						monYrInfo.setMonthInfo(monConfig);
					}
					
				}
				
				catch (NoSuchElementException e)
				{
					EX_General egen = new EX_General("INVALID_MONTH", new Object[]
					{ monNametoCmp });
					
					throw egen;
				}
				
			}
			
		}
		
		else
		
		{
			EX_General egen = new EX_General("ERR_BLANK_DATE", null);
			
			throw egen;
		}
		return monYrInfo;
	}
	
	private void Initialize_Metadata(
	) throws EX_General
	{
		if (this.pathsMO != null)
		{
			monthsConfig_JAXB moJAXB = new monthsConfig_JAXB(pathsMO);
			if (moJAXB != null)
			{
				try
				{
					months_mdt = new Months(moJAXB.Load_XML_to_Objects());
					if (months_mdt != null)
					{
						if (months_mdt.getMonthConfig().size() != 12)
						{
							EX_General egen = new EX_General("MISSING_CONFIG_MONTHS", new Object[]
							{ this.pathsMO.getJaxPath_MO_xml(), months_mdt.getMonthConfig().size() });
							
							throw egen;
						}
					}
					
				} catch (Exception e)
				{
					EX_General egen = new EX_General("ERR_MONTHS_MDT_CONV", new Object[]
					{ this.pathsMO.getJaxPath_MO_xml() });
					
					throw egen;
				}
			}
		}
		
	}
	
}
