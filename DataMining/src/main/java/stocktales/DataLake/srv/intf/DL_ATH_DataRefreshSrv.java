package stocktales.DataLake.srv.intf;

import java.util.concurrent.CompletableFuture;

import stocktales.DataLake.model.pojo.UploadStats;

public interface DL_ATH_DataRefreshSrv
{
	public CompletableFuture<UploadStats> refreshDataLake();
}
