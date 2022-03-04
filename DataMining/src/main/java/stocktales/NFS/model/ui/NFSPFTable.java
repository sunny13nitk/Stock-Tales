package stocktales.NFS.model.ui;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NFSPFTable
{
	private String                 sccode;
	private int                    rankincl;
	private int                    rankcurr;
	private double                 priceincl;
	private double                 cmp;
	private int                    units;
	private double                 plper;
	private double                 plamnt;
	private double                 invAmnt;
	private double                 daysChangePer;
	private double                 daysChangeAmnt;
	private Date                   dateincl;
	private Date                   datelasttxn;
	private EnumMCapClassification mcapClass;
	
}
