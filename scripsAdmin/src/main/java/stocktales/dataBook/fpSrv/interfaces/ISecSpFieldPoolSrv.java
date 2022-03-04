package stocktales.dataBook.fpSrv.interfaces;

import java.util.List;
import java.util.Optional;

import stocktales.dataBook.model.pojo.PJIntervalSimple;

public interface ISecSpFieldPoolSrv<T> extends IFieldPoolsShortData
{
	public List<T> getAnnualData(
	        String scCode
	);
	
	public List<T> getQuarterlyData(
	        String scCode
	);
	
	public Optional<T> getFieldPoolDataById(
	        long fpid
	);
	
	public Optional<T> getFieldPoolDataByDuration(
	        PJIntervalSimple interval, String scCode
	);
	
	public boolean isDataExistingforDuration(
	        PJIntervalSimple interval, String scCode
	);
	
	public List<T> getFieldPoolDataforScrip(
	        String scCode
	);
	
	public T saveorUpdate(
	        Object T
	);
	
	public void deleteById(
	        long fpid
	);
	
}
