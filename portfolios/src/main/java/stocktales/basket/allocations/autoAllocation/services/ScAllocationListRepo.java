package stocktales.basket.allocations.autoAllocation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationListRepo;
import stocktales.basket.allocations.autoAllocation.interfaces.IScAllocationSrv;
import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.basket.allocations.autoAllocation.valuations.pojos.ScValuation;
import stocktales.dataBook.model.entity.adhocScrips.AdhocScrip;
import stocktales.dataBook.model.repo.adhocScrips.RepoAdhocScrip;
import stocktales.helperPOJO.ErrorsValidation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScAllocationListRepo implements IScAllocationListRepo
{
	@Autowired
	private SCValuationSrv   scValSrv;
	@Autowired
	private IScAllocationSrv scAllocSrv;
	
	@Autowired
	private RepoAdhocScrip repoAdhocSc;
	
	private List<ScAllocation> scAllocList = new ArrayList<ScAllocation>();
	
	@Override
	public double getTotalAllocationPercentage(
	)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ErrorsValidation isAllocationValid(
	)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addNew(
	        String scCode
	)
	{
		// Get Existing Bean SC Codes
		List<String> scrips = getScripCodes();
		// Add to the list current SC Code
		scrips.add(scCode);
		//Trigger Stage Allocations - implicitly set Bean Allocations list again
		stageAllocationsforScrips(scrips);
	}
	
	@Override
	public void delete(
	        String ScCode
	)
	{
		// Get Existing Bean SC Codes
		List<String> scrips = getScripCodes();
		// Remove from the list current SC Code
		scrips.remove(ScCode);
		//Trigger Stage Allocations - implicitly set Bean Allocations list again
		stageAllocationsforScrips(scrips);
	}
	
	@Override
	public void refreshAllocation(
	        ScAllocation scAlloc
	)
	{
		if (scAlloc != null)
		{
			//Get the Scrip Allocation Matching Scrip Code from REpo
			
			Optional<ScAllocation> currAllocO = this.getScAllocList().stream()
			        .filter(x -> x.getScCode().equals(scAlloc.getScCode())).findFirst();
			if (currAllocO.isPresent())
			{
				//Update the same from parameter Entity
				ScAllocation allocEnt = currAllocO.get();
				if (allocEnt != null)
				{
					//Only the Attributes that would be affected - this should change in List Too
					allocEnt.setMoS(scAlloc.getMoS());
					allocEnt.setEDScore(scAlloc.getEDScore());
					allocEnt.setCMP(scAlloc.getCMP());
					allocEnt.setCurrPE(scAlloc.getCurrPE());
					allocEnt.setPrice5Yr(scAlloc.getPrice5Yr());
					allocEnt.setRet5YrCAGR(scAlloc.getRet5YrCAGR());
					allocEnt.setAllocation(scAlloc.getAllocation());
				}
			}
			
		}
	}
	
	@Override
	public List<ScAllocation> stageAllocationsforScrips(
	        List<String> scrips
	)
	{
		if (scrips != null)
		{
			if (scrips.size() > 0)
			{
				//Clear Existing ScAllocations If Any as the Allocations have been re-triggered
				this.scAllocList.clear();
				//Get SC Valuations for All Scrips First
				for (String scCode : scrips)
				{
					//Validate if Scrip is an Adhoc Scrip at this juncture and populate Allocation Accordingly
					Optional<AdhocScrip> adSCO = this.repoAdhocSc.findBySccodeIgnoreCase(scCode);
					if (adSCO.isPresent())
					{
						
						//This is an Adhoc Scrip
						ScAllocation scAlloc = new ScAllocation();
						scAlloc.setAllocation(0);
						scAlloc.setCMP(0);
						scAlloc.setScCode(scCode);
						scAlloc.setAdhoc(true);
						this.scAllocList.add(scAlloc);
						
					} else
					{
						
						ScValuation scVal = scValSrv.getValuationforScrip(scCode, 0, 0);
						if (scVal != null)
						{
							ScAllocation scAlloc = new ScAllocation(scVal);
							scAlloc.setAdhoc(false);
							this.scAllocList.add(scAlloc);
						}
					}
					
				}
				
				//Send to Allocation Bean to Get List of Allocations by List of Valuations
				this.setScAllocList(scAllocSrv.calculateAllocations(getScAllocList()));
			}
		}
		// Set current Allocation List & also return out for direct exposure
		return this.scAllocList;
	}
	
	private List<String> getScripCodes(
	)
	{
		List<String> scrips = new ArrayList<String>();
		
		this.scAllocList.stream().filter(x -> scrips.add(x.getScCode())).collect(Collectors.toList());
		
		return scrips;
	}
	
	@Override
	public void clearAllocations(
	)
	{
		this.scAllocList.clear();
		
	}
	
	@Override
	public void addAllocation(
	        ScAllocation scAlloc
	)
	{
		//No duplicates
		
		if (scAlloc != null)
		{
			//Get the Scrip Allocation Matching Scrip Code from REpo
			
			if (scAllocList.size() > 0)
			{
				
				Optional<ScAllocation> currAllocO = this.getScAllocList().stream()
				        .filter(x -> x.getScCode().equals(scAlloc.getScCode())).findFirst();
				if (!currAllocO.isPresent())
				{
					this.scAllocList.add(scAlloc);
				}
			} else
			{
				this.scAllocList.add(scAlloc);
			}
		}
		
	}
	
	@Override
	public void clear_replace_allocations(
	        List<ScAllocation> allocations
	)
	{
		clearAllocations();
		this.scAllocList = allocations;
		
	}
	
}
