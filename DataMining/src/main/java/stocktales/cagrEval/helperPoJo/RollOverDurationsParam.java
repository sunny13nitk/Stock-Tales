package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RollOverDurationsParam
{
	private int     baseYr;
	private int     intervalinYrs;
	private int     length;
	private boolean tolastUpdate;
}
