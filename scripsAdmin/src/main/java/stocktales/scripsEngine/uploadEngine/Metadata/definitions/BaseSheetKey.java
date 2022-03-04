package stocktales.scripsEngine.uploadEngine.Metadata.definitions;

public class BaseSheetKey
{
	private String	baseSheetName;
	private String	keyFieldName;
	private String	sheetfieldName;

	public String getSheetfieldName()
	{
		return sheetfieldName;
	}

	public void setSheetfieldName(String sheetfieldName)
	{
		this.sheetfieldName = sheetfieldName;
	}

	public String getBaseSheetName()
	{
		return baseSheetName;
	}

	public void setBaseSheetName(String baseSheetName)
	{
		this.baseSheetName = baseSheetName;
	}

	public String getKeyFieldName()
	{
		return keyFieldName;
	}

	public void setKeyFieldName(String keyFieldName)
	{
		this.keyFieldName = keyFieldName;
	}

	public BaseSheetKey()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public BaseSheetKey(String baseSheetName, String keyFieldName, String sheetfieldName)
	{
		super();
		this.baseSheetName = baseSheetName;
		this.keyFieldName = keyFieldName;
		this.sheetfieldName = sheetfieldName;
	}

}
