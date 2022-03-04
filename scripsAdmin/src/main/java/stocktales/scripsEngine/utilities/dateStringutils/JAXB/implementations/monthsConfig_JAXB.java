package stocktales.scripsEngine.utilities.dateStringutils.JAXB.implementations;

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

import stocktales.scripsEngine.uploadEngine.interfaces.IJaxable;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.Months;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.PathsJAXBMO;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.monthNames;
import stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions.monthsConfig;

public class monthsConfig_JAXB implements IJaxable
{
	
	private PathsJAXBMO pathConstants;
	
	public PathsJAXBMO getPathConstants(
	)
	{
		return pathConstants;
	}
	
	public void setPathConstants(
	        PathsJAXBMO pathConstants
	)
	{
		this.pathConstants = pathConstants;
	}
	
	public monthsConfig_JAXB(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public monthsConfig_JAXB(
	        PathsJAXBMO pathConstants
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
		
		JAXBContext context = JAXBContext.newInstance(Months.class);
		
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
				        File file1 = new File(getPathConstants().getJaxPath_MO_xsd());
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
		Months months = new Months();
		
		monthNames monNames = new monthNames("JAN", "JANUARY");
		
		monthsConfig mconfig1 = new monthsConfig(monNames, 1, 1);
		
		months.getMonthConfig().add(mconfig1);
		
		// Marshal Here
		JAXBContext jaxbContext    = JAXBContext.newInstance(Months.class);
		Marshaller  jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		// Marshal the ObjectSchemas to file in OS Specific Location as
		// per properties file
		jaxbMarshaller.marshal(months, new File(this.getPathConstants().getJaxPath_MO_xml_GEN()));
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
			JAXBContext  jaxbContext      = JAXBContext.newInstance(Months.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			// We had written this file in marshalling example
			InputStream is      = this.getClass().getClassLoader()
			        .getResourceAsStream(this.getPathConstants().getJaxPath_MO_xml());
			Months      monfigL = (Months) jaxbUnmarshaller.unmarshal(is);
			for (monthsConfig monConfig : monfigL.getMonthConfig())
			{
				list.add((T) monConfig);
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
			JAXBContext  jaxbContext      = JAXBContext.newInstance(Months.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			// We had written this file in marshalling example
			InputStream is         = this.getClass().getClassLoader().getResourceAsStream(extfile_path);
			Months      monthslist = (Months) jaxbUnmarshaller.unmarshal(is);
			// for ( Model model : models.getModels() )
			// {
			list.add((T) monthslist);
			// }
		} catch (Exception Ex)
		{
			throw new Exception(Ex);
		}
		return list;
	}
	
}
