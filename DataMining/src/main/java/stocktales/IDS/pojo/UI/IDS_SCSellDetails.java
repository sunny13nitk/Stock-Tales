package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HCI;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SCSellDetails extends HCI
{
	private double txnAmount;
	private String txnAmountStr;
	private double pl;
	private double plStr;
}
