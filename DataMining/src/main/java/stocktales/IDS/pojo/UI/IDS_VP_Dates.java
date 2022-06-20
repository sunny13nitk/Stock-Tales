package stocktales.IDS.pojo.UI;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumSMABreach;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_VP_Dates
{
	private EnumSMABreach breach;
	private Date dateBreach;
	private double priceBreach;
}
