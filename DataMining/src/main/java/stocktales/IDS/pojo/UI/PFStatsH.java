package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PFStatsH
{
	private double totalInv;
	private String totalInvStr;
	private double currVal;
	private String currValStr;
	private double amntUtilPer;
	private PLSnapShot pfPLSS = new PLSnapShot();
	private PLSnapShot todayPLSS = new PLSnapShot();
	private ScripPLSS maxGainer = new ScripPLSS();
	private ScripPLSS maxLoser = new ScripPLSS();

}
