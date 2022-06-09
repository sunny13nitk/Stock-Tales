package stocktales.BackTesting.ATH.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.BackTesting.CoreMi.pojo.BT_Pojo_Transitions;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BT_Pojo_Transitions_ATH extends BT_Pojo_Transitions
{
	public double cashBalance;
	public double cashPerPf;
}
