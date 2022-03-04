package stocktales.scripsEngine.uploadEngine.tools.definitions;

import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.T;

// Values List derived from Sheet for a Particular Field
public class FldVals
{
	private String			fieldName;

	private ArrayList<T>	fieldVals;

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public ArrayList<T> getFieldVals()
	{
		return fieldVals;
	}

	public void setFieldVals(ArrayList<T> fieldVals)
	{
		this.fieldVals = fieldVals;
	}

	public FldVals()
	{
		super();
		this.fieldVals = new ArrayList<T>();
	}

}
