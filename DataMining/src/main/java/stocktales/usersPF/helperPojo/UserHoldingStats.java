package stocktales.usersPF.helperPojo;

import org.apache.commons.math3.util.Precision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.usersPF.model.Holding;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserHoldingStats extends Holding
{
	private double totalAlloc;
	private String totalAllocS;
	private double allocPer;
	private String allocPerS;
	private String realzDivS;
	private String realzPLS;
	
	public void copyfromHolding(
	        Holding holding
	)
	{
		this.setHid(holding.getHid());
		this.setPpu(Precision.round(holding.getPpu(), 0));
		
		this.setRealzdiv(holding.getRealzdiv());
		this.setRealzDivS(UtilDecimaltoMoneyString.getMoneyStringforDecimal(this.getRealzdiv(), 1));
		
		this.setRealzpl(holding.getRealzpl());
		this.setRealzPLS(UtilDecimaltoMoneyString.getMoneyStringforDecimal(this.getRealzpl(), 1));
		
		this.setSccode(holding.getSccode());
		this.setUnits(holding.getUnits());
		
		this.setUserStrategy(holding.getUserStrategy());
		
	}
}
