package stocktales.scripsEngine.uploadEngine.tools.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import stocktales.scripsEngine.enums.SCEenums;

@XmlRootElement(name = "EntityListHeadScannerConfig")
@XmlAccessorType(XmlAccessType.FIELD)
// Base Class - Entity List Header Scanner Config
public class EntityListHeadScannerConfig
{
	private int rowToScan;
	
	private int colToScanStart;
	
	private int colToScanEnd;
	
	private SCEenums.rowScanType rowScanType;
	
	private SCEenums.rowDataType rowDataType;
	
	private SCEenums.rowDataType valrowDataType;
	
	private boolean checkNullVals;
	
	private int nullValRowPos;
	
	private String headerValsConvBean;
	
	private String objField;
	
	private SCEenums.rowDataType headerDataType;
	
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
	
	public String getHeaderValsConvBean(
	)
	{
		return headerValsConvBean;
	}
	
	public void setHeaderValsConvBean(
	        String headerValsConvBean
	)
	{
		this.headerValsConvBean = headerValsConvBean;
	}
	
	public int getRowToScan(
	)
	{
		return rowToScan;
	}
	
	public void setRowToScan(
	        int rowToScan
	)
	{
		this.rowToScan = rowToScan;
	}
	
	public int getColToScanStart(
	)
	{
		return colToScanStart;
	}
	
	public void setColToScanStart(
	        int colToScanStart
	)
	{
		this.colToScanStart = colToScanStart;
	}
	
	public int getColToScanEnd(
	)
	{
		return colToScanEnd;
	}
	
	public void setColToScanEnd(
	        int colToScanEnd
	)
	{
		this.colToScanEnd = colToScanEnd;
	}
	
	public SCEenums.rowScanType getRowScanType(
	)
	{
		return rowScanType;
	}
	
	public void setRowScanType(
	        SCEenums.rowScanType rowScanType
	)
	{
		this.rowScanType = rowScanType;
	}
	
	public SCEenums.rowDataType getRowDataType(
	)
	{
		return rowDataType;
	}
	
	public void setRowDataType(
	        SCEenums.rowDataType rowDataType
	)
	{
		this.rowDataType = rowDataType;
	}
	
	public boolean isCheckNullVals(
	)
	{
		return checkNullVals;
	}
	
	public void setCheckNullVals(
	        boolean checkNullVals
	)
	{
		this.checkNullVals = checkNullVals;
	}
	
	public int getNullValRowPos(
	)
	{
		return nullValRowPos;
	}
	
	public void setNullValRowPos(
	        int nullValRowPos
	)
	{
		this.nullValRowPos = nullValRowPos;
	}
	
	public SCEenums.rowDataType getValrowDataType(
	)
	{
		return valrowDataType;
	}
	
	public void setValrowDataType(
	        SCEenums.rowDataType valrowDataType
	)
	{
		this.valrowDataType = valrowDataType;
	}
	
	public SCEenums.rowDataType getHeaderDataType(
	)
	{
		return headerDataType;
	}
	
	public void setHeaderDataType(
	        SCEenums.rowDataType headerDataType
	)
	{
		this.headerDataType = headerDataType;
	}
	
	public EntityListHeadScannerConfig(
	        int rowToScan, int colToScanStart, int colToScanEnd, SCEenums.rowScanType rowScanType,
	        SCEenums.rowDataType rowDataType, SCEenums.rowDataType valrowDataType, boolean checkNullVals,
	        int nullValRowPos, String headerValsConvBean, String objField, SCEenums.rowDataType headerDataType
	)
	{
		super();
		this.rowToScan          = rowToScan;
		this.colToScanStart     = colToScanStart;
		this.colToScanEnd       = colToScanEnd;
		this.rowScanType        = rowScanType;
		this.rowDataType        = rowDataType;
		this.valrowDataType     = valrowDataType;
		this.checkNullVals      = checkNullVals;
		this.nullValRowPos      = nullValRowPos;
		this.headerValsConvBean = headerValsConvBean;
		this.objField           = objField;
		this.headerDataType     = headerDataType;
	}
	
	public EntityListHeadScannerConfig(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}