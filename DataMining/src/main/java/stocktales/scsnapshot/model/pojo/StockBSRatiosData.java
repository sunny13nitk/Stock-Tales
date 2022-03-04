package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockBSRatiosData
{
	private String year;
	private int    opm;
	private int    npm;
	private int    roe;
	private int    roce;
	private int    cfoy;
	private int    divp;
	private int    taxper;
}
