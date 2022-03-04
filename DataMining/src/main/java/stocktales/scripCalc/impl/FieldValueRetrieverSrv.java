package stocktales.scripCalc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripCalc.enums.EnumAttrRole;
import stocktales.scripCalc.enums.EnumDataType;
import stocktales.scripCalc.intf.IFieldValidator;
import stocktales.scripCalc.intf.IFieldValueRetriever;
import stocktales.scripCalc.pojos.TY_EntAttrValue;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service
public class FieldValueRetrieverSrv implements IFieldValueRetriever
{
	@Autowired
	private IFieldValidator fldValidatorSrv;
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<TY_EntAttrValue> getFieldValueSemanticsforObject(
	        Object instance, List<String> fieldNames, boolean validateFields
	) throws Exception
	{
		List<TY_EntAttrValue> entAttrVals = null;
		
		if (fieldNames != null)
		{
			if (instance != null)
			{
				
				if (validateFields)
				{
					for (String fieldName : fieldNames)
					{
						fldValidatorSrv.isFieldValidforObject(instance, fieldName);
					}
					
				}
				
				//List Processing
				if (instance instanceof List<?> || instance instanceof ArrayList<?>)
				{
					entAttrVals = new ArrayList<TY_EntAttrValue>();
					
					for (Object currIns : (List) instance)
					{
						if (currIns != null)
						{
							Object_Info objInfo = new Object_Info(currIns);
							int         i       = 0;
							for (String fieldName : fieldNames)
							{
								
								Method getMethod = objInfo.Get_Getter_for_FieldName(fieldName);
								if (getMethod != null)
								{
									TY_EntAttrValue entAttrVal = new TY_EntAttrValue();
									if (i == 0)
									{
										entAttrVal.setRole(EnumAttrRole.Key);
									}
									if (i == 1)
									{
										entAttrVal.setRole(EnumAttrRole.Attribute1);
									}
									if (i == 2)
									{
										entAttrVal.setRole(EnumAttrRole.Attribute2);
									}
									
									entAttrVal.setAttrName(fieldName);
									entAttrVal.setValue(getMethod.invoke(currIns));
									if ((int.class == getMethod.getReturnType()))
									{
										entAttrVal.setDataType(EnumDataType.Integer);
									} else if ((double.class == getMethod.getReturnType()))
									{
										entAttrVal.setDataType(EnumDataType.Double);
									} else if ((String.class == getMethod.getReturnType()))
									{
										entAttrVal.setDataType(EnumDataType.String);
									}
									
									entAttrVals.add(entAttrVal);
									i++;
									
								}
							}
						}
					}
					
				}
			}
		}
		return entAttrVals;
	}
	
}
