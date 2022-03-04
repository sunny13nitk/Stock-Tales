package stocktales.NFS.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yahoofinance.histquotes.HistoricalQuote;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSStockHistoricalQuote
{
	private String                scCode;
	private List<HistoricalQuote> quotesH = new ArrayList<HistoricalQuote>();
}
