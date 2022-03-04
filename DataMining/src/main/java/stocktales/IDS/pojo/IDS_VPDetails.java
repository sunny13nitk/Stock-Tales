package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumVolatilityProfile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class IDS_VPDetails
{
	private String sccode;
	private int sma1breaches;
	private int sma2breaches;
	private int sma3breaches;
	private int sma4breaches;
	private double volscore;
	private EnumVolatilityProfile volprofile;
}
