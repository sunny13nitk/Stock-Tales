package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_ScSMASpread
{
	private String scCode;
	private List<IDS_SMASpread> prSMAList = new ArrayList<IDS_SMASpread>();
	private boolean notEligibleSMA; // Not Eligible for SMA computation as total price history less than req SMA
}
