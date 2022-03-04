package stocktales.IDS.pojo.UI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_PF_BuyPHeader
{
	private double depAmnt;
	private String depAmntStr;
	private Double dayAmnt;
	private String dayAmntStr;
	private double utlizDay;
	private double shortfall;
	private String scurl; // SmallCase Url maintained in application.properties
}
