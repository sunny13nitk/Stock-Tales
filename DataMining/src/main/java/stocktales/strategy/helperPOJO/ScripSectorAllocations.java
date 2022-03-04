package stocktales.strategy.helperPOJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScripSectorAllocations
{
	private String sccode;
	
	private String sector;
	
	private double alloc;
}
