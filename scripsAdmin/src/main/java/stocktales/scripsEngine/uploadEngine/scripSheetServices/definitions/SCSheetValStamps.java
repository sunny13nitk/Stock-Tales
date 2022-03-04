package stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions;

import java.util.ArrayList;

public class SCSheetValStamps
{
	private String					scCode;

	private ArrayList<SheetValStamp>	sheetVals;

	public String getScCode()
	{
		return scCode;
	}

	public void setScCode(String scCode)
	{
		this.scCode = scCode;
	}

	public ArrayList<SheetValStamp> getSheetVals()
	{
		return sheetVals;
	}

	public void setSheetVals(ArrayList<SheetValStamp> sheetVals)
	{
		this.sheetVals = sheetVals;
	}

	public SCSheetValStamps()
	{
		super();
		this.sheetVals = new ArrayList<SheetValStamp>();
	}

	public SCSheetValStamps(String scCode, ArrayList<SheetValStamp> sheetVals)
	{
		super();
		this.scCode = scCode;
		this.sheetVals = sheetVals;
	}

}
