package stocktales.services.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import stocktales.services.intf.IVWManagerSrv;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VWManagerSrv implements IVWManagerSrv
{

	private String viewName;

	@Override
	public String getViewName()
	{
		return this.viewName;
	}

	@Override
	public void setViewName(String vwName)
	{
		this.viewName = vwName;

	}

	@Override
	public void clearSession()
	{
		this.viewName = null;

	}

}
