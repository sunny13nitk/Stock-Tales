package stocktales.scripsEngine.uploadEngine.tools.definitions;

import java.util.ArrayList;

public class TY_SingleCard_SheetRawData
{
	private String					sheetName;

	private ArrayList<TY_AttribVal>	sheetRawData;

	public String getSheetName()
	{
		return sheetName;
	}

	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

	public ArrayList<TY_AttribVal> getSheetRawData()
	{
		return sheetRawData;
	}

	public void setSheetRawData(ArrayList<TY_AttribVal> sheetRawData)
	{
		this.sheetRawData = sheetRawData;
	}

	public TY_SingleCard_SheetRawData()
	{
		super();
		this.sheetRawData = new ArrayList<TY_AttribVal>();
	}

	public TY_SingleCard_SheetRawData(String sheetName, ArrayList<TY_AttribVal> sheetRawData)
	{
		super();
		this.sheetName = sheetName;
		this.sheetRawData = sheetRawData;
	}

}
