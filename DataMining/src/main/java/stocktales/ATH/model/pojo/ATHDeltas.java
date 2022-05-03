package stocktales.ATH.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ATHDeltas
{
	private String sccode;
	private double yearHighDelta;
	private double sma50Delta;
	private double sma200Delta;
}
