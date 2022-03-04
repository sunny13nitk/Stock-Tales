package stocktales.strategy.helperPOJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NiftyStgyCAGR
{
	private final String durationPrefix = "Since Last ";
	private final String durationSuffix = " Yrs.";
	private String       durationVal;
	private double       stgyCAGR;
	private double       niftyCAGR;
}
