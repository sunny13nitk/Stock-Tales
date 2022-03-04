package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSReplacement
{
	private String  sccode;
	private double  consolscore;
	private int     rank;
	private String  screneerUrl;
	private boolean inclInPf;   //Switch - User selection
}
