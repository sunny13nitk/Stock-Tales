package stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn;

import java.util.List;

import stocktales.scripsEngine.enums.SCEenums;

public class ScripUpdateContainerPOJO
{
	private Object objInstance;
	
	private List<String> updFldNames;
	
	private SCEenums.ModeOperation mode;
	
	private String whrClauseFldName;
	
	public Object getObjInstance(
	)
	{
		return objInstance;
	}
	
	public void setObjInstance(
	        Object objInstance
	)
	{
		this.objInstance = objInstance;
	}
	
	public List<String> getUpdFldNames(
	)
	{
		return updFldNames;
	}
	
	public void setUpdFldNames(
	        List<String> updFldNames
	)
	{
		this.updFldNames = updFldNames;
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
	
	public String getWhrClauseFldName(
	)
	{
		return whrClauseFldName;
	}
	
	public void setWhrClauseFldName(
	        String whrClauseFldName
	)
	{
		this.whrClauseFldName = whrClauseFldName;
	}
	
	public ScripUpdateContainerPOJO(
	)
	{
		super();
		
	}
	
}
