package stocktales.tracker.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScTrackerHead
{
	private String  scCode;
	private double  mCap;
	private String  mCapStr;
	private double  mCapR;
	private double  salesL4Q;
	private String  salesL4QStr;
	private double  mCapBySales;
	private double  PECurr;
	private double  PEG;
	private double  UPH;
	private boolean isFinancial;
	
}
