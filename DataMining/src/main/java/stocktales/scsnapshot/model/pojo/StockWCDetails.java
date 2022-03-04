package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockWCDetails
{
	private String year;
	private double capextoSales;
	private double debttoSales;
	private double nfatoSales;
	private double nfat;
	private int    itr;
	private int    recvd;
}
