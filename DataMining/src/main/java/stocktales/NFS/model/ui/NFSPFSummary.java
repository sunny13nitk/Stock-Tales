package stocktales.NFS.model.ui;

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
public class NFSPFSummary
{
	private List<NFSMCapClass> mcapClass   = new ArrayList<NFSMCapClass>();
	private NFSPFPL            portfolioPL = new NFSPFPL();
	private List<NFSPFTable>   pfTable     = new ArrayList<NFSPFTable>();
	private NFSGainersLosers   pfGainersLosers;
}
