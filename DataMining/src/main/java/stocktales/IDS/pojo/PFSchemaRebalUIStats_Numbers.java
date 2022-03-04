package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFSchemaRebalUIStats_Numbers
{
	private int numScrips;
	private int numSectors;
	private double sumIdealAlloc;
	private double sumIncAlloc;
	private double sumtop5Scrips;
	private double minmInv;
	private String minmInvStr;
}
