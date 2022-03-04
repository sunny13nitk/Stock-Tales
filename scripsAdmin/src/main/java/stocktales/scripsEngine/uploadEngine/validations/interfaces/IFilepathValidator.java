package stocktales.scripsEngine.uploadEngine.validations.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFilepathValidator
{

	public boolean validateFilePath(String filepath) throws FileNotFoundException, IOException;

	public String getFilePath();
}
