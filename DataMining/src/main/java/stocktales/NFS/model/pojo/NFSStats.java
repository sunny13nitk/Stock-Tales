package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSStats
{
	private int numScripsTotal;
	
	private int numScripsDataAvail;
	
	private int numMcapFltOut;
	
	private int numDurationFltOut;
	
	private int numConsistencyFltOut;
	
	private int numScripsTopN;
	
	private int numSMACMP_Trends_FltOut;
	
	private int CMPFltOut;
	
	private int priceManipulationFltOut;
	
	private int numFinalScrips;
	
	private double RRTopNAvg;
	
	private double RRMin;
	
	private long elapsedMins;
}
