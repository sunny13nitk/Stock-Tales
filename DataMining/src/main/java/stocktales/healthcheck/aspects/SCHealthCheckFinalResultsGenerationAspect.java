package stocktales.healthcheck.aspects;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import stocktales.healthcheck.intf.IHC_Srv;
import stocktales.healthcheck.model.entity.Cfg_ScripHealthCheck;
import stocktales.healthcheck.model.helperpojo.HCComboResult;
import stocktales.healthcheck.model.helperpojo.HCEvalResult;
import stocktales.healthcheck.repo.Repo_CfgHC;
import stocktales.healthcheck.repo.intf.IRepoCfgSrvStext;

@Aspect
@Service
public class SCHealthCheckFinalResultsGenerationAspect
{
	@Autowired
	private Repo_CfgHC repoHC;
	
	@Autowired
	private MessageSource msgSrc;
	
	private IHC_Srv hcSrv;
	
	List<HCComboResult> results = null;
	
	/*
	 * Used Around as we will adjust the HCEvalResult part of all HCComboResult
	 */
	
	@SuppressWarnings("unchecked")
	@Around("getScripHeatlhCheckResults()")
	public Object prepareFinalResultsasPerConfig(
	        ProceedingJoinPoint pjp
	) throws Throwable
	{
		if (pjp != null)
		{
			System.out.println("Results Consolidation Aspect Triggered!");
			
			/*
			 * Ensure Invoking Target is an implementation of IHC_Srv
			 */
			
			if (pjp.getTarget() instanceof IHC_Srv)
			{
				this.hcSrv = (IHC_Srv) pjp.getTarget();
				if (hcSrv != null)
				{
					//Get the results Obtained so Far- before Conversion for Main Service populates HCEvalResult
					this.results = (List<HCComboResult>) pjp.proceed();
					if (results != null)
					{
						if (results.size() > 0)
						{
							// List of Unique Configured Services
							List<IRepoCfgSrvStext> hcConfigList = repoHC.getServicesListUnique();
							if (hcConfigList != null)
							{
								if (hcConfigList.size() > 0)
								{
									//Looping on configs
									for (IRepoCfgSrvStext config : hcConfigList)
									{
										//Get Relevant Result as per Config
										Optional<HCComboResult> resultO = this.results.stream()
										        .filter(x -> x.getEvalResult().getStext().equals(config.getStext()))
										        .findFirst();
										if (resultO.isPresent())
										{
											HCComboResult result = resultO.get();
											if (result != null)
											{
												//Get all configs for Current Stext - ENumHCCriteria
												List<Cfg_ScripHealthCheck> currConfigs = repoHC
												        .findByStext(result.getEvalResult().getStext());
												if (currConfigs != null)
												{
													if (currConfigs.size() > 0)
													{
														double baseVal = result.getBeanProcResult().getValueToCmp();
														
														for (Cfg_ScripHealthCheck configI : currConfigs)
														{
															switch (configI.getCmpsign())
															{
																case EQ:
																	if (baseVal == configI.getBaseval())
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	break;
																case NE:
																	if (!(baseVal == configI.getBaseval()))
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	
																	break;
																
																case GE:
																	if (baseVal >= configI.getBaseval())
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	break;
																
																case GT:
																	if (baseVal > configI.getBaseval())
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	break;
																
																case LE:
																	if (baseVal <= configI.getBaseval())
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	break;
																
																case LT:
																	if (baseVal < configI.getBaseval())
																	{
																		//Populate EvalResult for curr configI and base Val
																		result.setEvalResult(
																		        populateEvalResult(configI, baseVal));
																	}
																	break;
																
																default:
																	break;
															}
														}
													}
												}
											}
										}
										
									}
								}
							}
						}
					}
				}
			}
			
		}
		
		return this.results; //REfurbrished Results - Completed with Conversions
	}
	
	/*
	 * Populate HCEvalResult for ConfigI and base Val
	 */
	
	private HCEvalResult populateEvalResult(
	        Cfg_ScripHealthCheck configI, double baseVal
	)
	{
		
		HCEvalResult evalRes = new HCEvalResult();
		
		evalRes.setStext(configI.getStext());
		evalRes.setBaseValue(configI.getBaseval());
		
		if (configI.getMsgkey() != null)
		{
			evalRes.setMsg(msgSrc.getMessage(configI.getMsgkey(), new Object[]
			{ baseVal }, Locale.ENGLISH));
		}
		
		if (configI.getRepkey() != null)
		{
			evalRes.setRepercursion(msgSrc.getMessage(configI.getRepkey(), null, Locale.ENGLISH));
		}
		
		if (configI.getTagkey() != null)
		{
			evalRes.setTagline(msgSrc.getMessage(configI.getTagkey(), null, Locale.ENGLISH));
		}
		
		evalRes.setEffect(configI.getEffect());
		evalRes.setEmphasis(configI.getEmphasis());
		
		return evalRes;
		
	}
	
	// POINTCUTS
	/*
	 * On Process of Scrip Health Check Getting Results
	 */
	
	@Pointcut("execution( public *  stocktales.healthcheck.impl.HC_Srv.getResults())")
	public void getScripHeatlhCheckResults(
	)
	{
		
	}
	
}
