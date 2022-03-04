package stocktales.strategy.helperPOJO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyAllocShort;
import stocktales.basket.allocations.autoAllocation.strategy.interfaces.IStgyShort;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StgyStatsShortSummary
{
	private IStgyShort stgyDetails;
	private int        numSectors;
	
	private int numScrips;
	
}
