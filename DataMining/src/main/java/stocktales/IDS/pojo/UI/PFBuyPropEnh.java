package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumVolatilityProfile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFBuyPropEnh
{
	private int totalUnits;
	private double nppu;
	private double effectppu;
	private double utilz;
	private EnumVolatilityProfile vp;
}
