package stocktales.NFS.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.ui.NFSPFExit;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSPFExitSS
{
	private double             plExitPer;
	private double             plExitAmnt;
	private String             currInvStr;
	private double             currInv;                                       //From NFSPF REpo
	private String             maxLossStr;
	private double             maxLoss;
	private double             maxLossPer;
	private List<NFSPFExitSMA> pfExitsSMAList = new ArrayList<NFSPFExitSMA>();
	private List<NFSPFExit>    pfExitScrips   = new ArrayList<NFSPFExit>();
	
}
