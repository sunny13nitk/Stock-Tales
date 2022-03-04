package stocktales.scsnapshot.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.dataBook.model.entity.scLinks.ScLinks;
import stocktales.scripCalc.pojos.TY_ScripCalcResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSnapshot
{
	private StockQuoteBasic quoteBasic;
	
	private ScLinks scLinks;
	
	private ScValuation targetPrice;
	
	private SC_EDRC_Summary summary;
	
	private StockMCapLast4QData last4QData;
	
	private List<StockQtrNos> last10QNos = new ArrayList<StockQtrNos>();
	
	private String latestQ;
	
	private StockFundamentals fundamentals;
	
	private StockTrends trends;
	
	private StockSnapshotMsgs msgs;
	
	private TY_ScripCalcResult scCalcAttrResult;
	
	private List<StockValuationsI> valuations = new ArrayList<StockValuationsI>();
	
	private List<StockWCDetails> wcDetails = new ArrayList<StockWCDetails>();
	
	private StockBalSheetData balSheetData;
	
}
