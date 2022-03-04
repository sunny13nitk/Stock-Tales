package stocktales.BackTesting.IDS.pojo;

import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * All Admin (AD) Stuff - No Business Logic PoJOS
 *
 */
public class BT_AD_IDS
{
	private Calendar calFrom; // Calendar Start Date
	private Calendar calCurr; // Calendar Current Date
	private Calendar calTo; // Calendar End Date
	private Date startDate; // Simulation Start Date
	private long numDaysIterations; // Days Iterated
	private Date currDateLoopPass; // Current Loop Pass Date
	private BT_IP_IDS ip_params; // Import Params - Service Invocation
}
