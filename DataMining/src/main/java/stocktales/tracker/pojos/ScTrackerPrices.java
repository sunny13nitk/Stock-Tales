package stocktales.tracker.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScTrackerPrices
{
	
	private String cmp;
	private String dma200;
	private String dma50;
	
	private String High52W;
	
	private String Low52W;
}
