package stocktales.basket.allocations.autoAllocation.strategy.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.helperPOJO.NameValDouble;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StQuickStats
{
	private int           numScrips;
	private NameValDouble maxAlloc;
	private NameValDouble minAlloc;
}
