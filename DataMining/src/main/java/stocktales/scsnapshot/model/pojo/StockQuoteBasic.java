package stocktales.scsnapshot.model.pojo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockQuoteBasic
{
	private String scCode;
	private String sector;
	
	private double uph;      //general
	private double peg;      //general
	private double fcfyield; //10YData
	private double valratio; //Value Ratio - (MCap Change/Retained Earnings) - Last 10 Yrs
	private double cps;      //general		
	
	private double CFOPAT; //10YData
	private double FCFCFO; //10YData
	
	private String name;
	
	private boolean isFinancial;
	
	private BigDecimal MCap;
	
	private String MCapTxt;
	
	private BigDecimal cmp;
	private BigDecimal dma200;
	private BigDecimal dma50;
	
	private BigDecimal High52W;
	
	private BigDecimal Low52W;
	
	private BigDecimal epsCurr;
	
	private BigDecimal peRatio;
	
	private BigDecimal bvps;
	
	private BigDecimal pbRatio;
	
	private BigDecimal perChg200DMA;
	
	private BigDecimal perChg50DMA;
	
	private BigDecimal perChg52WkLow;
	
	private BigDecimal perChg52WkHigh;
	
}
