package stocktales.scripCalc.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.scripCalc.enums.EnumAttrRole;
import stocktales.scripCalc.enums.EnumDataType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TY_EntAttrValue
{
	private EnumDataType dataType;
	private Object       value;
	private String       attrName;
	private EnumAttrRole role;
}
