package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSIncAlloc
{
	private boolean newPF;
	private boolean exisProp;
	private boolean newProp;
	private double incInvestment;
	private double minInv;

}
