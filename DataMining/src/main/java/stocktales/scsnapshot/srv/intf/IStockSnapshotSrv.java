package stocktales.scsnapshot.srv.intf;

import stocktales.scsnapshot.model.pojo.StockSnapshot;

public interface IStockSnapshotSrv
{
	public StockSnapshot getStockSnapshot(
	        String scCode
	) throws Exception;
}
