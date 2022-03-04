package stocktales.basket.allocations.autoAllocation.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FCFScore
{
	private String scCode;
	private double fcfYield;
	private double cfoYield;
	
}
