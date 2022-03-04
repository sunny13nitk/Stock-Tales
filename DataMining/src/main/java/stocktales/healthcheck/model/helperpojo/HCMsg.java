package stocktales.healthcheck.model.helperpojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumEmphasis;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HCMsg
{
	private EnumEffect   effect;
	private EnumEmphasis emphasis;
	private String       msg;
}
