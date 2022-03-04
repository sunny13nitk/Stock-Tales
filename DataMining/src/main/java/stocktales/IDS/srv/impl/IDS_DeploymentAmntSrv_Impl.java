package stocktales.IDS.srv.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.BackTesting.IDS.pojo.BT_PFSchema;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.model.cfg.entity.IDS_CF_VPDeployments;
import stocktales.IDS.model.cfg.repo.IDS_RepoCF_VPDeployments;
import stocktales.IDS.model.pf.entity.PFVolProfile;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.model.pf.repo.RepoPFVolProfile;
import stocktales.IDS.srv.intf.IDS_DeploymentAmntSrv;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_DeploymentAmntSrv_Impl implements IDS_DeploymentAmntSrv
{
	@Autowired
	private RepoPFVolProfile repoPFVolProf;

	@Autowired
	private IDS_RepoCF_VPDeployments repoVPDep;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Override
	public double getDeploymentAmountByScrip4mPF(String scCode, EnumSMABreach smaBreach)
	{
		double depAmnt = 0;
		EnumVolatilityProfile pfVP = null;
		double perSMADep = 0;

		// 1. Get Volatility profile for Scrip
		if (scCode != null && smaBreach != null)
		{
			if (scCode.trim().length() >= 3)
			{
				Optional<PFVolProfile> pfVPO = repoPFVolProf.findById(scCode);
				if (pfVPO.isPresent())
				{
					pfVP = pfVPO.get().getProfile();
					if (pfVP != null)
					{
						Optional<IDS_CF_VPDeployments> vpDO = repoVPDep.findById(pfVP);
						if (vpDO.isPresent())
						{
							IDS_CF_VPDeployments vpDP = vpDO.get();
							if (vpDP != null)
							{
								switch (smaBreach)
								{
								case sma1:
									perSMADep = vpDP.getSma1dep();
									break;
								case sma2:
									perSMADep = vpDP.getSma2dep();
									break;
								case sma3:
									perSMADep = vpDP.getSma3dep();
									break;
								case sma4:
									perSMADep = vpDP.getSma4dep();
									break;

								default:
									break;
								}
							}

							if (perSMADep > 0)
							{
								depAmnt = repoPFSchema.getDeployableAmountforScrip(scCode) * perSMADep * .01;
							}
						}
					}
				}
			}
		}

		return depAmnt;
	}

	@Override
	public double getDeploymentAmountByScrip4mPF(String scCode, EnumSMABreach smaBreach, BT_PFSchema pfSchema,
			EnumVolatilityProfile VP)
	{
		double depAmnt = 0;
		double perSMADep = 0;

		// 1. Get Volatility profile for Scrip
		if (scCode != null && smaBreach != null && pfSchema != null && VP != null)
		{
			if (scCode.trim().length() >= 3)
			{

				Optional<IDS_CF_VPDeployments> vpDO = repoVPDep.findById(VP);
				if (vpDO.isPresent())
				{
					IDS_CF_VPDeployments vpDP = vpDO.get();
					if (vpDP != null)
					{
						switch (smaBreach)
						{
						case sma1:
							perSMADep = vpDP.getSma1dep();
							break;
						case sma2:
							perSMADep = vpDP.getSma2dep();
							break;
						case sma3:
							perSMADep = vpDP.getSma3dep();
							break;
						case sma4:
							perSMADep = vpDP.getSma4dep();
							break;

						default:
							break;
						}
					}

					if (perSMADep > 0)
					{
						depAmnt = pfSchema.getDepamnt() * perSMADep * .01;
					}
				}

			}
		}

		return depAmnt;
	}

}
