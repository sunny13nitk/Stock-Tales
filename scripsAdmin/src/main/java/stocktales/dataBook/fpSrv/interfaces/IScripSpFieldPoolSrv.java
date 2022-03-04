package stocktales.dataBook.fpSrv.interfaces;

import java.util.List;
import java.util.Optional;

import stocktales.dataBook.model.pojo.PJIntervalSimple;

public interface IScripSpFieldPoolSrv<T> extends IFieldPoolsShortDataSimple
{
	public List<T> getAnnualData(
	
	);
	
	public List<T> getQuarterlyData(
	
	);
	
	public Optional<T> getFieldPoolDataById(
	        long fpid
	);
	
	public List<T> getFieldPoolDataByDuration(
	        PJIntervalSimple interval
	);
	
	public boolean isDataExistingforDuration(
	        PJIntervalSimple interval
	);
	
	public T saveorUpdate(
	        Object T
	);
	
	public void deleteById(
	        long fpid
	);
	
}
