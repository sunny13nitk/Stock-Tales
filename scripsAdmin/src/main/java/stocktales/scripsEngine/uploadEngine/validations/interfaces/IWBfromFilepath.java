package stocktales.scripsEngine.uploadEngine.validations.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * Interface to return orkbook Context reference from a physical filepath
 * Prototype bean servie to implement this interface
 */
public interface IWBfromFilepath
{
	public XSSFWorkbook getWBcontextfromFilepath(String filepath) throws FileNotFoundException, IOException;

}
