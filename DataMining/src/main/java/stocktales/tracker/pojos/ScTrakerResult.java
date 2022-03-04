package stocktales.tracker.pojos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScTrakerResult
{
	private List<ScTrackerHead>   trackerHeaders = new ArrayList<ScTrackerHead>();
	private List<ScTrackerPrices> trackerPrices  = new ArrayList<ScTrackerPrices>();
	private List<ScTrackerRevPAT> trackerRevPATs = new ArrayList<ScTrackerRevPAT>();
}
