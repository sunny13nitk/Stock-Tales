package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.pojo.UI.IDS_VP_Dates;

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

	List<IDS_VP_Dates> brechDetails = new ArrayList<IDS_VP_Dates>();
}
