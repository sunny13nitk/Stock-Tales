package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CAGRFormPoJo extends RollOverDurationsParam
{
	private int     stgyId;
	private boolean E2EOnly;
}
