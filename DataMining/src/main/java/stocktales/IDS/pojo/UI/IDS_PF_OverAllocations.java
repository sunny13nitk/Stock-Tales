package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_PF_OverAllocations
{
	private String scCode;
	private double depAmnt;
	private String depAmntStr;
	private double depPer;
	private double cmp;
	private int unitsSell;
	private double pl;
	private boolean select;
}
