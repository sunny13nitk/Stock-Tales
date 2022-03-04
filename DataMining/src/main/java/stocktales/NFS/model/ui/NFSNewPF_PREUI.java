package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSNewPF_PREUI
{
	private int numScrips;
	private double minInv;
	private double perAlloc;
	private String minInvStr;
	private double currInv;
	private String currInvStr;
}
