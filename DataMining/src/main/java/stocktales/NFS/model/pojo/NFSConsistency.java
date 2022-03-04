package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSConsistency
{
	private String scCode;
	
	private int numMonths;
	
	private double monthlyRR;
	
	private double mCap;
}
