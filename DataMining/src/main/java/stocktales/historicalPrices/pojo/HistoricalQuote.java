package stocktales.historicalPrices.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalQuote
{
	private String date;
	private Date   dateVal;
	private double closePrice;
}
