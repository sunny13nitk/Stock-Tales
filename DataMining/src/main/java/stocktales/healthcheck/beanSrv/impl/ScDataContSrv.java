package stocktales.healthcheck.beanSrv.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.maths.UtilPercentages;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScDataContSrv implements IScDataContSrv
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private ISCExistsDB_Srv scEXSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Override
	public List<NameVal> getDeltaListforBalSheet(
	        String scCode, String attrName
	) throws Exception
	{
		List<NameVal> nameValCol = null;
		if (scCode != null && attrName != null)
		{
			//Scrip and Attribute Both Valid
			if (scEXSrv.Is_ScripExisting_DB(scCode) && isFieldValid(attrName))
			{
				//Get the Balance sheet collection for Scrip
				List<EN_SC_BalSheet> balSheetCol = fsBuffSrv.getDataContainerforScrip(scCode).getBalSheet_L();
				List<NameVal>        valBuff     = new ArrayList<NameVal>();
				if (balSheetCol != null)
				{
					if (balSheetCol.size() > 0)
					{
						
						nameValCol = new ArrayList<NameVal>();
						//Iterate and get the Values
						for (EN_SC_BalSheet en_SC_BalSheet : balSheetCol)
						{
							//Get Value for Entity Pass
							valBuff.add(new NameVal(String.valueOf(en_SC_BalSheet.getYear()),
							        this.getValueforAttributeforEntity(attrName, en_SC_BalSheet)));
							//As two entities Come calculate delta and clear top delta entity(index 0)
							if (valBuff.size() == 2)
							{
								//Calculate Delta Percentage
								NameVal deltaVal = new NameVal(
								        valBuff.get(0).getName() + "/" + valBuff.get(1).getName(),
								        UtilPercentages.getPercentageDelta(valBuff.get(0).getValue(),
								                valBuff.get(1).getValue(), 1));
								nameValCol.add(deltaVal);
								
								//Remove Index 0 entity from valBuff
								valBuff.remove(0);
								
							}
						}
						
					}
				}
			}
		}
		return nameValCol;
	}
	
	@Override
	public boolean isFieldValid(
	        String fldName
	) throws Exception
	{
		boolean isValid = false;
		
		EN_SC_BalSheet balSheetPoJo = new EN_SC_BalSheet();
		Object_Info    objInfo      = new Object_Info(balSheetPoJo);
		if (objInfo != null)
		{
			Method getMethod = objInfo.Get_Getter_for_FieldName(fldName);
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
	
	private double getValueforAttributeforEntity(
	        String attrName, Object Entity
	) throws Exception
	{
		double value = 0;
		
		//Get Getter Method for Attribute Name
		Object_Info objInfo = new Object_Info(Entity);
		if (objInfo != null)
		{
			Method M = objInfo.Get_Getter_for_FieldName(attrName);
			if (M != null)
			{
				//Int to to Double Cast Exception
				if ((int.class == M.getReturnType()))
				{
					//Is a Double No need to convert
					value = (double) ((Integer) M.invoke(Entity)).intValue();
				} else
				{
					value = (double) M.invoke(Entity);
				}
			}
		}
		
		return value;
	}
	
}
