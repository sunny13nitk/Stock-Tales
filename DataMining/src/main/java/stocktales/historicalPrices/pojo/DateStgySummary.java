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
public class DateStgySummary
{
	private String                   date;
	private List<StockPriceAllocVal> stockVals = new ArrayList<StockPriceAllocVal>();
	private double                   totalValue;
	
}
