package stocktales.factsheet.helperPOJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinMaxBalSheetYear
{
	private String scCode;
	private int    minY;
	private int    maxY;
	private int    delta;
}
