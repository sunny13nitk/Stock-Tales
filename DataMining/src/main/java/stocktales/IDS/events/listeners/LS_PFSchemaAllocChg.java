package stocktales.IDS.events.listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import stocktales.IDS.events.EV_PFSchemaAllocChg;

@Service
public class LS_PFSchemaAllocChg implements ApplicationListener<EV_PFSchemaAllocChg>
{

	@Override
	public void onApplicationEvent(EV_PFSchemaAllocChg event)
	{
		/*
		 * Plug in - @Autowire the PF Schema to check POJOS generated; and if so trigger
		 * performance optimized updates
		 */
	}

}
