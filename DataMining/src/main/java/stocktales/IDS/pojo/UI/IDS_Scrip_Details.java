package stocktales.IDS.pojo.UI;

import java.util.ArrayList;
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

	private List<HCI> txns = new ArrayList<HCI>();

	private List<IDS_SCSellDetails> sellTxns = new ArrayList<IDS_SCSellDetails>();

	private List<IDS_SCBuyDetails> buyTxns = new ArrayList<IDS_SCBuyDetails>();

	private List<DateAmount> buyChartData = new ArrayList<DateAmount>();

	private List<DateAmount> sellChartData = new ArrayList<DateAmount>();;

	private IDS_PFTxn_UI formData = new IDS_PFTxn_UI();

}
