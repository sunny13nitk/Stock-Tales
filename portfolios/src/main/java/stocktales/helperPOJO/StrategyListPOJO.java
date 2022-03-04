package stocktales.helperPOJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StrategyListPOJO
{
	private int     id;
	private String  name;
	private String  concept;
	private int     numScrips;
	private boolean rebalanceallowed;
	
}
