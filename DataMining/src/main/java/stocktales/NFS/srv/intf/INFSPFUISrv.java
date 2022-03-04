package stocktales.NFS.srv.intf;

import java.util.List;

import stocktales.NFS.model.entity.NFSPF;
import stocktales.NFS.model.ui.NFSNewPF_PREUI;
import stocktales.NFS.model.ui.NFSPFSummary;
import stocktales.NFS.model.ui.NFSRunTmp_UISel;

public interface INFSPFUISrv
{
	public NFSPFSummary getPfSummary() throws Exception;

	public List<NFSRunTmp_UISel> getScripsForSelectionFromSavedProposal();

	public void saveSelScripsinBuffer(List<NFSRunTmp_UISel> scripsUserSelected);

	public NFSNewPF_PREUI getNewPF_PreCreateDetails() throws Exception;

	public void createPF4mExistingProposalSelection(double invAmnt) throws Exception;
}
