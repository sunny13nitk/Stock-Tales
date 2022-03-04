package stocktales.NFS.model.ui;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFS_UIRebalProposalContainer
{
	private String currInvStr;
	private String invAmntStr;
	private double invAmnt;
	private int numCurrPFScrips;
	private int numIdealScrips;
	private int numProposals;
	private int numExits;
	private NFSExitList exitsList;
	private NFSRunTmpList proposals;
}
