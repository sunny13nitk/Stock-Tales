package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CAGRPoJo
{
	private double iniVal;
	private double finVal;
	private int    interval;
	private double CAGR;
	
}
