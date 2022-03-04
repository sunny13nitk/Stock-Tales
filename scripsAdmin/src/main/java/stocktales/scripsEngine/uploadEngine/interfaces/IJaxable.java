package stocktales.scripsEngine.uploadEngine.interfaces;

import java.util.ArrayList;

// Interface for Jaxabale Objects -- which can be store to and from XML file formats at
// specified locations

// Generic Methods definiton to easily generate to and from XML
public interface IJaxable
{
	// Generate XSD for the Class- Schema Generator
	public void Generate_XSD() throws Exception;

	// Generate XML for the Class - XML Generator
	public void Generate_XML() throws Exception;

	// Load Objects from XML at desired location - Load Objects from XML
	// file
	public <T> ArrayList<T> Load_XML_to_Objects() throws Exception;

	// Load Objects from XML at desired location - External file path for XML - Load Obejcts from XML
	// file - As per specified ObjectSchemas XSD
	public <T> ArrayList<T> Load_XML_to_Objects(String extfile_path) throws Exception;

}
