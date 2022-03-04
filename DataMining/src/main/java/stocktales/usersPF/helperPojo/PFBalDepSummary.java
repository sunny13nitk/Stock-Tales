package stocktales.usersPF.helperPojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFBalDepSummary
{
	private double cashBal;
	
	private String cashBalS;
	
	private double deployBal;
	
	private String deployBalS;
	
	private int deployPer;
	
}
