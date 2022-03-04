package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.historicalPrices.pojo.StgyRelValuation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_Stgy_ReturnsDetails
{
	private String stgyName;
	private List<IDS_Stgy_ReturnsDuration> durDetails = new ArrayList<IDS_Stgy_ReturnsDuration>();
	private List<StgyRelValuation> datePrices = new ArrayList<StgyRelValuation>();

}
