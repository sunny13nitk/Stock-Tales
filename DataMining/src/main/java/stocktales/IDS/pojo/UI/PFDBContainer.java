package stocktales.IDS.pojo.UI;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.PFSchemaRebalUIStats;
import stocktales.IDS.pojo.XIRRContainer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PFDBContainer
{

	private List<PFHoldingsPL> holdings = new ArrayList<PFHoldingsPL>(); // 1
	private List<IDS_SMAPreview> smaPvwL = new ArrayList<IDS_SMAPreview>(); // 2
	private IDS_BuyProposalBO buyProposals = new IDS_BuyProposalBO(); // 3
	private List<PFSchema> schemaDetails = new ArrayList<PFSchema>(); // 4
	private PFStatsH statsH = new PFStatsH(); // 5
	private PFSchemaRebalUIStats schemaStats = null; // 6
	private XIRRContainer xirrContainer = new XIRRContainer(); // 7

	// On DEmand - Always clear on Invocation and filled in again
	private IDS_PF_OverAllocsContainer overAllocsContainer = new IDS_PF_OverAllocsContainer();

}
