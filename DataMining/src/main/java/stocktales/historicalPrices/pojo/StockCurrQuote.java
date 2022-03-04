package stocktales.historicalPrices.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCurrQuote
{
	public String scCode;
	public double currPrice;
}
