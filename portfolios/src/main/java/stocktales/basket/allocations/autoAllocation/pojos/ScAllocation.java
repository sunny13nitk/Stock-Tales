package stocktales.basket.allocations.autoAllocation.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScAllocation extends ScValuation
{
	private double  allocation;
	private boolean adhoc;
	
	//Lock feature Extension for reCalibrating Allocations
	
	public ScAllocation(
	        ScValuation scVal
	)
	{
		this.setCMP(scVal.getCMP());
		this.setCurrEPS(scVal.getCurrEPS());
		this.setCurrPE(scVal.getCurrPE());
		this.setEDScore(scVal.getEDScore());
		this.setEDSCcoreB4MoS(scVal.getEDSCcoreB4MoS());
		this.setMoS(scVal.getMoS());
		this.setPrice5Yr(scVal.getPrice5Yr());
		this.setRet5YrCAGR(scVal.getRet5YrCAGR());
		this.setScCode(scVal.getScCode());
		this.setStrengthScore(scVal.getStrengthScore());
		this.setUPH(scVal.getUPH());
		this.setWeightedPE(scVal.getWeightedPE());
		this.setStrengthScore(scVal.getStrengthScore());
		
	}
	
}
