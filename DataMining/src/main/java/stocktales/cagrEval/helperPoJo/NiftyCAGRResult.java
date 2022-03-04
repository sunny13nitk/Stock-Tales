package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.cagrEval.model.NiftyTracker;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NiftyCAGRResult
{
	private NiftyTracker from;
	private NiftyTracker to;
	private int          duration;
	private double       CAGR;
}
