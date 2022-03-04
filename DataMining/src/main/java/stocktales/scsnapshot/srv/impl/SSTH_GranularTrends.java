package stocktales.scsnapshot.srv.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import stocktales.scsnapshot.model.pojo.StockBSGranularNos;
import stocktales.scsnapshot.model.txthelperPojo.THVarsSCGranNo;
import stocktales.scsnapshot.srv.intf.IStockSnapshotTextsHelper;

@Service("SSTH_GranularTrends")
public class SSTH_GranularTrends implements IStockSnapshotTextsHelper
{
	
	@Autowired
	private MessageSource msgSrc;
	
	private List<StockBSGranularNos> granNos;
	private THVarsSCGranNo           vars;
	
	/*
	 * The Company has grown it's sales from {0} to {1} Crs. in last {2} years by an multiple of {3} times.
	 * It has been achieved the same with it's nett. debt changing from {4} to {5} Crs. Debt to Sales Ratio 
	 * currently at {6} from {7} {8} years back. Over the same period the PAT has changed  from {9} to {10}
	 * by a multiple of {11} times. Cash and Investments position of the company have moved from {12} 
	 * to {13} a multiple of {14} times.
	
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMessagesforObject(
	        Object anyObject
	)
	{
		List<String> msgs = null;
		
		if (anyObject instanceof List<?>)
		{
			if (((List<?>) anyObject).size() > 0 && (((List<?>) anyObject).get(0) instanceof StockBSGranularNos))
			{
				//Proceed
				this.granNos = (List<StockBSGranularNos>) anyObject;
				if (granNos != null)
				{
					Optional<StockBSGranularNos> balMinO = granNos.stream()
					        .min(Comparator.comparing(StockBSGranularNos::getYrint));
					
					Optional<StockBSGranularNos> balMaxO = granNos.stream()
					        .max(Comparator.comparing(StockBSGranularNos::getYrint));
					
					if (balMinO.isPresent() && balMaxO.isPresent() && msgSrc != null)
					{
						vars = new THVarsSCGranNo();
						
						vars.setSalesMin(balMinO.get().getSales());
						vars.setSalesMax(balMaxO.get().getSales());
						vars.setDelta(balMaxO.get().getYrint() - balMinO.get().getYrint());
						vars.setSalesX(Precision
						        .round(((double) balMaxO.get().getSales() / (double) balMinO.get().getSales()), 1));
						
						vars.setDebtMin(balMinO.get().getDebt());
						vars.setDebtMax(balMaxO.get().getDebt());
						
						vars.setDebtToSalesMin(
						        Precision.round((double) vars.getDebtMin() / (double) vars.getSalesMin(), 1));
						vars.setDebtToSalesMax(
						        Precision.round((double) vars.getDebtMax() / (double) vars.getSalesMax(), 1));
						
						vars.setPatMin(balMinO.get().getPat());
						vars.setPatMax(balMaxO.get().getPat());
						vars.setPatX(Precision
						        .round(((double) balMaxO.get().getPat() / (double) balMinO.get().getPat()), 1));
						
						vars.setCashIMin(balMinO.get().getCashi());
						vars.setCashIMax(balMaxO.get().getCashi());
						vars.setCashIX(Precision
						        .round(((double) balMaxO.get().getCashi() / (double) balMinO.get().getCashi()), 1));
						
						msgs = new ArrayList<String>();
						
						String msg = msgSrc.getMessage("SC_SUMMARY", new Object[]
						{ vars.salesMin, vars.salesMax, vars.delta, vars.salesX, vars.debtMin, vars.debtMax,
						        vars.debtToSalesMax, vars.debtToSalesMin, vars.delta, vars.patMin, vars.patMax,
						        vars.patX, vars.cashIMin, vars.cashIMax, vars.cashIX }, Locale.ENGLISH);
						msgs.add(msg);
					}
				}
			}
		}
		return msgs;
	}
	
}
