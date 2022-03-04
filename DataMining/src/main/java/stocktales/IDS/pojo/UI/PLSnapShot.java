package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PLSnapShot
{
	private double amountPL;
	private String amountPLStr;
	private double perPL;
	private int numGainers;
	private int numLosers;
}
