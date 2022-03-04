/**
 * 
 */
package stocktales.scsnapshot.srv.intf;

import stocktales.scripCalc.pojos.TY_ScripCalcResult;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;

/**
 * Stock Snapshot Calculation Attributes Data Extraction Service
 *
 */
public interface IStockSnapshotCalcAttr_DESrv
{
	public Object extractStockSnapshotData(
	        ISCDataContainerSrv scDCSrv, TY_ScripCalcResult scCalcAttrRes
	);
}
