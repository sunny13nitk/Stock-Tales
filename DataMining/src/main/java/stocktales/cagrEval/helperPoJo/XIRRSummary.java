package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class XIRRSummary
{
	private double  nettCAGR;
	private double  sumzeroCAGRAlloc;
	private double  boostFactor;
	private boolean adjusted;
	private double  niftyCAGR;
}
