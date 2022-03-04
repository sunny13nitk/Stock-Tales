package stocktales.basket.allocations.autoAllocation.facades.pojos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.pojos.SCCalibrationItem;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SC_EDRC_Summary
{
	private String scCode;
	private String sector;
	private ScripEDRCScore scEDRC;
	private double avWtED;
	private double avWtRR;
	private double avWtCF;
	private double FCFYield;
	private double CFOYield;
	private double EDRC;
	private double valR;
	private double patCFOAdh;
	private double strengthScoreb4Calibration;
	private double strengthScore;

	private List<SCCalibrationItem> calibrations = new ArrayList<SCCalibrationItem>();
}
