package stocktales.DataLake.srv.intf;

import org.springframework.web.multipart.MultipartFile;

public interface DL_ScripPricesUploadSrv
{
	public boolean UploadScripPrices(MultipartFile file) throws Exception;

	public boolean RefreshAndUploadScripPrices(MultipartFile file) throws Exception;

}
