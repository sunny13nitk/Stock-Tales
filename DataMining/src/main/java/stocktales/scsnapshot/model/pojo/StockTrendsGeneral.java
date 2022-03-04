package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockTrendsGeneral
{
	private String period;
	private double salesG;
	private double patG;
	private double opm;
	private double fcfCfoAvg;
	private double divPayO;
	private double fiRatio;
}
