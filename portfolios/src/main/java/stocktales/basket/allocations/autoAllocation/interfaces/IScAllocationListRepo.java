package stocktales.basket.allocations.autoAllocation.interfaces;

import java.util.List;

import stocktales.basket.allocations.autoAllocation.pojos.ScAllocation;
import stocktales.helperPOJO.ErrorsValidation;

public interface IScAllocationListRepo
{
	public List<ScAllocation> getScAllocList(
	);
	
	public double getTotalAllocationPercentage(
	);
	
	public ErrorsValidation isAllocationValid(
	);
	
	public void addNew(
	        String scCode
	);
	
	public void delete(
	        String ScCode
	);
	
	public List<ScAllocation> stageAllocationsforScrips(
	        List<String> scrips
	);
	
	public void refreshAllocation(
	        ScAllocation scAlloc
	);
	
	public void clearAllocations(
	);
	
	public void addAllocation(
	        ScAllocation scAlloc
	);
	
	public void clear_replace_allocations(
	        List<ScAllocation> allocations
	);
	
}
