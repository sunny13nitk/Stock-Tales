package stocktales.basket.allocations.autoAllocation.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.interfaces.ISrv_FCFSCore;
import stocktales.basket.allocations.autoAllocation.pojos.FCFScore;
import stocktales.repository.Repo_SCBalSheet;
import stocktales.repository.SC10YearRepository;
import stocktales.repository.TrendsRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.services.interfaces.ScripService;

@Service
public class Srv_FCFScore implements ISrv_FCFSCore
{
	@Autowired
	private TrendsRepository repo_scTrends;
	
	@Autowired
	private SC10YearRepository repo_sc10Yr;
	
	@Autowired
	private Repo_SCBalSheet repo_scBalSheet;
	
	@Autowired
	private ScripService scSrv;
	
	private FCFScore fcfScore = new FCFScore();
	
	@Override
	public FCFScore getFCFScorebyScrip(
	        String scCode
	)
	{
		
		if (scCode != null)
		{
			
			getCFOYield(scCode);
			getFCFYield(scCode);
			fcfScore.setScCode(scCode);
		}
		return this.fcfScore;
	}
	
	@Override
	public double getFCFYield(
	        String scCode
	)
	{
		double fcfYield = 0;
		if (scCode != null)
		{
			if (repo_sc10Yr != null && scSrv != null)
			{
				EN_SC_GeneralQ scGen = scSrv.getScripHeader(scCode);
				if (scGen != null)
				{
					Optional<EN_SC_10YData> sc10YO = repo_sc10Yr.findBySCCode(scCode);
					if (sc10YO.isPresent())
					{
						this.fcfScore
						        .setFcfYield(Precision.round((sc10YO.get().getFCF() / scGen.getMktCap()) * 100, 1));
						fcfYield = fcfScore.getFcfYield();
					}
					
				}
			}
		}
		return fcfYield;
	}
	
	@Override
	public double getCFOYield(
	        String scCode
	)
	{
		double cfoYield  = 0;
		double entpValue = 0;
		if (scCode != null)
		{
			if (repo_scBalSheet != null)
			{
				List<EN_SC_BalSheet> balsheetList = repo_scBalSheet.findAllBySCCode(scCode);
				if (balsheetList != null)
				{
					double sumEquity = balsheetList.stream().mapToDouble(EN_SC_BalSheet::getEquity).sum();
					double sumDebt   = balsheetList.stream().mapToDouble(EN_SC_BalSheet::getDebt).sum();
					
					entpValue = sumEquity + sumDebt;
					
				}
			}
			
			if (repo_sc10Yr != null && entpValue > 0)
			{
				Optional<EN_SC_10YData> sc10YO = repo_sc10Yr.findBySCCode(scCode);
				if (sc10YO.isPresent())
				{
					this.fcfScore.setCfoYield(Precision.round((sc10YO.get().getCFOG10() / entpValue) * 100, 1));
					cfoYield = fcfScore.getCfoYield();
				}
			}
		}
		return cfoYield;
	}
	
}
