package stocktales.scsnapshot.srv.intf;

import java.util.List;

/*
 * Implemented by Services that Ought to generate Text response to Data PoJo
 * Used in Stock Snapshot to give meaningful Descriptions to Data
 */
public interface IStockSnapshotTextsHelper
{
	public List<String> getMessagesforObject(
	        Object anyObject
	);
}
