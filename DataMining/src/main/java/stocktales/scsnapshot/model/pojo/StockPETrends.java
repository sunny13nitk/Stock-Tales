package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPETrends
{
	private String ptype;
	private double epsgr;
	private double pegr;
	private double avgpe;
}
