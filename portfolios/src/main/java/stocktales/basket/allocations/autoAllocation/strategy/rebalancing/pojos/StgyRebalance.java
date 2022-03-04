package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.pojos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.entity.StgyRebalancingTexts;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StgyRebalance
{
	private int                        stid;
	private String                     stgyDesc;
	private List<String>               currScrips         = new ArrayList<String>();
	private List<String>               propAdditions      = new ArrayList<String>();
	private List<String>               propRemovals       = new ArrayList<String>();
	private List<StgyRebalancingTexts> propwithTxtsAdd    = new ArrayList<StgyRebalancingTexts>();
	private List<StgyRebalancingTexts> propwithTxtsRemove = new ArrayList<StgyRebalancingTexts>();
	private List<String>               predScrips         = new ArrayList<String>();
	private List<String>               stgyScrips         = new ArrayList<String>();
	private List<String>               summary            = new ArrayList<String>();
	private List<Integer>              tobeDeleted        = new ArrayList<Integer>();
}
