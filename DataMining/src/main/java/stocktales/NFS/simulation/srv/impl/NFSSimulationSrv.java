package stocktales.NFS.simulation.srv.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.simulation.repo.RepoNFSJornalSM;
import stocktales.NFS.simulation.repo.RepoNFSPFSM;
import stocktales.NFS.simulation.repo.RepoNFSTmpSM;
import stocktales.NFS.simulation.srv.intf.INFSSimulationSrv;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSSimulationSrv implements INFSSimulationSrv
{
	@Autowired
	private RepoNFSJornalSM reponfsJ;

	@Autowired
	private RepoNFSPFSM repoNFSPF;

	@Autowired
	private RepoNFSTmpSM repoNFSTmp;

	private double nettRealz;
	private double iniValue;
	private Calendar startDate;
	private Calendar currDate;
	private Calendar toDate;

	@Override
	public void runSimulation(int numYrsSince, double iniAmnt)
	{
		if (iniAmnt > 0 && numYrsSince > 1)
		{
			setDates(numYrsSince);

			createRebalPF(iniAmnt);
		}

	}

	private void createRebalPF(double iniAmnt)
	{

		if (startDate.before(currDate))
		{
			if (repoNFSPF.count() == 0)
			{
				// create PF
				createPF(iniAmnt);
			} else
			{
				rebalancePF();
			}
			currDate.add(Calendar.DAY_OF_MONTH, 14); // Fortnightly Re-balance
		}
	}

	private void rebalancePF()
	{
		// TODO Auto-generated method stub

	}

	private void createPF(double iniAmnt)
	{

	}

	private void setDates(int numYrsSince)
	{
		toDate = Calendar.getInstance();
		startDate = Calendar.getInstance();
		currDate = Calendar.getInstance();

		startDate.add(Calendar.YEAR, numYrsSince * -1); // from Calendar.Interval Amounts ago
	}

}
