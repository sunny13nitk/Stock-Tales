package stocktales.scripsEngine.uploadEngine.validations.implementations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.validations.interfaces.IFilepathValidator;

/**
 * Filepath Validation Service - Prototype BEan - New Instance returned when requested from Applicatipon context
 *
 */
@Service("FilepathValidationService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Always Create a New Instance)
public class FilepathValidationService implements IFilepathValidator
{
	private String filePath;
	
	@Override
	public boolean validateFilePath(
	        String filepath
	) throws FileNotFoundException, IOException
	{
		boolean         validpath = false;
		FileInputStream fis       = new FileInputStream(filepath);
		if (fis != null)
		{
			this.filePath = filepath;
			fis.close();
			validpath = true;
		}
		
		return validpath;
	}
	
	@Override
	public String getFilePath(
	)
	{
		return filePath;
	}
	
}
