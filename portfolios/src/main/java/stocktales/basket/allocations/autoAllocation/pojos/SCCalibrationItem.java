package stocktales.basket.allocations.autoAllocation.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.enums.SCSScoreCalibrationHeaders;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SCCalibrationItem
{
	private SCSScoreCalibrationHeaders calHeader;
	private double                     triggerVal;
	private double                     valB4;
	private double                     valAfter;
	private double                     delta;
}
