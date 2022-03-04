package stocktales.basket.allocations.config.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntvPriceCAGR
{
	private String interval;
	private double price;
	private double CAGR;

}
