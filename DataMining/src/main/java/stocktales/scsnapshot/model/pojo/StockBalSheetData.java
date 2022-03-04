package stocktales.scsnapshot.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockBalSheetData
{
	private List<StockBSGranularNos> balSheetGrData = new ArrayList<StockBSGranularNos>();
	
	private List<StockBSPerShareData> epsData = new ArrayList<StockBSPerShareData>();
	
	private List<StockBSRatiosData> balSheetRatios = new ArrayList<StockBSRatiosData>();
	
}
