package stocktales.scripCalc.pojos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TY_ScripCalcResult
{
	private List<TY_AttrMultiContainer> attrsMulti = new ArrayList<TY_AttrMultiContainer>();
	
	private List<NameVal> attrsSingle = new ArrayList<NameVal>();
}
