package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockMCapLast4QData
{
	private double McapToSales;
	
	private int OPM;
	
	private int NPM;
}
