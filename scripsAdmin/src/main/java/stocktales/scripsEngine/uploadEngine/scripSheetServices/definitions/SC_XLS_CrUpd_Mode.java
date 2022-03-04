package stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions;

import stocktales.scripsEngine.enums.SCEenums;

/**
 * Scrip Create/Update Mode and other details like Entities to be updated etc.
 *
 */

public class SC_XLS_CrUpd_Mode
{
	private SCEenums.ModeOperation mode;
	
	private String SCCode;
	
	public String getSCCode(
	)
	{
		return SCCode;
	}
	
	public void setSCCode(
	        String sCCode
	)
	{
		SCCode = sCCode;
	}
	
	public SCEenums.ModeOperation getMode(
	)
	{
		return mode;
	}
	
	public void setMode(
	        SCEenums.ModeOperation mode
	)
	{
		this.mode = mode;
	}
	
	public SC_XLS_CrUpd_Mode(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
