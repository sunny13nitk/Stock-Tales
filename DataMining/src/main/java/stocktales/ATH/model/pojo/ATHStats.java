package stocktales.ATH.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ATHStats
{
	private int totalScrips;

	private int dataError;

	private int mcapFltRemain;

	private int momentumRemain;

	private int numFinalScrips;

	private long elapsedMins;
}
