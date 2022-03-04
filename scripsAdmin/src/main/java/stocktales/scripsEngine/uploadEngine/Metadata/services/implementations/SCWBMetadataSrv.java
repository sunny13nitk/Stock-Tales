package stocktales.scripsEngine.uploadEngine.Metadata.services.implementations;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.JAXB.definitions.PathsJAXBSM;
import stocktales.scripsEngine.uploadEngine.Metadata.JAXB.implementations.SCWBMetadata_JAXB;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.BaseSheetKey;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCWBMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.interfaces.ISCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

@Service
public class SCWBMetadataSrv implements ISCWBMetadataSrv
{
	@Autowired
	private PathsJAXBSM pathsSMbean;
	
	private SCWBMetadata wbMdt;
	
	private BaseSheetKey bshKey;
	
	@Override
	public SCSheetMetadata getMetadataforSheet(
	        String SheetName
	) throws EX_General
	{
		
		SCSheetMetadata shtMdt = null;
		if (SheetName != null)
		{
			if (this.wbMdt == null)
			{
				Initialize();
			}
			
			try
			{
				shtMdt = this.wbMdt.getSheetMetadata().stream().filter(x -> x.getSheetName().equals(SheetName))
				        .findFirst().get();
			} catch (NoSuchElementException e)
			{
				// Do Nothing
			}
		}
		
		return shtMdt;
	}
	
	@Override
	public SheetFieldsMetadata getFieldMetadata(
	        String SheetName, String FieldName
	) throws EX_General
	{
		SheetFieldsMetadata fldMdt = null;
		SCSheetMetadata     shMdt  = null;
		if (SheetName != null)
		{
			shMdt = this.getMetadataforSheet(SheetName);
			if (shMdt != null)
			{
				fldMdt = shMdt.getFldsMdt().stream().filter(x -> x.getSheetField().equals(FieldName)).findFirst().get();
			}
		}
		
		return fldMdt;
	}
	
	@Override
	public BaseSheetKey getBaseSheetKey(
	) throws EX_General
	{
		if (this.bshKey == null)
		{
			if (this.wbMdt == null)
			{
				Initialize();
			}
			
			try
			{
				SCSheetMetadata shtMdt = this.wbMdt.getSheetMetadata().stream().filter(x -> x.isBaseSheet() == true)
				        .findFirst().get();
				if (shtMdt != null)
				{
					SheetFieldsMetadata fldMdt = shtMdt.getFldsMdt().stream().filter(x -> x.isKey() == true).findFirst()
					        .get();
					if (fldMdt != null)
					{
						bshKey = new BaseSheetKey();
						bshKey.setBaseSheetName(shtMdt.getSheetName());
						bshKey.setKeyFieldName(fldMdt.getObjField());
						bshKey.setSheetfieldName(fldMdt.getSheetField());
					}
				}
				
			}
			
			catch (Exception Ex)
			{
				EX_General egen = new EX_General("NO_BASE_SHEET_KEY_FLD", null);
				
				throw egen;
			}
			
		}
		
		return bshKey;
		
	}
	
	@Override
	public SCSheetMetadata getMetadataforBaseSheet(
	) throws EX_General
	{
		SCSheetMetadata shtMdt = null;
		
		if (this.wbMdt == null)
		{
			Initialize();
		}
		
		try
		{
			shtMdt = this.wbMdt.getSheetMetadata().stream().filter(x -> x.isBaseSheet() == true).findFirst().get();
		}
		
		catch (Exception Ex)
		{
			EX_General egen = new EX_General("NO_BASE_SHEET_KEY_FLD", null);
			
			throw egen;
		}
		
		return shtMdt;
	}
	
	@Override
	public SCWBMetadata getWbMetadata(
	) throws EX_General
	{
		SCWBMetadata wbMdt = null;
		if (this.wbMdt == null)
		{
			Initialize();
			return this.wbMdt;
		} else
		{
			wbMdt = this.wbMdt;
		}
		
		return wbMdt;
	}
	
	@Override
	public String getRelationNameforSheetName(
	        String sheetName
	) throws EX_General
	{
		String relName = null;
		
		if (sheetName != null)
		{
			// first get the sheet Metadata and validate it is for not a base sheet
			SCSheetMetadata shmdt = this.getMetadataforSheet(sheetName);
			if (!shmdt.isBaseSheet())
			{
				try
				{
					// TBC - To be changed
					relName = null; /*
					                 * FrameworkManager.getObjectSchemaFactory()
					                 * .get_Dependant_Metadata_byObjName(shmdt.getBobjName()).getRelationname();
					                 */
				}
				
				catch (Exception e)
				{
					// Do nothing - Sheet handled via X Sheet Validator
				}
				
			}
		}
		
		return relName;
	}
	
	/**
	 * -------- Private Section ---------------------------
	 */
	
	private void Initialize(
	) throws EX_General
	{
		if (pathsSMbean != null)
		{
			SCWBMetadata_JAXB wbJAXB = new SCWBMetadata_JAXB(pathsSMbean);
			try
			{
				this.wbMdt = new SCWBMetadata(wbJAXB.Load_XML_to_Objects());
			} catch (Exception e)
			{
				EX_General egen = new EX_General("NO_SHEET_MDT", new Object[]
				{ this.pathsSMbean.getJaxPath_SM_xml() });
				
				throw egen;
			}
		}
		
	}
	
}
