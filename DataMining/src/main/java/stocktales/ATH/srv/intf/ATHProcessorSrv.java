package stocktales.ATH.srv.intf;

import java.util.concurrent.CompletableFuture;

import stocktales.ATH.model.pojo.ATHContainer;

public interface ATHProcessorSrv
{
	public CompletableFuture<ATHContainer> generateProposal(boolean updateDb) throws Exception;
}
