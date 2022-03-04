package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockUnitsPPU
{
	private int units;
	private double ppu;
	private double balAmnt;
}
