package stocktales.healthcheck.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.healthcheck.intf.IHC_GetEvaluationResults;
import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.healthcheck.model.helperpojo.HCEvalResult;
import stocktales.healthcheck.repo.Repo_CfgHC;
import stocktales.healthcheck.repo.intf.IRepoCfgSrvStext;
import stocktales.services.interfaces.ScripService;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Service
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HC_Srv implements IHC_Srv
{
	@Autowired
	private IFactSheetBufferSrv fsBuffSrv;
	
	@Autowired
	private ApplicationContext appCtxt;
	
	@Autowired
	private Repo_CfgHC repoCFG_HC;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	private boolean financialScrip;
	
	private String scCode;
	
	private List<HCComboResult> results = new ArrayList<HCComboResult>();
	
	@Override
	public void Initialize(
	        String scCode
	) throws Exception
	{
		if (scCode != null)
		{
			this.results.clear();
			this.scCode = scCode;
			fsBuffSrv.Initialize(scCode, true);
			if (scSrv != null)
			{
				this.financialScrip = scSrv.isScripBelongingToFinancialSector(scCode);
			}
		}
		
	}
	
	@Override
	public void processScripHealthCheck(
	)
	{
		/*
		 * All the Beans Srv specified in Customizing of Type IHC_GetEvaluationResults to be called
		 * and processed - Return Values to be persisted here in this session Srv
		 */
		
		if (repoCFG_HC != null)
		{
			//Get all Customizing - Unique Services and Criteria List from config Repo
			
			List<IRepoCfgSrvStext> cfgList = repoCFG_HC.getServicesListUnique();
			if (cfgList != null)
			{
				if (cfgList.size() > 0)
				{
					//for each Configuration
					for (IRepoCfgSrvStext cfg : cfgList)
					{
						if (cfg.getSrvname() != null && appCtxt != null)
						{
							//Get Main Service Instance
							IHC_GetEvaluationResults mainSrv = (IHC_GetEvaluationResults) appCtxt
							        .getBean(cfg.getSrvname());
							if (mainSrv != null)
							{
								HCComboResult comboResult = new HCComboResult();
								
								//Prepare and Set Eval Result
								HCEvalResult evalResult = new HCEvalResult();
								evalResult.setStext(cfg.getStext());
								comboResult.setEvalResult(evalResult);
								
								if (financialScrip)
								{
									//If Financial Scrip - Execute Only for Financial Services
									if (cfg.getForFinancials())
									{
										
										comboResult.setBeanProcResult(
										        mainSrv.returnAfterEvaluation(this.scCode, financialScrip));
									}
								} else
								{
									comboResult.setBeanProcResult(
									        mainSrv.returnAfterEvaluation(this.scCode, financialScrip));
								}
								
								this.results.add(comboResult);
							}
						}
						
					}
				}
			}
		}
		
		/*
		 * Conversion Layer to be Set up to Use Each HCBeanReturn(valuetocmp) to evaluate as per Config for
		 * each Service and Populate Corresponding HCEvalResult of Result Entity type HCComboResult
		 * Taken care by an Aspect - SCHealtCheckFinalResultsGenerationAspect
		 */
		
	}
	
}
