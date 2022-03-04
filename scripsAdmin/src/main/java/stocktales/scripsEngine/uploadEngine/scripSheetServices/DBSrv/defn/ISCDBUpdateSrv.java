package stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn;

public interface ISCDBUpdateSrv
{
	public void prepareObjectInfo(
	);
	
	public void prepareQueryString(
	);
	
	public void setQueryParams(
	) throws Exception;
	
	public void ProcessSCUpdate(
	        ScripUpdateContainerPOJO scUpdCont
	) throws Exception;
	
	public int prepareExecuteQuery(
	) throws Exception;
	
	public void getTableName(
	);
}
