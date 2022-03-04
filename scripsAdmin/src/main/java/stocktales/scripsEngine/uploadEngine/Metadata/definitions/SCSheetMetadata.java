package stocktales.scripsEngine.uploadEngine.Metadata.definitions;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import stocktales.scripsEngine.uploadEngine.tools.definitions.EntityListHeadScannerConfig;

@XmlRootElement(name = "SheetMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class SCSheetMetadata
{
	private String sheetName;
	
	private String bobjName;
	
	private boolean isReqdCreate;
	
	private boolean isBaseSheet;
	
	private boolean isCollection;
	
	private boolean updHeaderDeltaMode;
	
	private int colToScanStart;
	
	private int colToScanEnd;
	
	private String updateReqdProcessorBean;
	
	private EntityListHeadScannerConfig headScanConfig;
	
	private String assembly;
	
	private String entPKey; // Primary key in Entity for Update Op
	
	private ArrayList<SheetFieldsMetadata> fldsMdt;
	
	public String getEntPKey(
	)
	{
		return entPKey;
	}
	
	public void setEntPKey(
	        String entPKey
	)
	{
		this.entPKey = entPKey;
	}
	
	public String getAssembly(
	)
	{
		return assembly;
	}
	
	public void setAssembly(
	        String assembly
	)
	{
		this.assembly = assembly;
	}
	
	public String getBobjName(
	)
	{
		return bobjName;
	}
	
	public void setBobjName(
	        String bobjName
	)
	{
		this.bobjName = bobjName;
	}
	
	public boolean isReqdCreate(
	)
	{
		return isReqdCreate;
	}
	
	public void setReqdCreate(
	        boolean isReqdCreate
	)
	{
		this.isReqdCreate = isReqdCreate;
	}
	
	public boolean isBaseSheet(
	)
	{
		return isBaseSheet;
	}
	
	public void setBaseSheet(
	        boolean isBaseSheet
	)
	{
		this.isBaseSheet = isBaseSheet;
	}
	
	public boolean isCollection(
	)
	{
		return isCollection;
	}
	
	public void setCollection(
	        boolean isCollection
	)
	{
		this.isCollection = isCollection;
	}
	
	public boolean isUpdHeaderDeltaMode(
	)
	{
		return updHeaderDeltaMode;
	}
	
	public void setUpdHeaderDeltaMode(
	        boolean updHeaderDeltaMode
	)
	{
		this.updHeaderDeltaMode = updHeaderDeltaMode;
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
	
	public String getUpdateReqdProcessorBean(
	)
	{
		return updateReqdProcessorBean;
	}
	
	public void setUpdateReqdProcessorBean(
	        String updateReqdProcessorBean
	)
	{
		this.updateReqdProcessorBean = updateReqdProcessorBean;
	}
	
	public ArrayList<SheetFieldsMetadata> getFldsMdt(
	)
	{
		return fldsMdt;
	}
	
	public void setFldsMdt(
	        ArrayList<SheetFieldsMetadata> fldsMdt
	)
	{
		this.fldsMdt = fldsMdt;
	}
	
	public String getSheetName(
	)
	{
		return sheetName;
	}
	
	public void setSheetName(
	        String sheetName
	)
	{
		this.sheetName = sheetName;
	}
	
	public EntityListHeadScannerConfig getHeadScanConfig(
	)
	{
		return headScanConfig;
	}
	
	public void setHeadScanConfig(
	        EntityListHeadScannerConfig headScanConfig
	)
	{
		this.headScanConfig = headScanConfig;
	}
	
	public SCSheetMetadata(
	)
	{
		super();
		this.fldsMdt = new ArrayList<SheetFieldsMetadata>();
		// TODO Auto-generated constructor stub
	}
	
	public SCSheetMetadata(
	        String sheetName, String bobjName, boolean isReqdCreate, boolean isBaseSheet, boolean isCollection,
	        boolean updHeaderDeltaMode, int colToScanStart, int colToScanEnd, String updateReqdProcessorBean,
	        EntityListHeadScannerConfig headScanConfig, ArrayList<SheetFieldsMetadata> fldsMdt
	)
	{
		super();
		this.sheetName               = sheetName;
		this.bobjName                = bobjName;
		this.isReqdCreate            = isReqdCreate;
		this.isBaseSheet             = isBaseSheet;
		this.isCollection            = isCollection;
		this.updHeaderDeltaMode      = updHeaderDeltaMode;
		this.colToScanStart          = colToScanStart;
		this.colToScanEnd            = colToScanEnd;
		this.updateReqdProcessorBean = updateReqdProcessorBean;
		this.headScanConfig          = headScanConfig;
		this.fldsMdt                 = fldsMdt;
	}
	
}
