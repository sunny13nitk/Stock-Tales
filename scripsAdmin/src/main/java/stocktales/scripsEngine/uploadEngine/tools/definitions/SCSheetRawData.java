package stocktales.scripsEngine.uploadEngine.tools.definitions;

import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.T;

/**
 * 
 * Sheet Raw Data Container
 *
 */
public class SCSheetRawData
{
	private ArrayList<T>	headers;

	private SheetFldValsHeadersList		fldValsList;

	public ArrayList<T> getHeaders()
	{
		return headers;
	}

	public void setHeaders(ArrayList<T> headers)
	{
		this.headers = headers;
	}

	public SheetFldValsHeadersList getFldValsList()
	{
		return fldValsList;
	}

	public void setFldValsList(SheetFldValsHeadersList fldValsList)
	{
		this.fldValsList = fldValsList;
	}

	public SCSheetRawData()
	{
		super();
		this.headers = new ArrayList<T>();
		this.fldValsList = new SheetFldValsHeadersList();
	}

	public SCSheetRawData(ArrayList<T> headers, SheetFldValsHeadersList fldValsList)
	{
		super();
		this.headers = headers;
		this.fldValsList = fldValsList;
	}

}
