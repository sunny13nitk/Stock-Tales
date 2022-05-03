package stocktales.ATH.model.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ATHPool
{
	private String sccode;
	private double cmp;
	private double sma50;
	private double sma50Delta;
	private double sma200;
	private double sma200Delta;
	private double yearHigh;
	private double yearHighDelta;
	private double yearLow;
	private double mCapCr;
	private Date lastTradeDate;
	private String series;

}
