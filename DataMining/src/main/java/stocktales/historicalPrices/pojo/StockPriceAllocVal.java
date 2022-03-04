package stocktales.historicalPrices.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceAllocVal
{
	private String scCode;
	private double closePrice;
	private double alloc;
	private double value;
}
