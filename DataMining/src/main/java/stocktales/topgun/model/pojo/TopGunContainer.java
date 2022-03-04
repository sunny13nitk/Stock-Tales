package stocktales.topgun.model.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.pojo.StockHistoryCmpNFS;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopGunContainer
{
	private List<StockHistoryCmpNFS> dataPool = new ArrayList<StockHistoryCmpNFS>();
	
	private List<ScripIntvReturns> intvReturnsPool = new ArrayList<ScripIntvReturns>();
	
	private List<Date> IntervalsEndDates = new ArrayList<Date>();
	
	private List<IntvScripRetRankConsol> intvReturnConsol = new ArrayList<IntvScripRetRankConsol>();
	
	private List<IntervalStats> intvStats = new ArrayList<IntervalStats>();
}
