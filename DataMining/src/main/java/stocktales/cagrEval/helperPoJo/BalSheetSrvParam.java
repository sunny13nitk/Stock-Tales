package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.cagrEval.enums.EnumAggFxn;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalSheetSrvParam
{
	private YearsFromTo yrsFromToFilter;
	private String      scCode;
	private String      attrName;
	private EnumAggFxn  fxntoTrigger;
	private boolean     currentData;
}
