package stocktales.topgun.model.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntervalStats
{
	private int          jid;
	private Date         endDate;                                //End Date - Trigger Date
	private double       allEquwRet;
	private double       topNRealized;
	private double       topNUnrealized;
	private int          numExits;
	private double       perChurn;
	private List<String> scripsPresent = new ArrayList<String>();
	private List<String> scripsExited  = new ArrayList<String>();
}
