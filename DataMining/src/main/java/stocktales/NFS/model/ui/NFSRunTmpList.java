package stocktales.NFS.model.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRunTmpList
{
	private List<NFSRunTmp_UISel> scSel = new ArrayList<NFSRunTmp_UISel>();
}
