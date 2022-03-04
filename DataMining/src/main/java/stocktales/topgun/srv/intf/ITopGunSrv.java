package stocktales.topgun.srv.intf;

import stocktales.topgun.model.pojo.TopGunContainer;

public interface ITopGunSrv
{
	public TopGunContainer dryRun(int numMonthsinPast, int numWeeksinInterval) throws Exception;

	public void createRebalance(int numWeeksinInterval) throws Exception;

	/**
	 * REfresh Data Pool with Core Portfolio
	 * 
	 * @throws Exception
	 */
	public void refreshListFromCorePF() throws Exception;
}
