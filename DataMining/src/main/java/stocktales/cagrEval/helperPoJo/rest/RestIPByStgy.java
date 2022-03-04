package stocktales.cagrEval.helperPoJo.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.cagrEval.helperPoJo.RollOverDurationsParam;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestIPByStgy extends RollOverDurationsParam
{
	private int     stgyId;
	private boolean e2eOnly;
	
}
