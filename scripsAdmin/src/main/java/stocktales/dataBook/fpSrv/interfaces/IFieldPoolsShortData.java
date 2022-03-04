package stocktales.dataBook.fpSrv.interfaces;

import java.util.List;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShort;

public interface IFieldPoolsShortData
{
	public List<IFieldPoolShort> findAllByInterval(
	        EnumInterval intervalType
	);
}
