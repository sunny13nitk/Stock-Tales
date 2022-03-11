package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SMADeltas
{
	private double lastPurchaseDelta;
	private double SMA1Delta;
	private double SMA2Delta;
	private double SMA3Delta;
	private double SMA4Delta;
}
