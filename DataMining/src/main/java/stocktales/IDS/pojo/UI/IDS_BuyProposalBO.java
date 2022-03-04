package stocktales.IDS.pojo.UI;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.pojo.IDS_SCBuyProposal;
import stocktales.IDS.pojo.IDS_SMAPreview;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_BuyProposalBO
{
	private IDS_PF_BuyPHeader buyPHeader;
	private List<IDS_SCBuyProposal> buyP = new ArrayList<IDS_SCBuyProposal>();
	private List<IDS_SMAPreview> smaList = new ArrayList<IDS_SMAPreview>();
}
