package stocktales.historicalPrices.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockHistory
{
	public String                scCode;
	public List<HistoricalQuote> priceHistory = new ArrayList<HistoricalQuote>();
}
