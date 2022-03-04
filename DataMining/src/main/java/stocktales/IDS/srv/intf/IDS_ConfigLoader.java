package stocktales.IDS.srv.intf;

import java.util.List;

import stocktales.IDS.model.cfg.entity.IDS_CF_SMARanks;
import stocktales.IDS.model.cfg.entity.IDS_CF_SMAWts;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPDeployments;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPRange;

public interface IDS_ConfigLoader
{
	public List<IDS_CF_SMAWts> getSMAWts();

	public List<IDS_CF_VPDeployments> getVPDeployments();

	public List<IDS_CF_VPRange> getVPRange();

	public List<IDS_CF_SMARanks> getSMARanks();

	public void loadConfig();

}
