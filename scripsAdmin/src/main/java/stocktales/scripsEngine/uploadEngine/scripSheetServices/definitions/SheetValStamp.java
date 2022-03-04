package stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions;

public class SheetValStamp
{

	private String	sheetName;
	private Object	value;

	public String getSheetName()
	{
		return sheetName;
	}

	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public SheetValStamp()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public SheetValStamp(String sheetName, Object value)
	{
		super();
		this.sheetName = sheetName;
		this.value = value;
	}

}
