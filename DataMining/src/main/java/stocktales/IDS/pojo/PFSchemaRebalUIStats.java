package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.historicalPrices.pojo.StgyRelValuation;
import stocktales.strategy.helperPOJO.SectorAllocations;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFSchemaRebalUIStats
{
	private PFSchemaRebalUIStats_Numbers numbers;
	private List<SectorAllocations> secAllocs = new ArrayList<SectorAllocations>();
	private List<ScripMcapCatg> scMCapList = new ArrayList<ScripMcapCatg>();
	private List<SectorAllocations> mCapAllocs = new ArrayList<SectorAllocations>();
	private List<StgyRelValuation> dateVals = new ArrayList<StgyRelValuation>();
	private List<MCapSpread> mcapSpread = new ArrayList<MCapSpread>();

}
