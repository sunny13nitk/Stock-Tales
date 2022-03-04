package stocktales.IDS.pojo.UI;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.NFS.enums.EnumMCapClassification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PFHoldingsPL
{
	private String scCode;
	private String sector;
	private int units;
	private double ppu;
	private double cmp;
	private double investments;
	private String invString;
	private double currVal;
	private String currValString;
	private double pl;
	private String plStr;
	private double plPer;
	private double dayPL;
	private double dayPLPer;
	private EnumSMABreach smaLvl;
	private String MCap;
	private EnumMCapClassification mCapClass;
	private double depAmnt;
	private String depAmntStr;
	private double depPer;
	private Date lastBuyDate;
	private Date lastSellDate;

}
