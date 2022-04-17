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
public class IDSOverAllocList
{
	private List<IDS_PF_OverAllocations> overAllocList = new ArrayList<IDS_PF_OverAllocations>();
}
