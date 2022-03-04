package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockQtrNos
{
	private String qtr;
	private double sales;
	private double np;
	private double npm;
	private double oi;
}
