package stocktales.siteconfig.interfaces;

/**
 * Navigation REdirect Service to Implicitly Set a Destination Path from one of
 * the Configured SitePath(s)
 *
 */
public interface INavigationRedirectSrv
{
	public void setPathTitle(String tgtPathTitle);

	public String getPathUrl();

	public void setScCode(String scCode);

	public void clear();

	public void publishScripUploadEvent(String srcNavTitle);
}
