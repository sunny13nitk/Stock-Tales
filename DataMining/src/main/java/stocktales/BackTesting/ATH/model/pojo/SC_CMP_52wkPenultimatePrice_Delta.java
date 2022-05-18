package stocktales.BackTesting.ATH.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SC_CMP_52wkPenultimatePrice_Delta
{
	private String scCode;
	private double cmp;
	private double lastYrPrice;
	private double delta;
	private double sma20;
	private double sma50;
	private double sma100;
	private double sma20Delta;
	private double sma50Delta;
	private double sma100Delta;

	@Override
	public String toString()
	{
		return "SC_CMP_52wkPenultimatePrice_Delta [scCode=" + scCode + ", cmp=" + cmp + ", lastYrPrice=" + lastYrPrice
				+ ", delta=" + delta + ", sma20Delta=" + sma20Delta + ", sma50Delta=" + sma50Delta + ", sma100Delta="
				+ sma100Delta + "]";
	}

}
