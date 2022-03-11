package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_Scrip_Details_H extends PFHoldingsPL
{
	private double div;
	private double divStr;
	private double realZPl;
	private double realZPlStr;
	private int numBuys;
	private int numSells;
	private double allocWt;
	private double currWt;

}
