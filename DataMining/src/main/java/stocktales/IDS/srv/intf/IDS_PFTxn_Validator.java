package stocktales.IDS.srv.intf;

import stocktales.IDS.model.pf.entity.HCI;

public interface IDS_PFTxn_Validator
{
	/**
	 * IS Portfolio Transaction Valid
	 * 
	 * @param pfTxn- POJO of type HCI
	 * @return - true if Txn is valid, false otherwise
	 */
	public boolean isTxnValid(HCI pfTxn) throws Exception;

}
