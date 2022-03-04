package stocktales.BackTesting.IDS.pojo;

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
public class BT_IP_IDS
{
	private int startSinceLastYrs; // Years to Traverse Back from Today for BackTesting
	private List<BT_ScripAllocs> scAllocs = new ArrayList<BT_ScripAllocs>(); // List of Scrips and Allocations
	private double lumpSumInv; // Lump Sum Investment Amount
	private double sipInfows; // SIP Inflows Amount
	private int sipFrequency; // SIP Frequency in number of Days
	private int vpUpdateFrequency; // Days in which Volatility Profile is to be Updated
	private boolean equiwt; // No Individual allocation distribute equally

}
