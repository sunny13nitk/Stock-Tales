package stocktales.scripsEngine.uploadEngine.Metadata.JAXB.implementations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import stocktales.scripsEngine.enums.SCEenums.rowDataType;
import stocktales.scripsEngine.enums.SCEenums.rowScanType;
import stocktales.scripsEngine.uploadEngine.Metadata.JAXB.definitions.PathsJAXBSM;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCWBMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.interfaces.IJaxable;
import stocktales.scripsEngine.uploadEngine.tools.definitions.EntityListHeadScannerConfig;

public class SCWBMetadata_JAXB implements IJaxable
{
	
	private PathsJAXBSM pathConstants;
	
	public PathsJAXBSM getPathConstants(
	)
	{
		return pathConstants;
	}
	
	public void setPathConstants(
	        PathsJAXBSM pathConstants
	)
	{
		this.pathConstants = pathConstants;
	}
	
	public SCWBMetadata_JAXB(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SCWBMetadata_JAXB(
	        PathsJAXBSM pathConstants
	)
	{
		super();
		this.pathConstants = pathConstants;
	}
	
	@Override
	public void Generate_XSD(
	) throws Exception
	{
		// Generate XSD from the ObjSchemas Class
		// grab the context
		
		JAXBContext context = JAXBContext.newInstance(SCWBMetadata.class);
		
		context.generateSchema(
		        // need to define a SchemaOutputResolver to store to
		        
		        new SchemaOutputResolver()
		        {
			        @Override
			        public Result createOutput(
			                String ns, String file
			        ) throws IOException
			        {
				        // save the schema to the file at
				        // specified path
				        // create new file
				        File file1 = new File(getPathConstants().getJaxPath_SM_xsd());
				        // create stream result
				        StreamResult result = new StreamResult(file1);
				        // set system id
				        result.setSystemId(file1.toURI().toURL().toString());
				        
				        // return result
				        return result;
				        
			        }
		        });
		
	}
	
	@Override
	public void Generate_XML(
	) throws Exception
	{
		// Genrate XML document with Dummy Objects
		
		// Create a Prices Projection Metadata Object
		SCWBMetadata wmMdt = new SCWBMetadata();
		
		SCSheetMetadata sheetAnalysis = new SCSheetMetadata();
		sheetAnalysis.setSheetName("Analysis");
		
		EntityListHeadScannerConfig anHeadConfig = new EntityListHeadScannerConfig(2, 2, 11, rowScanType.Continous,
		        rowDataType.String, rowDataType.Numerical, true, 3, "Analysis_HeadersConvSrv", "Year",
		        rowDataType.Numerical);
		sheetAnalysis.setHeadScanConfig(anHeadConfig);
		
		ArrayList<SheetFieldsMetadata> sheetFlds = new ArrayList<SheetFieldsMetadata>();
		if (sheetFlds != null)
		{
			SheetFieldsMetadata salesfldMdt = new SheetFieldsMetadata("Sales", "sales", true, false, false, false,
			        false, rowDataType.Numerical);
			sheetFlds.add(salesfldMdt);
			SheetFieldsMetadata opfldMdt = new SheetFieldsMetadata("Operating Profit", "op", true, false, false, false,
			        false, rowDataType.Numerical);
			sheetFlds.add(opfldMdt);
			SheetFieldsMetadata opmfldMdt = new SheetFieldsMetadata("Operating Profit Margin (OPM%)", "opm", true,
			        false, false, false, false, rowDataType.Numerical);
			sheetFlds.add(opmfldMdt);
		}
		
		sheetAnalysis.getFldsMdt().addAll(sheetFlds);
		
		wmMdt.getSheetMetadata().add(sheetAnalysis);
		
		SCSheetMetadata sheetTrends = new SCSheetMetadata();
		sheetAnalysis.setSheetName("Trends");
		
		EntityListHeadScannerConfig trHeadConfig = new EntityListHeadScannerConfig(1, 2, 6, rowScanType.Continous,
		        rowDataType.String, rowDataType.Numerical, true, 2, null, "period", rowDataType.String);
		sheetTrends.setHeadScanConfig(trHeadConfig);
		
		wmMdt.getSheetMetadata().add(sheetTrends);
		
		// Marshal Here
		JAXBContext jaxbContext    = JAXBContext.newInstance(wmMdt.getClass());
		Marshaller  jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		// Marshal the ObjectSchemas to file in OS Specific Location as
		// per properties file
		jaxbMarshaller.marshal(wmMdt, new File(this.getPathConstants().getJaxPath_SM_xml_GEN()));
		// Later copy the same file to your project
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> ArrayList<T> Load_XML_to_Objects(
	) throws Exception
	{
		
		ArrayList<T> list = new ArrayList<>();
		// Get Objects Back from XML
		try
		{
			JAXBContext  jaxbContext      = JAXBContext.newInstance(SCWBMetadata.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			// We had written this file in marshalling example
			InputStream  is    = this.getClass().getClassLoader()
			        .getResourceAsStream(this.getPathConstants().getJaxPath_SM_xml());
			SCWBMetadata wbMdt = (SCWBMetadata) jaxbUnmarshaller.unmarshal(is);
			for (SCSheetMetadata shMdt : wbMdt.getSheetMetadata())
			{
				list.add((T) shMdt);
			}
		} catch (Exception Ex)
		{
			throw new Exception(Ex);
		}
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> ArrayList<T> Load_XML_to_Objects(
	        String extfile_path
	) throws Exception
	{
		ArrayList<T> list = new ArrayList<>();
		// Get Objects Back from XML
		try
		{
			JAXBContext  jaxbContext      = JAXBContext.newInstance(SCWBMetadata.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			// We had written this file in marshalling example
			InputStream  is    = this.getClass().getClassLoader().getResourceAsStream(extfile_path);
			SCWBMetadata wbMdt = (SCWBMetadata) jaxbUnmarshaller.unmarshal(is);
			for (SCSheetMetadata shMdt : wbMdt.getSheetMetadata())
			{
				list.add((T) shMdt);
			}
		} catch (Exception Ex)
		{
			throw new Exception(Ex);
		}
		return list;
	}
	
}
