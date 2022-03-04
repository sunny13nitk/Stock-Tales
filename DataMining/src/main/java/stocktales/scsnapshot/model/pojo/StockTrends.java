package stocktales.scsnapshot.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockTrends
{
	private List<StockRevWCCycleTrends> revWCTrends = new ArrayList<StockRevWCCycleTrends>();
	
	private List<StockPETrends> peTrends = new ArrayList<StockPETrends>();
	
	private List<StockTrendsGeneral> trendsGen = new ArrayList<StockTrendsGeneral>();
	
}
