package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StAllocItem;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.Strategy;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.entity.StgyRebalancingTexts;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.enums.ERebalType;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalTxts_Simple;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.interfaces.IStgyRebalanceSrv;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos.StgyRebalance;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.repo.RepoStgyRebalancingTexts;
import stocktales.basket.allocations.autoAllocation.strategy.repo.IRepoStrategy;
import stocktales.dataBook.model.repo.adhocScrips.RepoAdhocScrip;
import stocktales.events.main.EV_StrategyRebalanced;
import stocktales.predicates.GenericSCEDRCSummaryPredicate;
import stocktales.predicates.manager.PredicateManagerImpl;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

/*
 * Session Scoped Service for Strategy Re-Balancing
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StgyRebalanceSrv implements IStgyRebalanceSrv
{
	
	@Autowired
	private IRepoStrategy repoStgy;
	
	@Autowired
	private RepoAdhocScrip repoAdhocSc;
	
	@Autowired
	private ApplicationEventPublisher evPub;
	
	@Autowired
	private RepoStgyRebalancingTexts repoStgyRblTxts;
	
	@Autowired
	private EDRCFacade edrcFacSrv;
	
	@Autowired
	private PredicateManagerImpl predManager;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private IScAllocationListRepo scAllocRepoSrv;
	
	private StgyRebalance rblPojo;
	
	@Override
	public StgyRebalance triggerReBalancingforStgy(
	        int stgId
	)
	{
		this.rblPojo = null; //Clear on Start
		
		// 1. Populate the Basic Data
		
		populateBasicData(stgId);
		
		//2. Populate Proposals for Additions and REmovals
		populateProposals();
		
		//3. Populate REbalancing Texts
		populateProposalswithTexts();
		
		return rblPojo;
	}
	
	/*
	 * Add New Scrip to the Session Object
	 */
	@Override
	public boolean addNewScrip(
	        String scCode
	)
	{
		boolean added = false;
		
		//If coming from Proposed Additions - Remove from Proposed Additions 
		
		if (scCode != null)
		{
			if (this.rblPojo.getPropAdditions().size() > 0)
			{
				Optional<String> propO = this.rblPojo.getPropAdditions().stream().filter(x -> x.equals(scCode))
				        .findFirst();
				if (propO.isPresent())
				{
					this.rblPojo.getPropAdditions().remove(scCode);
					
					this.rblPojo.getPropwithTxtsAdd().removeIf(x -> x.getSccode().equals(scCode));
				}
			}
			
			//Add it to Current Scrips - only if already not a part of
			//Can come from Direct Scrip Selection too - Adhoc 
			Optional<String> currO = this.rblPojo.getCurrScrips().stream().filter(x -> x.equals(scCode)).findFirst();
			if (!currO.isPresent())
			{
				this.rblPojo.getCurrScrips().add(scCode);
				added = true;
			}
			
		}
		
		return added;
	}
	
	/*
	 * Remove Scrip from the Session Object
	 */
	@Override
	public boolean deleteScrip(
	        String scCode
	)
	{
		boolean removed = false;
		
		//If coming from Proposed Removals - Remove from Proposed Removals
		
		if (scCode != null)
		{
			if (this.rblPojo.getPropRemovals().size() > 0)
			{
				Optional<String> propO = this.rblPojo.getPropRemovals().stream().filter(x -> x.equals(scCode))
				        .findFirst();
				if (propO.isPresent())
				{
					this.rblPojo.getPropRemovals().remove(scCode);
					
					this.rblPojo.getPropwithTxtsRemove().removeIf(x -> x.getSccode().equals(scCode));
				}
				
			}
			
			//Remove it from Current Scrips - only if already not a part of
			//Can come from Direct Scrip Selection too - Adhoc 
			Optional<String> currO = this.rblPojo.getCurrScrips().stream().filter(x -> x.equals(scCode)).findFirst();
			if (currO.isPresent())
			{
				this.rblPojo.getCurrScrips().remove(scCode);
				removed = true;
			}
			
		}
		
		return removed;
	}
	
	@Override
	public void saveStrategy(
	        Strategy Stgy
	)
	{
		Strategy stgy = null;
		
		if (this.rblPojo != null && this.scAllocRepoSrv != null && Stgy != null)
		{
			if (this.rblPojo.getStid() > 0 && this.scAllocRepoSrv.getScAllocList() != null)
			{
				if (this.scAllocRepoSrv.getScAllocList().size() > 0)
				{
					//GEt Strategy from DB
					Optional<Strategy> stgyO = repoStgy.findByStid(this.rblPojo.getStid());
					if (stgyO.isPresent())
					{
						
						stgy = stgyO.get();
						
						// Update the Header first
						
						stgy.setName(Stgy.getName());
						stgy.setConcept(Stgy.getConcept());
						stgy.setActive(Stgy.isActive());
						stgy.setRebalanceallowed(Stgy.isRebalanceallowed());
						stgy.setLastupdate(new java.sql.Date(System.currentTimeMillis()));
						
						//Now Update the Allocations
						
						//Start reading from Buffer
						for (ScAllocation scAlloc : this.scAllocRepoSrv.getScAllocList())
						{
							//Find for Current scrip in Strategy Allocations
							Optional<StAllocItem> allocIO = stgy.getAllocItems().stream()
							        .filter(x -> x.getSccode().equals(scAlloc.getScCode())).findFirst();
							
							if (allocIO.isPresent())
							{
								//Found -Update
								StAllocItem allocItem = allocIO.get();
								allocItem.setAlloc(scAlloc.getAllocation()); //Update Allocation Percentage
								allocItem.setMos(scAlloc.getMoS()); //Set MoS
								
							} else
							{
								//Not found - Insert
								
								//Create POJO
								StAllocItem allocItem = new StAllocItem();
								
								if (repoAdhocSc.findBySccodeIgnoreCase(scAlloc.getScCode()).isPresent())
								{
									allocItem.setAdhoc(true);
								}
								
								//Set Properties			
								allocItem.setSccode(scAlloc.getScCode());
								allocItem.setMos(scAlloc.getMoS());
								allocItem.setAlloc(scAlloc.getAllocation());
								
								//Add to Strategy
								stgy.addAllocationItem(allocItem);
								
							}
							
						}
						
						//Also need to take care of deleting the ones that are in Dbase but not in buffer - Deleted
						//Read DB Ones
						
						//Add to a seperate List the ones that need deletion
						List<String> tobeDeleted = new ArrayList<String>();
						
						for (StAllocItem scAllocI : stgy.getAllocItems())
						{
							Optional<ScAllocation> scAllocO = this.scAllocRepoSrv.getScAllocList().stream()
							        .filter(x -> x.getScCode().equals(scAllocI.getSccode())).findFirst();
							if (scAllocO.isPresent())
							{
								//Do Nothing - already Updated as Above
							} else
							{
								scAllocI.setStrategy(null); //Dis-associate the relation
								//These needs to be deleted from DB
								this.rblPojo.getTobeDeleted().add(scAllocI.getAllocid());
								tobeDeleted.add(scAllocI.getSccode());
							}
						}
						
						if (tobeDeleted.size() > 0)
						{
							for (String scCode : tobeDeleted)
							{
								stgy.getAllocItems().removeIf(x -> x.getSccode().equals(scCode));
							}
						}
						
						try
						{
							
							//Trigger the Event if valid Strategy REturned
							if (stgy != null)
							{
								//SAve  In the event within @Transactional for Rollback in case of any error
								EV_StrategyRebalanced event_rebalOK = new EV_StrategyRebalanced(this, stgy);
								evPub.publishEvent(event_rebalOK);
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						
					}
					
				}
			}
		}
		
	}
	
	/*
	 * -------------------------------------------------------------------------------------
	 *                                   PRIVATE METHODS
	 * --------------------------------------------------------------------------------------
	 */
	
	private void populateBasicData(
	        int stgId
	)
	{
		if (stgId > 0 && repoStgy != null)
		{
			
			//Look up for Strategy
			
			Optional<Strategy> stgyObjO = repoStgy.findById(stgId);
			if (stgyObjO.isPresent())
			{
				Strategy stgy = stgyObjO.get();
				if (stgy != null)
				{
					this.rblPojo = new StgyRebalance();
					this.rblPojo.setStid(stgId);
					this.rblPojo.setStgyDesc(stgy.getName());
					
					//1. Populate Existing Strategy Scrips
					stgy.getAllocItems().stream().filter(x -> this.rblPojo.getStgyScrips().add(x.getSccode()))
					        .collect(Collectors.toList());
					
					//2. Initialize Current Scrips too with Strategy Scrips to begin with
					this.rblPojo.setCurrScrips(rblPojo.getStgyScrips());
					
					//3. Populate the Predicate Filtered Scrips
					if (stgy.getPredicatebean() != null)
					{
						//Get the PRedicate Bean REf Handle
						GenericSCEDRCSummaryPredicate predBean = predManager
						        .getActivePredicateBean(stgy.getPredicatebean());
						if (predBean != null)
						{
							try
							{
								List<SC_EDRC_Summary> predScrips = edrcFacSrv
								        .getEDRCforSCripsList(scExSrv.getAllScripNames(), predBean);
								if (predScrips != null)
								{
									if (predScrips.size() > 0)
									{
										predScrips.stream().filter(x -> this.rblPojo.getPredScrips().add(x.getScCode()))
										        .collect(Collectors.toList());
									}
								}
							} catch (EX_General e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				}
			}
		}
		
	}
	
	private void populateProposals(
	)
	
	{
		if (rblPojo.getPredScrips().size() > 0 && rblPojo.getStgyScrips().size() > 0)
		{
			rblPojo.setPropAdditions(
			        
			        rblPojo.getPredScrips().stream().filter(
			                preds -> rblPojo.getStgyScrips().stream().noneMatch(stgy -> stgy.equals(preds)
			                )).collect(Collectors.toList())
			);
			
			rblPojo.setPropRemovals(
			        
			        rblPojo.getStgyScrips().stream().filter(
			                stgy -> rblPojo.getPredScrips().stream().noneMatch(preds -> preds.equals(stgy)
			                )).collect(Collectors.toList())
			);
			
		}
	}
	
	private void populateProposalswithTexts(
	)
	{
		//Get From Repo Consolidated List of Additions or REmovals
		List<IStgyRebalTxts_Simple> rblTxts = repoStgyRblTxts
		        .findSimpleStgRebalTxts_ByStid(this.getRblPojo().getStid());
		
		//Additions First
		for (String scriptobeAdded : rblPojo.getPropAdditions())
		{
			//Scan the Repo First for The Scrip in Question
			Optional<IStgyRebalTxts_Simple> rbTxt = rblTxts.stream().filter(x -> x.getSccode().equals(scriptobeAdded))
			        .findFirst();
			if (rbTxt.isPresent())
			{
				//Push to Collection
				StgyRebalancingTexts rblTxtObj = new StgyRebalancingTexts();
				rblTxtObj.setRbid(rbTxt.get().getRbid());
				rblTxtObj.setRbaltype(rbTxt.get().getRbaltype());
				rblTxtObj.setReason(rbTxt.get().getReason());
				rblTxtObj.setSccode(scriptobeAdded);
				
				rblPojo.getPropwithTxtsAdd().add(rblTxtObj);
			} else
			{
				//Create new Instance for Addition and Push to Collection
				StgyRebalancingTexts rblTxtObj = new StgyRebalancingTexts();
				rblTxtObj.setRbaltype(ERebalType.ADD);
				rblTxtObj.setSccode(scriptobeAdded);
				rblPojo.getPropwithTxtsAdd().add(rblTxtObj);
			}
		}
		
		//Removals Now
		for (String scriptobeRemoved : rblPojo.getPropRemovals())
		{
			//Scan the Repo First for The Scrip in Question
			Optional<IStgyRebalTxts_Simple> rbTxt = rblTxts.stream().filter(x -> x.getSccode().equals(scriptobeRemoved))
			        .findFirst();
			if (rbTxt.isPresent())
			{
				//Push to Collection
				StgyRebalancingTexts rblTxtObj = new StgyRebalancingTexts();
				rblTxtObj.setRbid(rbTxt.get().getRbid());
				rblTxtObj.setRbaltype(rbTxt.get().getRbaltype());
				rblTxtObj.setReason(rbTxt.get().getReason());
				rblTxtObj.setSccode(scriptobeRemoved);
				
				rblPojo.getPropwithTxtsRemove().add(rblTxtObj);
			} else
			{
				//Create new Instance for Addition and Push to Collection
				StgyRebalancingTexts rblTxtObj = new StgyRebalancingTexts();
				rblTxtObj.setRbaltype(ERebalType.REMOVE);
				rblTxtObj.setSccode(scriptobeRemoved);
				rblPojo.getPropwithTxtsRemove().add(rblTxtObj);
			}
		}
	}
	
}
