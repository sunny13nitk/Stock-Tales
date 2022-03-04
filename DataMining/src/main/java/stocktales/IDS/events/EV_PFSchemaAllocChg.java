package stocktales.IDS.events;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * PF Schema Allocation Changed Event - Blank Hook with no Object being passed
 */
public class EV_PFSchemaAllocChg extends ApplicationEvent
{

	public EV_PFSchemaAllocChg(Object source)
	{
		super(source);

	}

}
