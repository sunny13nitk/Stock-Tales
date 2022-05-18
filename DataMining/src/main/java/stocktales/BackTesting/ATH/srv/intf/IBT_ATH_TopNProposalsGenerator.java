package stocktales.BackTesting.ATH.srv.intf;

import java.util.Calendar;
import java.util.List;

import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;

public interface IBT_ATH_TopNProposalsGenerator
{
	public List<SC_CMP_52wkPenultimatePrice_Delta> getProposals(Calendar startDate) throws Exception;
}
