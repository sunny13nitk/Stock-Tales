package stocktales.BackTesting.CoreMi.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.BackTesting.CoreMi.enums.EnumState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BT_Pojo_Transitions
{
	private Date date;
	private List<BT_Pojo_ScContbn> scContributions = new ArrayList<BT_Pojo_ScContbn>();
	private int numChangesProposed;
	private int numChangesExecuted;
	private double plPer;
	private double plAmount;
	private EnumState state;
	private double baseto100Val;

}
