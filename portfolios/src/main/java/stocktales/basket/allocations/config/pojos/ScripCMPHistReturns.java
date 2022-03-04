package stocktales.basket.allocations.config.pojos;

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
public class ScripCMPHistReturns
{
	private String sccode;
	private double cmp;
	private List<IntvPriceCAGR> returns = new ArrayList<IntvPriceCAGR>();

}
