package stocktales.NFS.srv.intf;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.pojo.NFSContainer;
import stocktales.NFS.model.pojo.NFSPFExitSS;
import stocktales.NFS.model.ui.NFS_UIRebalProposalContainer;

public interface INFSProcessor
{
	/**
	 * Trigger NFS Portfolio Proposal Generation
	 * 
	 * @param updateDb - true: Update the DB with Proposal in Temporary Data Areas
	 * @return - NFS Container that Contains Complete Information about PF Creation
	 * @throws Exception
	 */
	public CompletableFuture<NFSContainer> generateProposal(boolean updateDb) throws Exception;

	/**
	 * Re-balance the Portfolio in the DB. This re-balancing will run on last
	 * proposal of Momentum Scrips saved in the temporary memory area of DB For Best
	 * results- always generate proposal checking in Dbase Update Option before
	 * executing re-balance. This will also implictly create a new PF for you in
	 * case one is not there The Journal will also be updated on Each Re-balance
	 * that would implictly record the trades
	 * 
	 * @throws Exception
	 */
	public void rebalancePF_DB(double incrementalInvestment, boolean updateDb) throws Exception;

	/**
	 * Re-balance- Incrementally Invest in the Portfolio manually involving user
	 * Selection of Exits/Entries
	 * 
	 * @param incrementalInvestment - Amount to invest incrementally
	 * @throws Exception
	 */
	public NFS_UIRebalProposalContainer rebalancePF_UI(double incrementalInvestment) throws Exception;

	/**
	 * Get the Current NFS Portfolio Exit SMA's and Percentages from CMP as they
	 * would get triggered
	 * 
	 * @return - NFS PF Exit Scenario Details for Current PF
	 * @throws Exception
	 */
	public NFSPFExitSS getPFExitSnapshot(

	) throws Exception;

	/**
	 * Get Number of Scrips that should be Exited Now
	 * 
	 * @return - Number of Scrips that
	 */
	public int getNumExitScrips() throws Exception;

	/**
	 * Create NFS Portfolio from Existing Proposal Selected Scrips that are chosen
	 * by User & passed on from NFSPFUI Srv buffer
	 * 
	 * @param scripsSel - List of Selected Scrips of Data Type NFSRunTmp_UISel
	 * @param invAmnt   - Amount to Invest in Portfolio
	 * @throws Exception
	 */

	public void createPF4mExistingProposalSelection(List<NFSPF> scripsSel, double invAmnt) throws Exception;

	/**
	 * MAss Update the Portfolio with Changed units and/or PPU
	 * 
	 * @param updPF - NFS PF with changes Unit(s)/PPu if any
	 */
	public void massUpdatePF(List<NFSPF> updPF);

	/**
	 * Completely Exit the Portfolio
	 * 
	 * @throws Exception
	 */
	public void exitPortfolio() throws Exception;

	/**
	 * Post Scrip Exit in ExitBook for Anlaytics
	 * 
	 * @param scCode - Scrip to be exited from Portfolio
	 * @throws Exception
	 */
	public void postScripExit(String scCode) throws Exception;

}
