package stocktales.cagrEval.srv;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.cagrEval.helperPoJo.BalSheetSrvParam;
import stocktales.cagrEval.helperPoJo.CAGRPoJo;
import stocktales.cagrEval.helperPoJo.SCCurrData;
import stocktales.cagrEval.helperPoJo.YearsFromTo;
import stocktales.cagrEval.intf.IBalSheetUtilSrv;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalSheetUtilSrv implements IBalSheetUtilSrv
{
	
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	private Method      getMethod;
	private YearsFromTo yearFromTo; // Balance Sheet Data maintained From to Years
	
	private SCCurrData scCurrData; //Current Scrip Data - From Container in case needed
	
	private final int maxYr = Calendar.getInstance().get(Calendar.YEAR);
	
	private List<EN_SC_BalSheet> balSheetEntities = new ArrayList<EN_SC_BalSheet>();
	
	private boolean currData = false;
	
	@Override
	public double getFromBalSheetByParam(
	        BalSheetSrvParam balSheetParam
	) throws Exception
	{
		clear();
		double result = 0;
		//1. Validate the Field
		if (isFieldValid(balSheetParam.getAttrName()))
		{
			//Load the Balance Sheet Entities from FactSheet Buffer of Scrip Data Containers for Current Scrip
			loadBalSheetData(balSheetParam.getScCode(), balSheetParam.isCurrentData());
			
			//Populate Max and Min Years of BS Data maintained
			populateBSDurationRange();
			
			//calculate as per Agg Function
			result = calculate(balSheetParam);
			
		}
		return result;
	}
	
	/*
	 *  ---------------- PRIVATE SECTION ----------------
	 */
	private boolean isFieldValid(
	        String fldName
	) throws Exception
	{
		boolean isValid = false;
		
		EN_SC_BalSheet balSheetPoJo = new EN_SC_BalSheet();
		Object_Info    objInfo      = new Object_Info(balSheetPoJo);
		if (objInfo != null)
		{
			getMethod = objInfo.Get_Getter_for_FieldName(fldName);
			if (getMethod != null)
			{
				isValid = true;
			}
		}
		
		if (isValid == false)
		{
			throw new Exception(msgSrc.getMessage("INVALID_BSHEET_FLD", new Object[]
			{ fldName }, Locale.ENGLISH));
		}
		
		return isValid;
	}
	
	private void clear(
	)
	{
		this.setGetMethod(null);
		this.setYearFromTo(null);
		this.setBalSheetEntities(new ArrayList<EN_SC_BalSheet>());
		this.setScCurrData(null);
		this.currData = false;
	}
	
	private void loadBalSheetData(
	        String scCode, boolean currData
	)
	{
		if (scCode != null)
		{
			scDataContainer scDC = fsBuffSrv.getDataContainerforScrip(scCode);
			if (scDC != null)
			{
				this.balSheetEntities = scDC.getBalSheet_L();
				this.scCurrData       = new SCCurrData();
				scCurrData.setMCap(scDC.getSCRoot().getMktCap());
			}
			if (currData == true)
			{
				
				this.currData = true;
				
			}
			
		}
	}
	
	private void populateBSDurationRange(
	)
	{
		Optional<EN_SC_BalSheet> balMin = this.balSheetEntities.stream()
		        .min(Comparator.comparing(EN_SC_BalSheet::getYear));
		if (balMin.isPresent())
		{
			yearFromTo = new YearsFromTo();
			yearFromTo.setFrom(balMin.get().getYear());
		}
		
		Optional<EN_SC_BalSheet> balMax = this.balSheetEntities.stream()
		        .max(Comparator.comparing(EN_SC_BalSheet::getYear));
		if (balMax.isPresent())
		{
			yearFromTo.setTo(balMax.get().getYear());
		}
		
	}
	
	private double calculate(
	        BalSheetSrvParam balSheetParam
	) throws Exception
	{
		double result = 0;
		switch (balSheetParam.getFxntoTrigger())
		{
			case CAGR:
				result = triggerCAGRCalc(balSheetParam.getYrsFromToFilter(), balSheetParam.getAttrName());
				break;
			
			default:
				break;
		}
		
		return result;
	}
	
	private double triggerCAGRCalc(
	        YearsFromTo yft, String attrName
	) throws Exception
	{
		double      result   = 0;
		YearsFromTo yftseek  = new YearsFromTo();
		CAGRPoJo    cagrPoJo = new CAGRPoJo();
		
		if (currData == false)
		{
			
			if (yearFromTo.getFrom() <= yft.getFrom() && yearFromTo.getTo() >= yft.getTo())
			{
				yftseek = yft;
				
			} else //Will fail on Min side Only - Min Data old ought not be found for certain scrips
			{
				if (yearFromTo.getTo() < yft.getTo())
				{
					yftseek.setFrom(yearFromTo.getFrom());
					yftseek.setTo(yearFromTo.getTo());
				} else
				{
					yftseek.setFrom(yearFromTo.getFrom());
					yftseek.setTo(yft.getTo());
				}
			}
			
		} else
		{
			yftseek = yft;
		}
		
		if (yftseek.getFrom() > 0)
		{
			if (yftseek.getFrom() >= yftseek.getTo())
			{
				result = 0;
			}
			
			else
			{
				//Already accounted for Current Year Data
				cagrPoJo.setInterval(yftseek.getTo() - yftseek.getFrom());
				
				cagrPoJo.setIniVal(getValueforAttributeByYear(yftseek.getFrom(), attrName));
				
				if (currData == true)
				{
					if (cagrPoJo.getIniVal() == 0)
					{
						cagrPoJo.setIniVal(getValueforAttributeByYear(yearFromTo.getFrom(), attrName));
						cagrPoJo.setInterval(maxYr - yearFromTo.getFrom());
					}
					//Latest data from ScCurrData - match by field name getter - Always compute to latest Values by default
					cagrPoJo.setFinVal(getCurrValueforScripforAttribute(attrName));
					//Increment the Interval size by 1 for fair comparison with Nifty
					//cagrPoJo.setInterval(cagrPoJo.getInterval()) ;
				} else
				{
					cagrPoJo.setFinVal(getValueforAttributeByYear(yftseek.getTo(), attrName));
				}
				
				/*
				 * Calculate CAGR
				 */
				if (cagrPoJo.getIniVal() > 0)
				{
					
					cagrPoJo.setCAGR(
					        (Math.pow(cagrPoJo.getFinVal() / cagrPoJo.getIniVal(), 1.0 / cagrPoJo.getInterval()) - 1.0)
					                * 100);
				} else
				{
					cagrPoJo.setCAGR(0);
				}
				
				result = cagrPoJo.getCAGR();
			}
		}
		
		return result;
		
	}
	
	private double getValueforAttributeByYear(
	        int year, String attrName
	) throws Exception
	{
		double                   value  = 0;
		Optional<EN_SC_BalSheet> balSEO = this.balSheetEntities.stream().filter(x -> x.getYear() == year).findFirst();
		if (balSEO.isPresent())
		{
			EN_SC_BalSheet entBSFrom = balSEO.get();
			if (entBSFrom != null)
			{
				//Get Getter Method for Attribute Name
				Object_Info objInfo = new Object_Info(entBSFrom);
				if (objInfo != null)
				{
					Method M = objInfo.Get_Getter_for_FieldName(attrName);
					if (M != null)
					{
						//Int to to Double Cast Exception
						if ((int.class == M.getReturnType()))
						{
							//Is a Double No need to convert
							value = (double) ((Integer) M.invoke(entBSFrom)).intValue();
						} else
						{
							value = (double) M.invoke(entBSFrom);
						}
						
					}
				}
			}
		}
		
		return value;
	}
	
	private double getCurrValueforScripforAttribute(
	        String attrName
	) throws Exception
	{
		double value = 0;
		
		//Get Getter Method for Attribute Name
		Object_Info objInfo = new Object_Info(this.scCurrData);
		if (objInfo != null)
		{
			Method M = objInfo.Get_Getter_for_FieldName(attrName);
			if (M != null)
			{
				//Int to to Double Cast Exception
				if ((int.class == M.getReturnType()))
				{
					//Is a Double No need to convert
					value = (double) ((Integer) M.invoke(this.scCurrData)).intValue();
				} else
				{
					value = (double) M.invoke(this.scCurrData);
				}
			}
		}
		
		return value;
	}
	
}
