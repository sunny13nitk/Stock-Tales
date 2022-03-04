package stocktales.scripsEngine.utilities.implementations;

import org.springframework.stereotype.Service;

import stocktales.scripsEngine.utilities.interfaces.IPercentageAdjustService;

@Service
public class PercentageAdjustService implements IPercentageAdjustService
{
	
	@Override
	public double adjustPercentagetoFigure(
	        double Figure, double Percentage
	)
	{
		double adjFigure = 0;
		
		if (Figure != 0 && Percentage != 0)
		{
			adjFigure = Figure + (Percentage / 100 * Figure);
			
		}
		
		return adjFigure;
	}
	
}
