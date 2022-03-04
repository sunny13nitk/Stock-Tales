package stocktales.scripCalc.impl;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.scripCalc.annotation.ScripCalcAttrList;
import stocktales.scripCalc.enums.EnumCalcOperation;
import stocktales.scripCalc.intf.IScripCalculations;
import stocktales.scripCalc.pojos.TY_ScripCalcResult;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;

@Service
@Getter
@Setter
@NoArgsConstructor
public class ScripSnapshotCalcAttrSrv implements IScripCalculations
{
	@ScripCalcAttrList(
	        label = "Capex To Sales (%)", sheetName = SheetNames.Analysis, keyAttr = "Year", attr1 = "Capex", attr2 = "Sales", operator = EnumCalcOperation.DivisionPercentage
	)
	public final String CapexToSales = "";
	
	@ScripCalcAttrList(
	        label = "Debt To Sales (%)", sheetName = SheetNames.Analysis, keyAttr = "Year", attr1 = "Debt", attr2 = "Sales", operator = EnumCalcOperation.DivisionPercentage
	)
	public final String DebtToSales = "";
	
	@ScripCalcAttrList(
	        label = "NFA To Sales (%)", sheetName = SheetNames.Analysis, keyAttr = "Year", attr1 = "NFA", attr2 = "Sales", operator = EnumCalcOperation.DivisionPercentage
	)
	public final String NFAToSales = "";
	
	@ScripCalcAttrList(
	        label = "Sales Growth - YoY (%)", sheetName = SheetNames.Analysis, keyAttr = "Year", attr1 = "Sales", operator = EnumCalcOperation.IncrementalDelta
	)
	public final String SalesGrowth = "";
	
	/**
	 * Handled via Central Aspect
	 */
	@Override
	public TY_ScripCalcResult getScripCalculatedAttributesResult(
	        String scCode
	)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
