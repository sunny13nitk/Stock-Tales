package stocktales.topgun.model.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntervalReturns
{
	private int    intvid;
	private Date   endDate;
	private double beginPrice;
	private double endPrice;
	private double returns;
	
}
