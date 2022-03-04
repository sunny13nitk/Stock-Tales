package stocktales.scripsEngine.uploadEngine.tools.interfaces;

import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

public interface IHeadersDeltaGetSrv
{
	public <T> ArrayList<T> getHeadersDelta(
	        XSSFSheet sheetRef, ArrayList<T> sheetEntityList, SCSheetMetadata shtMdt
	) throws EX_General;
}
