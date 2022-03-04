package stocktales.NFS.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.historicalPrices.pojo.HistoricalQuote;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockHistoryCmpNFS
{
	public String                scCode;
	private double               cmp;
	private double               sma50;
	public List<HistoricalQuote> priceHistory = new ArrayList<HistoricalQuote>();
}
