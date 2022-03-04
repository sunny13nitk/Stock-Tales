package stocktales.basket.allocations.autoAllocation.pojos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScAllocationList
{
	private int                stgyId;
	private List<ScAllocation> scAllocations = new ArrayList<ScAllocation>();
}
