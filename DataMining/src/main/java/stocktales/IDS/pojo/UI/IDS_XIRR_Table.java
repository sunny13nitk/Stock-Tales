package stocktales.IDS.pojo.UI;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_XIRR_Table
{
	private Date date;
	private String dateStr;
	private double cf;
	private String cfStr;
	private double per;
}
