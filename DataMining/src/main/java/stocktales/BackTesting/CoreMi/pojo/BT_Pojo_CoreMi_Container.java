package stocktales.BackTesting.CoreMi.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.historicalPrices.pojo.StgyRelValuation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BT_Pojo_CoreMi_Container
{
	private List<BT_Pojo_Transitions> pfTransitions = new ArrayList<BT_Pojo_Transitions>();
	private List<StgyRelValuation> valsBaseto100 = new ArrayList<StgyRelValuation>();
}
