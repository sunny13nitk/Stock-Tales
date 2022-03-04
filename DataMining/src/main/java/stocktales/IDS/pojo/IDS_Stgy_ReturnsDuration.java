package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_Stgy_ReturnsDuration
{
	private String duration;
	private double minValue;
	private double maxValue;
	private double maxDD;
	private double absRet;
	private double cagrRet;
	private double romad;
	private double stdDev;
	private double sharpeRatio;

}
