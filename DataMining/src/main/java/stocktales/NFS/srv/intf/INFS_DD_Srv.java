package stocktales.NFS.srv.intf;

import java.util.List;

import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.pojo.NFS_DD4ListScrips;
import stocktales.NFS.model.pojo.ScripPPU;
import stocktales.NFS.model.pojo.ScripPPUUnitsRank;

public interface INFS_DD_Srv
{
	/**
	 * Get Max Draw-down for Scrips as per NFS Algo
	 * 
	 * @param SC_List - List of Scrip Codes - Weight of Each Position :
	 *                equi-weighted as per PF Size
	 * @return - NFS_DD4ListScrips
	 * @throws Exception
	 */
	public NFS_DD4ListScrips getDDByScrips(List<String> SC_List) throws Exception;

	/**
	 * Get Max Draw-down for Scrips as per NFS Algo
	 * 
	 * @param SC_PPU_List- List of Scrip Codes & PPU { per unit acquisition price)-
	 *        Weight of Each Position : equi-weighted as per PF Size
	 * @return - NFS_DD4ListScrips
	 * @throws Exception
	 */

	public NFS_DD4ListScrips getDDByScripsPPU(List<ScripPPU> SC_PPU_List) throws Exception;

	/**
	 * Get Max Draw-down for Scrips as per NFS Algo
	 * 
	 * @param scHoldings - List of Each Scrip , it's PPU and Number of Units
	 *                   acquired
	 * @return - NFSPFExitSS
	 * @throws Exception
	 */
	public NFSPFExitSS getDDByScripsPPUUnits(List<ScripPPUUnitsRank> scHoldings) throws Exception;

}
