package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRebalStats
{
	private int numCPS;
	private int numRC;
	private int numRT;
	private int totalScrips;

	private double tad; // total Amount at Disposal
	private double arc; // amount realized due to Exits
	private double ppi; // per position Investment
	private double runningTotals; // running Total with current deployment pass for each Scrip
	private double rtia; // post retention table investible amount - new Scrips
	private double pprt; // per position Investment post Retention Table - new Scrips

}
