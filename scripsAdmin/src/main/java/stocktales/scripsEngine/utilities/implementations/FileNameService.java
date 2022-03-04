package stocktales.scripsEngine.utilities.implementations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import stocktales.scripsEngine.utilities.interfaces.IFileNameService;

@Service
public class FileNameService implements IFileNameService
{
	
	@Override
	public String getFileNameDate(
	        String prefix, String filePath, boolean isXlsX
	)
	{
		String fileName = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar   cal        = Calendar.getInstance();
		String     date       = (dateFormat.format(cal.getTime()));
		
		if (isXlsX)
		{
			fileName = filePath + prefix + date + ".xlsx";
		} else
		{
			fileName = filePath + prefix + date;
		}
		return fileName;
	}
	
	@Override
	public String getFileNameDuration(
	        String prefix, String filePath, boolean isXlsX
	)
	{
		String fileName = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Calendar   cal        = Calendar.getInstance();
		String     date       = (dateFormat.format(cal.getTime()));
		
		if (isXlsX)
		{
			fileName = filePath + prefix + date + ".xlsx";
		} else
		{
			fileName = filePath + prefix + date;
		}
		return fileName;
	}
	
	@Override
	public String getFileNameDate(
	        String prefix, String filePath, String fileExtension
	)
	{
		String fileName = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar   cal        = Calendar.getInstance();
		String     date       = (dateFormat.format(cal.getTime()));
		
		fileName = filePath + prefix + date + fileExtension;
		
		return fileName;
	}
	
	@Override
	public String getFileNameDuration(
	        String prefix, String filePath, String fileExtension
	)
	{
		String fileName = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Calendar   cal        = Calendar.getInstance();
		String     date       = (dateFormat.format(cal.getTime()));
		
		fileName = filePath + prefix + date + fileExtension;
		
		return fileName;
	}
	
}
