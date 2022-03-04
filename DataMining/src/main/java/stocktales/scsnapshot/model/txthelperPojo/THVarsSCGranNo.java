package stocktales.scsnapshot.model.txthelperPojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class THVarsSCGranNo
{
	public int    ymin;
	public int    ymax;
	public int    delta;
	public int    salesMin;
	public int    salesMax;
	public double salesX;
	public int    debtMin;
	public int    debtMax;
	public double debtToSalesMin;
	public double debtToSalesMax;
	public int    patMin;
	public int    patMax;
	public double patX;
	public int    cashIMin;
	public int    cashIMax;
	public double cashIX;
}
