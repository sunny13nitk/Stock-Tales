package stocktales.healthcheck.model.helperpojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumEmphasis;
import stocktales.healthcheck.enums.EnumHCCriteria;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HCEvalResult
{
	private EnumHCCriteria stext;
	private double         baseValue;
	private EnumEffect     effect;
	private EnumEmphasis   emphasis;
	private String         msg;
	private String         repercursion;
	private String         tagline;
}
