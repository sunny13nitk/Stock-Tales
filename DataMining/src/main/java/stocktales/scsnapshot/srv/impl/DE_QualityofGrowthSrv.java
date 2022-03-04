package stocktales.scsnapshot.srv.impl;

import org.springframework.stereotype.Service;

import stocktales.scripCalc.pojos.TY_ScripCalcResult;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scsnapshot.model.pojo.StockWCDetails;
import stocktales.scsnapshot.srv.intf.IStockSnapshotCalcAttr_DESrv;

@Service("DE_QualityofGrowthSrv")
public class DE_QualityofGrowthSrv implements IStockSnapshotCalcAttr_DESrv
{
	
	@Override
	public Object extractStockSnapshotData(
	        ISCDataContainerSrv scDCSrv, TY_ScripCalcResult scCalcAttrRes
	)
	{
		StockWCDetails wcDetails = null;
		
		if (scDCSrv != null && scCalcAttrRes != null)
		{
			
		}
		
		// TODO Auto-generated method stub
		return wcDetails;
	}
	
}
