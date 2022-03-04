package stocktales.NFS.model.ui;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRebalExit
{
	private String  sccode;
	private int     rankincl;
	private double  priceincl;
	private int     rankcurr;
	private Date    dateincl;
	private Date    datelasttxn;
	private int     units;
	private boolean exclPF;
	
}
