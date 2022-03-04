package stocktales.NFS.model.ui;

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
public class NFSExitList
{
	private List<NFSPFExit_UISel> scExit = new ArrayList<NFSPFExit_UISel>();
}
