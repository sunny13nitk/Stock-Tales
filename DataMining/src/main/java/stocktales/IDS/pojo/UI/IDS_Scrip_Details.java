package stocktales.IDS.pojo.UI;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.pojo.DateAmount;
import stocktales.IDS.pojo.IDS_CMPLastBuySMASpread;
import stocktales.IDS.pojo.IDS_SMADeltas;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_Scrip_Details
{
	private IDS_Scrip_Details_H scripDetailsH;

	private IDS_CMPLastBuySMASpread cmpLastBuySMASpread;

	private IDS_SMADeltas cmpDeltas;

	private List<HCI> txns;

	private List<IDS_SCSellDetails> sellTxns;

	private List<IDS_SCBuyDetails> buyTxns;

	private List<DateAmount> buyChartData;

	private List<DateAmount> sellChartData;

}
