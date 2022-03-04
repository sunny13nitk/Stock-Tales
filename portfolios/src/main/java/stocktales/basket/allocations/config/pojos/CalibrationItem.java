package stocktales.basket.allocations.config.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Valuations Soothing for Strength Score for too high a Value Ratio to adjust allocation
 * to avoid betting too high in a overpriced Scrip
 * Will be boot Strapped to Consumer via Command Line Runner Interface 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalibrationItem
{
	private double minm;
	private double maxm;
	private double sf;  //Soothing Factor
}
