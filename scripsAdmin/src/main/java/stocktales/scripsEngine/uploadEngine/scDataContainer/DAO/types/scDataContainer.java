package stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types;

import java.util.List;

import stocktales.scripsEngine.aspects.SheetName;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Last4QData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Quarters;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;

/*
 * Scrip Data Container Type
 */
public class scDataContainer
{
	@SheetName(Name = "General")
	private EN_SC_GeneralQ SCRoot;
	
	@SheetName(Name = "10YData")
	private EN_SC_10YData TenYData;
	
	@SheetName(Name = "Last 4QData")
	private EN_SC_Last4QData Last4QData;
	
	@SheetName(Name = "Analysis")
	private List<EN_SC_BalSheet> BalSheet_L;
	
	@SheetName(Name = "Trends")
	private List<EN_SC_Trends> Trends_L;
	
	@SheetName(Name = "Data Sheet")
	private List<EN_SC_Quarters> Quarters_L;
	
	public EN_SC_GeneralQ getSCRoot(
	)
	{
		return SCRoot;
	}
	
	public void setSCRoot(
	        EN_SC_GeneralQ sCRoot
	)
	{
		SCRoot = sCRoot;
	}
	
	public EN_SC_10YData getTenYData(
	)
	{
		return TenYData;
	}
	
	public void setTenYData(
	        EN_SC_10YData tenYData
	)
	{
		TenYData = tenYData;
	}
	
	public EN_SC_Last4QData getLast4QData(
	)
	{
		return Last4QData;
	}
	
	public void setLast4QData(
	        EN_SC_Last4QData last4qData
	)
	{
		Last4QData = last4qData;
	}
	
	public List<EN_SC_BalSheet> getBalSheet_L(
	)
	{
		return BalSheet_L;
	}
	
	public void setBalSheet_L(
	        List<EN_SC_BalSheet> balSheet_L
	)
	{
		BalSheet_L = balSheet_L;
	}
	
	public List<EN_SC_Trends> getTrends_L(
	)
	{
		return Trends_L;
	}
	
	public void setTrends_L(
	        List<EN_SC_Trends> trends_L
	)
	{
		Trends_L = trends_L;
	}
	
	public List<EN_SC_Quarters> getQuarters_L(
	)
	{
		return Quarters_L;
	}
	
	public void setQuarters_L(
	        List<EN_SC_Quarters> quarters_L
	)
	{
		Quarters_L = quarters_L;
	}
	
	public scDataContainer(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
