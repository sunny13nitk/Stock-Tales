package stocktales.dataBook.model.repo.sectors;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.IFieldPoolsShortData;
import stocktales.dataBook.model.entity.sectors.FPFinancialSector;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShort;

@Repository
public interface RepoFPFinancialSector extends JpaRepository<FPFinancialSector, Long>, IFieldPoolsShortData
{
	public List<FPFinancialSector> findAllBySccode(
	        String scCode
	);
	
	public Optional<FPFinancialSector> findAlLBySccodeAndIntervalAndValmAndVald(
	        String scCode, EnumInterval interval, int mainVal, int detailVal
	);
	
	public List<IFieldPoolShort> findAllByInterval(
	        EnumInterval intervalType
	);
}
