package stocktales.scsnapshot.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockFundamentals
{
	private double ROCE3Y;
	private double ROE3Y;
	
	private double recvInvBySales3Y;
	private double intDepByPAT3Y;
	
	private double SGToCapex;
	private double deRatio;
	
	private double divGrowth3Y;
	private double divPayout3Y;
	
	private double avgSSGR3Y;
	private double salesG3Y;
	
	private double PATtoFCF;
	private double REVtoFCF;
	
}
