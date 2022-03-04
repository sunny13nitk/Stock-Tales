package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSPFExitSMA
{
	private String scCode;
	private int    rank;
	private double priceIncl;
	private double priceExit;
	private double priceCmp;
	private double cmpExitDelta;
	private double plExit;
	
}
