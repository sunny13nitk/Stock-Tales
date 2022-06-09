package stocktales.BackTesting.ATH.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;
import stocktales.BackTesting.ATH.srv.intf.IBT_ATH_TopNProposalsGenerator;
import stocktales.DataLake.srv.intf.IDataLakeAccessSrv;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.NFS.repo.RepoBseData;
import stocktales.durations.UtilDurations;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BT_ATH_TopNProposalsGenerator implements IBT_ATH_TopNProposalsGenerator
{
	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private IDataLakeAccessSrv dlAccessSrv;

	@Autowired
	private RepoBseData repoBseData;

	private List<SC_CMP_52wkPenultimatePrice_Delta> proposals = new ArrayList<SC_CMP_52wkPenultimatePrice_Delta>();

	@Override
	public List<SC_CMP_52wkPenultimatePrice_Delta> getProposals(Calendar startDate) throws Exception
	{
		// Not in Future
		if (!startDate.after(UtilDurations.getTodaysDateOnly()))
		{
			if (repoBseData != null && nfsConfig != null)
			{
				if (repoBseData.count() > 0 && nfsConfig.getPfSize() > 0)
				{
					this.proposals.clear();

					for (String scrip : repoBseData.findAllNseCodes())
					{
						SC_CMP_52wkPenultimatePrice_Delta detail = dlAccessSrv.getLastYrPrice_SMA_DeltaByScrip(scrip,
								startDate);
						if (detail != null)
						{
							proposals.add(detail);
						}

					}

					/*
					 * proposals =
					 * dlAccessSrv.getLastYrPrice_SMA_DeltasByScripCodes(repoBseData.findAllNseCodes
					 * (), startDate);
					 */

					if (proposals.size() > 0)
					{
						filter4Momentum();

					}
				}
			}
		}
		return proposals;
	}

	private void filter4Momentum()
	{
		if (this.proposals.size() > 0)
		{
			proposals.removeIf(u -> u.getCmp() < u.getSma20() || u.getCmp() < u.getSma50() || u.getCmp() < u.getSma100()
					|| u.getSma50() < u.getSma100() || u.getSma20() < u.getSma50() || u.getSma20Delta() < 2
					|| u.getSma50Delta() < 10 || u.getSma100Delta() < 20);

		}

		// Sort By Delta From 52 Wk High
		proposals.sort(Comparator.comparingDouble(SC_CMP_52wkPenultimatePrice_Delta::getDelta).reversed());

		// Get Top N Only after Filter
		proposals = proposals.subList(0, nfsConfig.getPfSize());

	}

}
