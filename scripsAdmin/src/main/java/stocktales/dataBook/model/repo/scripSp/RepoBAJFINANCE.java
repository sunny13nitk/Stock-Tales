package stocktales.dataBook.model.repo.scripSp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.IFieldPoolsShortDataSimple;
import stocktales.dataBook.model.entity.scripSp.FPBAJFINANCE;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShortSimple;

@Repository
public interface RepoBAJFINANCE extends JpaRepository<FPBAJFINANCE, Long>, IFieldPoolsShortDataSimple
{
	public List<IFieldPoolShortSimple> findAllByInterval(
	        EnumInterval intervalType
	);
	
	public List<FPBAJFINANCE> findAllByIntervalAndValmAndVald(
	        EnumInterval interval, int mainVal, int detailVal
	);
	
}
