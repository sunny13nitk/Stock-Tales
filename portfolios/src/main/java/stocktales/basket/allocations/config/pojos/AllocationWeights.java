package stocktales.basket.allocations.config.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllocationWeights
{
	private double wtED; //Earnings and Div Yield weight
	private double wtRR; // ROCE/ROE weight
	private double wtCF; //Avg. FCF/CFO weight
	
}
