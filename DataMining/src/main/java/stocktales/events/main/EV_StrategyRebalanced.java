package stocktales.events.main;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;

/**
 *
 * Application Event for Strategy Re-balance. To be trigerred when a Strategy is Rebalanced
 * Published in StgyRebalanceSrv
 * Handled in LS_StrategryRebalanced
 */

@Getter
public class EV_StrategyRebalanced extends ApplicationEvent
{
	
	private Strategy strategy;
	
	public EV_StrategyRebalanced(
	        Object source, Strategy stgy
	)
	{
		super(source);
		this.strategy = stgy;
	}
	
}
