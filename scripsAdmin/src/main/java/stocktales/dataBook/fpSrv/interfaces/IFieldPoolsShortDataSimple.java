package stocktales.dataBook.fpSrv.interfaces;

import java.util.List;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShortSimple;

public interface IFieldPoolsShortDataSimple
{
	public List<IFieldPoolShortSimple> findAllByInterval(
	        EnumInterval intervalType
	);
}
