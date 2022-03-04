package stocktales.BackTesting.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BT_EP_IDS
{
	private BT_AD_IDS adminData = new BT_AD_IDS();
	private BT_CALC_IDS calcData = new BT_CALC_IDS();
	// there would be one for REporting
	// BT_REP_IDS

}
