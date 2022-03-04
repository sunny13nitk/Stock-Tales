package stocktales.scripsEngine.uploadEngine.tools.definitions;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.enums.SCEenums.rowDataType;

public class TY_AttribVal
{
	private String attrName;
	private String attrLabel;
	private Object value;
	
	private SCEenums.rowDataType attrType;
	
	/**
	 * @return the attrLabel
	 */
	public String getAttrLabel(
	)
	{
		return attrLabel;
	}
	
	/**
	 * @param attrLabel the attrLabel to set
	 */
	public void setAttrLabel(
	        String attrLabel
	)
	{
		this.attrLabel = attrLabel;
	}
	
	/**
	 * @return the attrName
	 */
	public String getAttrName(
	)
	{
		return attrName;
	}
	
	/**
	 * @param attrName the attrName to set
	 */
	public void setAttrName(
	        String attrName
	)
	{
		this.attrName = attrName;
	}
	
	/**
	 * @return the value
	 */
	public Object getValue(
	)
	{
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(
	        Object value
	)
	{
		this.value = value;
	}
	
	public SCEenums.rowDataType getAttrType(
	)
	{
		return attrType;
	}
	
	public void setAttrType(
	        SCEenums.rowDataType attrType
	)
	{
		this.attrType = attrType;
	}
	
	public TY_AttribVal(
	        String attrName, String attrLabel, Object value, rowDataType attrType
	)
	{
		super();
		this.attrName  = attrName;
		this.attrLabel = attrLabel;
		this.value     = value;
		this.attrType  = attrType;
	}
	
	/**
	 * 
	 */
	public TY_AttribVal(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
