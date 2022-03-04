package stocktales.basket.allocations.autoAllocation.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScripEDRCScore
{
	private String scCode;
	
	private DurVWvNett_ED earningsDivScore;
	
	private DurVWvNett returnRatiosScore;
	
	private DurVWvNett cashflowsScore;
	
	private double edrcScore;
}
