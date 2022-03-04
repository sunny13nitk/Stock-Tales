package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SCBuyProposalDetails
{
	private String scCode;
	private int numUnitsBuy;
	private double ppuBuy;
	private double totalInv;
	private String totalInvStr;
	private int totalUnits;
	private double currPFWt;
	private double b4TxnWt;
	private double plPostBuy;
}
