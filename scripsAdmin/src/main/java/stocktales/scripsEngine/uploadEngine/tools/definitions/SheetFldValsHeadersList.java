package stocktales.scripsEngine.uploadEngine.tools.definitions;

import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.T;

// List of Fields and Corresponding Values List for a Particular Sheet
public class SheetFldValsHeadersList
{
	private String				sheetName;

	private ArrayList<FldVals>	fldValList;

	private ArrayList<T>		headers;

	public String getSheetName()
	{
		return sheetName;
	}

	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

	public ArrayList<FldVals> getFldValList()
	{
		return fldValList;
	}

	public void setFldValList(ArrayList<FldVals> fldValList)
	{
		this.fldValList = fldValList;
	}

	public ArrayList<T> getHeaders()
	{
		return headers;
	}

	public void setHeaders(ArrayList<T> headers)
	{
		this.headers = headers;
	}

	public SheetFldValsHeadersList()
	{
		super();
		this.fldValList = new ArrayList<FldVals>();
		this.headers = new ArrayList<T>();
	}

	public SheetFldValsHeadersList(String sheetName, ArrayList<FldVals> fldValList, ArrayList<T> headers)
	{
		super();
		this.sheetName = sheetName;
		this.fldValList = fldValList;
		this.headers = headers;
	}

}
