package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockBSGranularNos
{
	private int    yrint;
	private String year;
	private int    sales;
	private int    pat;
	private int    cfo;
	private int    fcf;
	private int    cashi;
	private int    re;
	private int    debt;
}
