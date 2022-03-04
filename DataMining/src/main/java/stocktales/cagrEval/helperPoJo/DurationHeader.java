package stocktales.cagrEval.helperPoJo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.cagrEval.enums.EnumDurationType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DurationHeader
{
	private int              from;
	private int              to;
	private EnumDurationType durationType;
}
