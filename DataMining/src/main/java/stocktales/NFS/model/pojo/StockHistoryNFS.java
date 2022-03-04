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
public class StockHistoryNFS
{
	public String                scCode;
	private double               mCap;
	public List<HistoricalQuote> priceHistory = new ArrayList<HistoricalQuote>();
}
