package stocktales.scsnapshot.srv.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.scripCalc.pojos.TY_AttrMultiContainer;
import stocktales.scripCalc.pojos.TY_ScripCalcResult;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scsnapshot.model.pojo.StockWCDetails;
import stocktales.scsnapshot.srv.intf.IStockSnapshotCalcAttr_DESrv;

@Service("DE_WorkingCapitalSrv")
public class DE_WorkingCapitalSrv implements IStockSnapshotCalcAttr_DESrv
{
	
	private static String capexToSales = "Capex To Sales (%)";
	private static String debtToSales  = "Debt To Sales (%)";
	private static String nfaToSales   = "NFA To Sales (%)";
	
	@Override
	public Object extractStockSnapshotData(
	        ISCDataContainerSrv scDCSrv, TY_ScripCalcResult scCalcAttrRes
	)
	{
		List<StockWCDetails> wcDetails = new ArrayList<StockWCDetails>();
		
		if (scDCSrv != null && scCalcAttrRes != null)
		{
			List<EN_SC_BalSheet> balSheetEntL = scDCSrv.getScDC().getBalSheet_L();
			if (balSheetEntL != null)
			{
				//Prepare List of Capex to Sales Entities
				Optional<TY_AttrMultiContainer> capextoSalesLO = scCalcAttrRes.getAttrsMulti().stream()
				        .filter(x -> x.getAttrName().equals(capexToSales)).findFirst();
				
				//Prepare List of Debt to Sales Entities
				Optional<TY_AttrMultiContainer> debttoSalesLO = scCalcAttrRes.getAttrsMulti().stream()
				        .filter(x -> x.getAttrName().equals(debtToSales)).findFirst();
				
				//Prepare List of NFA to Sales Entities
				Optional<TY_AttrMultiContainer> nfatoSalesLO = scCalcAttrRes.getAttrsMulti().stream()
				        .filter(x -> x.getAttrName().equals(nfaToSales)).findFirst();
				
				if (capextoSalesLO.isPresent() && debttoSalesLO.isPresent() && nfatoSalesLO.isPresent())
				{
					TY_AttrMultiContainer capextoSales = capextoSalesLO.get();
					TY_AttrMultiContainer debttoSales  = debttoSalesLO.get();
					TY_AttrMultiContainer nfatoSales   = nfatoSalesLO.get();
					
					for (NameVal nameValcapextoSalesI : capextoSales.getNameVals())
					{
						int year = Integer.valueOf(nameValcapextoSalesI.getName());
						
						StockWCDetails wcdetailsI = new StockWCDetails();
						
						wcdetailsI.setYear(String.valueOf(year));
						wcdetailsI.setCapextoSales(nameValcapextoSalesI.getValue());
						
						Optional<NameVal> debttoSalesO = debttoSales.getNameVals().stream()
						        .filter(w -> w.getName().equals(nameValcapextoSalesI.getName())).findFirst();
						
						Optional<NameVal> nfatoSalesO = nfatoSales.getNameVals().stream()
						        .filter(w -> w.getName().equals(nameValcapextoSalesI.getName())).findFirst();
						
						Optional<EN_SC_BalSheet> bsheetO = balSheetEntL.stream().filter(w -> w.getYear() == year)
						        .findFirst();
						
						if (nfatoSalesO.isPresent() && bsheetO.isPresent())
						{
							if (debttoSalesO.isPresent())
							{
								wcdetailsI.setDebttoSales(debttoSalesO.get().getValue());
							} else
							{
								wcdetailsI.setDebttoSales(0);
							}
							
							wcdetailsI.setNfatoSales(nfatoSalesO.get().getValue());
							wcdetailsI.setNfat(Precision.round(
							        bsheetO.get().getNFAT(), 1));
							wcdetailsI.setRecvd(
							        bsheetO.get().getReceivableD());
							wcdetailsI.setItr(
							        bsheetO.get().getITR());
							
							wcDetails.add(wcdetailsI);
						}
						
					}
					
				}
				
			}
		}
		
		return wcDetails;
	}
	
}
