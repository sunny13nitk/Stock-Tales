package stocktales.basket.allocations.autoAllocation.valuations.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScValuation
{
	private String scCode;
	private double EDScore;
	private double EDSCcoreB4MoS;
	private double CMP;
	private double strengthScore;
	private double UPH;
	private double MoS;
	private double currEPS;
	private double currPE;
	private double weightedPE;
	private double price5Yr;
	private double ret5YrCAGR;
	private double cashCmpRatio;
	
}
