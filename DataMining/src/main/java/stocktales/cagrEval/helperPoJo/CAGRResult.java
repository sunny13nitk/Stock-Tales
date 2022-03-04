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
public class CAGRResult
{
	private DurationHeader  durationH;
	private List<XIRRItems> items = new ArrayList<XIRRItems>();
	private XIRRSummary     summary;
}
