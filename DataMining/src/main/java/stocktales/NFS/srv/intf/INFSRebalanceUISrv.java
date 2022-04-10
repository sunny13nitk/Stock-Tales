package stocktales.NFS.srv.intf;

import java.util.List;

/**
 * 
 * NFS UI Rebalance Srv Interface
 *
 */
public interface INFSRebalanceUISrv
{
	public void processRebalance(double incrementalAmnt, List<String> exits, List<String> entries) throws Exception;

}
