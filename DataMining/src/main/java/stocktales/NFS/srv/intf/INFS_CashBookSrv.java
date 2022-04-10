package stocktales.NFS.srv.intf;

import stocktales.NFS.model.pojo.NFSCB_IP;

public interface INFS_CashBookSrv
{
	public void processCBTxn(NFSCB_IP cbTxn) throws Exception;

	public void processCBTxn(double amountnewPFInv, double maxPerLoss) throws Exception;

	public double getDeployableBalance() throws Exception;
}
