package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumPFTxnUI;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_PFTxn_UI
{
	private String scCode;
	private EnumPFTxnUI txnType;
	private int numSharesTxn;
	private double ppuTxn;
	private double divPS; // Dividend per share if any
	private int oneToSplitIntoSharesNum;
	private int foreveryNShares;
	private int toGetSharesNum;

}
