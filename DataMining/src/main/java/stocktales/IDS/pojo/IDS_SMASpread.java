package stocktales.IDS.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SMASpread
{
	private Date date;
	private double closePrice;
	private double SMAI1;
	private double SMAI2;
	private double SMAI3;
	private double SMAI4;
}
