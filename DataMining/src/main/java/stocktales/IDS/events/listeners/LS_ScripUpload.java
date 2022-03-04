package stocktales.IDS.events.listeners;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import stocktales.IDS.events.EV_UploadScrip;

@Service
/**
 * 
 * EVENT Listener for Scrip Upload UI Action
 */
public class LS_ScripUpload implements ApplicationListener<EV_UploadScrip>
{

	@Override
	public void onApplicationEvent(EV_UploadScrip evUS)
	{
		if (evUS.getScCode() != null && evUS.getSourceTitle() != null)
		{
			if (evUS.getSourceTitle().equals("PF Schema"))
			{
				// Coming in From PF Schema
				/*
				 * Grab here the Service Reference for PFSchemaSrv and update the POJOS
				 * necessary to Update Buffer - Performance
				 */
			}
		}

	}

}
