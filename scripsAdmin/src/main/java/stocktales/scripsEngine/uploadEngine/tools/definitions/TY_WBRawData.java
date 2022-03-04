package stocktales.scripsEngine.uploadEngine.tools.definitions;

import java.util.ArrayList;

public class TY_WBRawData
{

	private ArrayList<SheetFldValsHeadersList>		CollSheetsRawData;

	private ArrayList<TY_SingleCard_SheetRawData>	nonCollSheetsRawData;

	public ArrayList<SheetFldValsHeadersList> getCollSheetsRawData()
	{
		return CollSheetsRawData;
	}

	public void setCollSheetsRawData(ArrayList<SheetFldValsHeadersList> collSheetsRawData)
	{
		CollSheetsRawData = collSheetsRawData;
	}

	public ArrayList<TY_SingleCard_SheetRawData> getNonCollSheetsRawData()
	{
		return nonCollSheetsRawData;
	}

	public void setNonCollSheetsRawData(ArrayList<TY_SingleCard_SheetRawData> nonCollSheetsRawData)
	{
		this.nonCollSheetsRawData = nonCollSheetsRawData;
	}

	public TY_WBRawData()
	{
		this.CollSheetsRawData = new ArrayList<SheetFldValsHeadersList>();
		this.nonCollSheetsRawData = new ArrayList<TY_SingleCard_SheetRawData>();
	}

	public TY_WBRawData(ArrayList<SheetFldValsHeadersList> collSheetsRawData, ArrayList<TY_SingleCard_SheetRawData> nonCollSheetsRawData)
	{
		super();
		CollSheetsRawData = collSheetsRawData;
		this.nonCollSheetsRawData = nonCollSheetsRawData;
	}

}
