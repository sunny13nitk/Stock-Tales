package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.pojo.UI.PFBuyPropEnh;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SCBuyProposal extends PFBuyPropEnh
{
	private String scCode;
	private int numUnitsBuy;
	private double ppuBuy;
	private double amount;
	private String amountStr;
	private EnumSMABreach smaBreach;

}
