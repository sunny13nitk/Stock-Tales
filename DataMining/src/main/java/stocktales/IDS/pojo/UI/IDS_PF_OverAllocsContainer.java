package stocktales.IDS.pojo.UI;

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
public class IDS_PF_OverAllocsContainer
{
	private double plSum;
	private String plSumStr;
	private double txnSum;
	private String txnSumStr;

	private List<IDS_PF_OverAllocations> overAllocs = new ArrayList<IDS_PF_OverAllocations>();
}
