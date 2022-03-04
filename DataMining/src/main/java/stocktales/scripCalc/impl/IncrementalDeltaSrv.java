package stocktales.scripCalc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.maths.UtilPercentages;
import stocktales.scripCalc.annotation.ScripCalcAttrList;
import stocktales.scripCalc.enums.EnumAttrRole;
import stocktales.scripCalc.enums.EnumDataType;
import stocktales.scripCalc.intf.IIncrementalDeltaSrv;
import stocktales.scripCalc.pojos.TY_AttrMultiContainer;
import stocktales.scripCalc.pojos.TY_EntAttrValue;

@Service
public class IncrementalDeltaSrv implements IIncrementalDeltaSrv
{
	
	private List<Integer> keyListI = new ArrayList<Integer>();
	private List<String>  keyListS = new ArrayList<String>();
	
	private List<Integer> valListI = new ArrayList<Integer>();
	private List<Double>  valListD = new ArrayList<Double>();
	
	private EnumDataType dtypeKey;
	private EnumDataType dtypeVal;
	
	@Override
	public TY_AttrMultiContainer getAttrMultiContainer(
	        List<TY_EntAttrValue> entAttrVals, ScripCalcAttrList annscCalcAttrList
	)
	{
		TY_AttrMultiContainer attrMc = null;
		
		if (entAttrVals != null && annscCalcAttrList != null)
		{
			if (entAttrVals.size() > 0)
			{
				//Determine the Data types
				determineDataTypes(entAttrVals);
				
				//Populate key and value tables as per Their Data types
				populatekeyValTables(entAttrVals);
				
				//Prepare Attribute Multi-Container
				attrMc = prepareAttrMC(annscCalcAttrList.label());
				
			}
		}
		
		return attrMc;
	}
	
	/*
	 * -------------------------------- PRIVATE SECTION -------------------------------------
	 */
	
	private void determineDataTypes(
	        List<TY_EntAttrValue> entAttrVals
	)
	{
		//Get first Key
		TY_EntAttrValue keyEnt = entAttrVals.stream().filter(x -> x.getRole() == EnumAttrRole.Key).findFirst().get();
		if (keyEnt != null)
		{
			//Set key Data Type
			dtypeKey = keyEnt.getDataType();
		}
		
		//Get first Value
		TY_EntAttrValue valEnt = entAttrVals.stream().filter(x -> x.getRole() == EnumAttrRole.Attribute1).findFirst()
		        .get();
		if (valEnt != null)
		{
			//Set key Data Type
			dtypeVal = valEnt.getDataType();
		}
		
	}
	
	private void populatekeyValTables(
	        List<TY_EntAttrValue> entAttrVals
	)
	{
		int           idx         = 0;
		List<Integer> idxtoRemove = new ArrayList<Integer>();
		
		switch (dtypeKey)
		{
			case String:
				
				List<TY_EntAttrValue> keyValsS = entAttrVals.stream().filter(x -> x.getRole().equals(EnumAttrRole.Key))
				        .collect(Collectors.toList());
				;
				if (keyValsS != null)
				{
					if (keyValsS.size() > 0)
					{
						for (TY_EntAttrValue entAttrValue : keyValsS)
						{
							keyListS.add((String) entAttrValue.getValue());
						}
					}
				}
				break;
			
			case Integer:
				
				List<TY_EntAttrValue> keyValsI = entAttrVals.stream().filter(x -> x.getRole().equals(EnumAttrRole.Key))
				        .collect(Collectors.toList());
				;
				if (keyValsI != null)
				{
					if (keyValsI.size() > 0)
					{
						for (TY_EntAttrValue entAttrValue : keyValsI)
						{
							keyListI.add((Integer) entAttrValue.getValue());
						}
					}
				}
				break;
			
			default:
				break;
		}
		
		switch (dtypeVal)
		{
			case Integer:
				
				List<TY_EntAttrValue> valValsI = entAttrVals.stream()
				        .filter(x -> x.getRole().equals(EnumAttrRole.Attribute1)).collect(Collectors.toList());
				;
				if (valValsI != null)
				{
					if (valValsI.size() > 0)
					{
						
						for (TY_EntAttrValue entAttrValue : valValsI)
						{
							if ((Integer) entAttrValue.getValue() == 0)
							{
								idxtoRemove.add(idx);
							} else
							{
								valListI.add((Integer) entAttrValue.getValue());
							}
							idx++;
						}
					}
				}
				break;
			
			case Double:
				
				List<TY_EntAttrValue> valValsD = entAttrVals.stream()
				        .filter(x -> x.getRole().equals(EnumAttrRole.Attribute1)).collect(Collectors.toList());
				;
				if (valValsD != null)
				{
					if (valValsD.size() > 0)
					{
						for (TY_EntAttrValue entAttrValue : valValsD)
						{
							
							if ((Double) entAttrValue.getValue() == 0)
							{
								idxtoRemove.add(idx);
							} else
							{
								valListD.add((Double) entAttrValue.getValue());
							}
							idx++;
							
						}
					}
				}
				break;
			
			default:
				break;
		}
		
		//Data cleansing- Remove 0 based Value and Key Pairs
		
		if (idxtoRemove.size() > 0)
		{
			for (Integer idxRem : idxtoRemove)
			{
				if (dtypeKey == EnumDataType.String)
				{
					keyListS.remove(idxRem);
				}
				if (dtypeKey == EnumDataType.Integer)
				{
					keyListI.remove(idxRem);
				}
				
				if (dtypeVal == EnumDataType.Integer)
				{
					valListI.remove(idxRem);
				}
				if (dtypeVal == EnumDataType.Double)
				{
					valListD.remove(idxRem);
				}
				
			}
			
		}
		
	}
	
	private TY_AttrMultiContainer prepareAttrMC(
	        String label
	)
	{
		TY_AttrMultiContainer attrMc = new TY_AttrMultiContainer();
		
		int size = 0;
		
		if (dtypeKey == EnumDataType.String)
		{
			size = keyListS.size();
		}
		if (dtypeKey == EnumDataType.Integer)
		{
			size = keyListI.size();
		}
		
		if (size > 0)
		{
			attrMc.setAttrName(label);
			
			for (int i = 0; i < size - 1; i++)
			{
				NameVal nameval = new NameVal();
				
				if (dtypeKey == EnumDataType.String)
				{
					nameval.setName(keyListS.get(i) + "/" + keyListS.get(i + 1));
				}
				
				if (dtypeKey == EnumDataType.Integer)
				{
					nameval.setName(keyListI.get(i).toString() + "/" + keyListI.get(i + 1).toString());
				}
				
				if (dtypeVal == EnumDataType.Integer)
				{
					nameval.setValue(UtilPercentages.getPercentageDelta(valListI.get(i).doubleValue(),
					        valListI.get(i + 1).doubleValue(), 1));
				}
				if (dtypeVal == EnumDataType.Double)
				{
					nameval.setValue(UtilPercentages.getPercentageDelta(valListD.get(i), valListD.get(i + 1), 1));
				}
				
				attrMc.getNameVals().add(nameval);
				
			}
		}
		
		return attrMc;
	}
}
