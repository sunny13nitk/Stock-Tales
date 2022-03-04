package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.strategy.helperPOJO.SectorAllocations;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NFSGainersLosers
{
	private int               numPfGainers;
	private int               numPfLosers;
	private int               numDayGainers;
	private int               numDayLosers;
	private SectorAllocations maxGainer;
	private SectorAllocations maxLoser;
}
