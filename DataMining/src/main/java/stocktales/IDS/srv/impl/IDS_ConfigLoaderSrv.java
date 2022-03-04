package stocktales.IDS.srv.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.cfg.entity.IDS_CF_SMARanks;
import stocktales.IDS.model.cfg.entity.IDS_CF_SMAWts;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPDeployments;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPRange;
import stocktales.IDS.model.cfg.repo.IDS_RepoCF_SMARanks;
import stocktales.IDS.model.cfg.repo.IDS_RepoCF_SMAWts;
import stocktales.IDS.model.cfg.repo.IDS_RepoCF_VPDeployments;
import stocktales.IDS.model.cfg.repo.IDS_RepoCF_VPRange;
import stocktales.IDS.srv.intf.IDS_ConfigLoader;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_ConfigLoaderSrv implements IDS_ConfigLoader
{
	@Autowired
	private IDS_RepoCF_SMAWts repoSMaWts;
	@Autowired
	private IDS_RepoCF_VPDeployments repoVPDep;
	@Autowired
	private IDS_RepoCF_VPRange repoVPRange;

	@Autowired
	private IDS_RepoCF_SMARanks repoSMARanks;

	private List<IDS_CF_VPRange> VPRange;

	private List<IDS_CF_SMAWts> SMAWts;

	private List<IDS_CF_VPDeployments> VPDeployments;

	private List<IDS_CF_SMARanks> SMARanks;

	@Override
	public void loadConfig()
	{
		if (VPRange == null || SMAWts == null || VPDeployments == null)
		{
			this.SMAWts = repoSMaWts.findAll();
			this.VPDeployments = repoVPDep.findAll();
			this.VPRange = repoVPRange.findAll();
			this.SMARanks = repoSMARanks.findAll();

		}

	}

	@PostConstruct
	public void init()
	{
		loadConfig();
	}

}
