package stocktales.BackTesting.ATH.srv.intf;

import java.util.Date;

import stocktales.BackTesting.ATH.model.pojo.BT_Pojo_ATH_Container;

public interface IBT_ATH_Srv
{
	public BT_Pojo_ATH_Container executeATHfromDate(Date startDate) throws Exception;
}
