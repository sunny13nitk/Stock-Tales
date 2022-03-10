package stocktales.IDS.pojo.UI;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_XIRR_UI
{
	private double xirr;
	private double abspl;
	private String since;
	private int numBuys;
	private int numBuyTxns;
	private int numSells;
	private int numSellTxns;

	private List<IDS_XIRR_Table> xirrTable = new ArrayList<IDS_XIRR_Table>();
	private List<IDS_XIRR_Chart> xirrChart = new ArrayList<IDS_XIRR_Chart>();
}
