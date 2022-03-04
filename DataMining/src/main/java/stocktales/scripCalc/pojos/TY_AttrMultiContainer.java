
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
public class TY_AttrMultiContainer
{
	private String attrName;
	
	private List<NameVal> nameVals = new ArrayList<NameVal>();
}
