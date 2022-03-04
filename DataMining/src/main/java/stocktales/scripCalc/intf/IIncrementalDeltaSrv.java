package stocktales.scripCalc.intf;

import java.util.List;

import stocktales.scripCalc.annotation.ScripCalcAttrList;
import stocktales.scripCalc.pojos.TY_AttrMultiContainer;
import stocktales.scripCalc.pojos.TY_EntAttrValue;

public interface IIncrementalDeltaSrv
{
	public TY_AttrMultiContainer getAttrMultiContainer(
	        List<TY_EntAttrValue> entAttrVals, ScripCalcAttrList annscCalcAttrList
	);
}
