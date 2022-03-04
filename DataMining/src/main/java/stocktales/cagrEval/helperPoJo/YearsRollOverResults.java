package stocktales.cagrEval.helperPoJo;

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
public class YearsRollOverResults
{
	private List<YearsFromTo> rollOverYrs = new ArrayList<YearsFromTo>();
	private YearsFromTo       e2eYrs;
}
