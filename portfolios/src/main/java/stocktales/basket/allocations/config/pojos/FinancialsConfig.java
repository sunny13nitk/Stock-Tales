package stocktales.basket.allocations.config.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialsConfig
{
	private double UPH;
	private double ROE;
	private double BoostBest;
	private double BoostROE;
	private double BoostBase;
}
