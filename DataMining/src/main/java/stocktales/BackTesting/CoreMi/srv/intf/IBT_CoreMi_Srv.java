package stocktales.BackTesting.CoreMi.srv.intf;

import java.util.Date;

import stocktales.BackTesting.CoreMi.pojo.BT_Pojo_CoreMi_Container;

public interface IBT_CoreMi_Srv
{
	public BT_Pojo_CoreMi_Container executeCoreMiforDate(Date startDate) throws Exception;
}
