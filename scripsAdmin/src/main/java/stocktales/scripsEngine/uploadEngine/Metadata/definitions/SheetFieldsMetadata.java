package stocktales.scripsEngine.uploadEngine.Metadata.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.enums.SCEenums.rowDataType;

@XmlRootElement(name = "FieldDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class SheetFieldsMetadata
{
	private String               sheetField;
	private String               objField;
	private boolean              isMandatory;
	private boolean              isSplitAware;
	private boolean              isBonusAware;
	private boolean              isUnModifiable;
	private boolean              isKey;
	private SCEenums.rowDataType dataType;
	
	public String getSheetField(
	)
	{
		return sheetField;
	}
	
	public void setSheetField(
	        String sheetField
	)
	{
		this.sheetField = sheetField;
	}
	
	public String getObjField(
	)
	{
		return objField;
	}
	
	public void setObjField(
	        String objField
	)
	{
		this.objField = objField;
	}
	
	public boolean isMandatory(
	)
	{
		return isMandatory;
	}
	
	public void setMandatory(
	        boolean isMandatory
	)
	{
		this.isMandatory = isMandatory;
	}
	
	public boolean isSplitAware(
	)
	{
		return isSplitAware;
	}
	
	public void setSplitAware(
	        boolean isSplitAware
	)
	{
		this.isSplitAware = isSplitAware;
	}
	
	public boolean isBonusAware(
	)
	{
		return isBonusAware;
	}
	
	public void setBonusAware(
	        boolean isBonusAware
	)
	{
		this.isBonusAware = isBonusAware;
	}
	
	public boolean isUnModifiable(
	)
	{
		return isUnModifiable;
	}
	
	public void setUnModifiable(
	        boolean isUnModifiable
	)
	{
		this.isUnModifiable = isUnModifiable;
	}
	
	public SCEenums.rowDataType getDataType(
	)
	{
		return dataType;
	}
	
	public void setDataType(
	        SCEenums.rowDataType dataType
	)
	{
		this.dataType = dataType;
	}
	
	public boolean isKey(
	)
	{
		return isKey;
	}
	
	public void setKey(
	        boolean isKey
	)
	{
		this.isKey = isKey;
	}
	
	public SheetFieldsMetadata(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SheetFieldsMetadata(
	        String sheetField, String objField, boolean isMandatory, boolean isSplitAware, boolean isBonusAware,
	        boolean isUnModifiable, boolean isKey, rowDataType dataType
	)
	{
		super();
		this.sheetField     = sheetField;
		this.objField       = objField;
		this.isMandatory    = isMandatory;
		this.isSplitAware   = isSplitAware;
		this.isBonusAware   = isBonusAware;
		this.isUnModifiable = isUnModifiable;
		this.isKey          = isKey;
		this.dataType       = dataType;
	}
	
}
