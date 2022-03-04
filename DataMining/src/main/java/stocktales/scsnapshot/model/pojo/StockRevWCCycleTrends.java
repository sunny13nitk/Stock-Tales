package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockRevWCCycleTrends
{
	private String period;
	
	private double revenue;
	private double recv;
	private double wcByrev;
	private double ssgr;
}
