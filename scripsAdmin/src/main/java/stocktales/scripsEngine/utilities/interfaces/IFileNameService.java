package stocktales.scripsEngine.utilities.interfaces;

public interface IFileNameService
{
	public String getFileNameDate(String prefix, String filePath, boolean isXlsX);

	public String getFileNameDuration(String prefix, String filePath, boolean isXlsX);

	public String getFileNameDate(String prefix, String filePath, String fileExtension);

	public String getFileNameDuration(String prefix, String filePath, String fileExtension);
}
