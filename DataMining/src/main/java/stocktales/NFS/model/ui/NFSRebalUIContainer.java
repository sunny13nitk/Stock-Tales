package stocktales.NFS.model.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRebalUIContainer
{
	private double               incAmnt;                                              //Incremental Amount
	private List<NFSReplacement> newEntryCandidates = new ArrayList<NFSReplacement>(); //New Entry Candidates to Choose from
	private List<NFSRebalExit>   exitCandidates     = new ArrayList<NFSRebalExit>();
	
}
