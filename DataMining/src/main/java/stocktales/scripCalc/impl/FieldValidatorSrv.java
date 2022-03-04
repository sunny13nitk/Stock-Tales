package stocktales.scripCalc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import stocktales.scripCalc.intf.IFieldValidator;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service

public class FieldValidatorSrv implements IFieldValidator
{
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isFieldValidforObject(
	        Object instance, String fieldName
	)
	{
		boolean isValid = false;
		
		if (instance != null)
		{
			Object currInstance = null;
			
			if (instance instanceof List<?> || instance instanceof ArrayList<?>)
			{
				currInstance = ((List) instance).get(0);
			} else
			{
				currInstance = instance;
			}
			if (currInstance != null)
			{
				Object_Info objInfo = new Object_Info(currInstance);
				if (objInfo != null)
				{
					Method getMethod = objInfo.Get_Getter_for_FieldName(fieldName);
					if (getMethod != null)
					{
						isValid = true;
					}
				}
				
			}
			
		}
		
		return isValid;
	}
	
}
