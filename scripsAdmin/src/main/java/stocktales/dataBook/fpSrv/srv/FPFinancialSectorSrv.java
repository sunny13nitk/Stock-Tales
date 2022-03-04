package stocktales.dataBook.fpSrv.srv;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.ISecSpFieldPoolSrv;
import stocktales.dataBook.model.entity.sectors.FPFinancialSector;
import stocktales.dataBook.model.pojo.PJIntervalSimple;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShort;
import stocktales.dataBook.model.repo.sectors.RepoFPFinancialSector;

@Service("FPFinancialSectorSrv")
public class FPFinancialSectorSrv implements ISecSpFieldPoolSrv<FPFinancialSector>
{
	@Autowired
	private RepoFPFinancialSector repoFPFinSec;
	
	@Override
	public List<FPFinancialSector> getAnnualData(
	        String scCode
	)
	{
		return repoFPFinSec.findAllBySccode(scCode).stream().filter(x -> x.getInterval() == EnumInterval.Annual)
		        .collect(Collectors.toList());
	}
	
	@Override
	public List<FPFinancialSector> getQuarterlyData(
	        String scCode
	)
	{
		return repoFPFinSec.findAllBySccode(scCode).stream().filter(x -> x.getInterval() == EnumInterval.Quarterly)
		        .collect(Collectors.toList());
	}
	
	@Override
	public Optional<FPFinancialSector> getFieldPoolDataById(
	        long fpid
	)
	{
		
		return repoFPFinSec.findById(fpid);
	}
	
	@Override
	public Optional<FPFinancialSector> getFieldPoolDataByDuration(
	        PJIntervalSimple interval, String scCode
	)
	{
		return repoFPFinSec.findAlLBySccodeAndIntervalAndValmAndVald(scCode, interval.getInterval(), interval.getValm(),
		        interval.getVald());
	}
	
	@Override
	public boolean isDataExistingforDuration(
	        PJIntervalSimple interval, String scCode
	)
	{
		
		boolean                     isPresent = false;
		Optional<FPFinancialSector> fpDataO   = repoFPFinSec.findAlLBySccodeAndIntervalAndValmAndVald(scCode,
		        interval.getInterval(), interval.getValm(), interval.getVald());
		if (fpDataO.isPresent())
		{
			isPresent = true;
		}
		return isPresent;
	}
	
	@Override
	public List<FPFinancialSector> getFieldPoolDataforScrip(
	        String scCode
	)
	{
		
		return repoFPFinSec.findAllBySccode(scCode);
	}
	
	@Override
	public FPFinancialSector saveorUpdate(
	        Object T
	)
	{
		return repoFPFinSec.save((FPFinancialSector) T);
	}
	
	@Override
	public void deleteById(
	        long fpid
	)
	{
		repoFPFinSec.deleteById(fpid);
		
	}
	
	@Override
	public List<IFieldPoolShort> findAllByInterval(
	        EnumInterval intervalType
	)
	{
		
		return repoFPFinSec.findAllByInterval(intervalType);
	}
	
}
