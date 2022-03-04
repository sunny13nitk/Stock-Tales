package stocktales.basket.allocations.autoAllocation.pojos;

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
public class DurVWvNett
{
	
	private double nettValue;
	
	private List<DurVWv> durItems = new ArrayList<DurVWv>();
	
}
