package stocktales.BackTesting.ATH.model.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.BackTesting.ATH.enums.EnumBTDurations;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BT_Pojo_ATH_IP
{
	private EnumBTDurations durationEnum;
	private Date startDate;
	private double initialCapital;
	private double sipCapital;
	private int freqSIPdays;
	private int freqREBALdays;

}
