package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSScores
{
	private String                 scCode;
	private double                 mrScore;
	private double                 momentumScore;
	private double                 consolidatedScore;
	private int                    rankCurr;
	private int                    rankPrev;
	private double                 cmp;
	private String                 series;
	private String                 screenerUrl;
	private EnumMCapClassification mcapClassification;
	
}
