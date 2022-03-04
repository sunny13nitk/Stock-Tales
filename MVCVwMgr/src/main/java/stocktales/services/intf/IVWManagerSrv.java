package stocktales.services.intf;

public interface IVWManagerSrv
{
	public String getViewName();

	public void setViewName(String vwName);

	public void clearSession();

}
