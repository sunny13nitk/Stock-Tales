package stocktales.scripsEngine.uploadEngine.tools.definitions;

// Populated at runtime while Work Book Processing to hand over to Row fields Parser Service
public class FieldsParserProp
{
	private String	sheetField;
	private int	colStartPos;	// will be handed over by SheetHeadersSrv during WB Processing
	private int	colEndPos;

	public String getSheetField()
	{
		return sheetField;
	}

	public void setSheetField(String sheetField)
	{
		this.sheetField = sheetField;
	}

	public int getColStartPos()
	{
		return colStartPos;
	}

	public void setColStartPos(int colStartPos)
	{
		this.colStartPos = colStartPos;
	}

	public int getColEndPos()
	{
		return colEndPos;
	}

	public void setColEndPos(int colEndPos)
	{
		this.colEndPos = colEndPos;
	}

	public FieldsParserProp()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public FieldsParserProp(String sheetField, int colStartPos, int colEndPos)
	{
		super();
		this.sheetField = sheetField;
		this.colStartPos = colStartPos;
		this.colEndPos = colEndPos;
	}

}
