package stocktales.scripsEngine.uploadEngine.validations.interfaces;

import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Sheet Validator Interface - Generic
 *
 */
public interface ISheetValidator
{

	public boolean validateSheet(XSSFSheet sheet);

}
