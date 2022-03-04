package stocktales.dataBook.fpSrv.srv.scripSp;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.fpSrv.interfaces.IScripSpFieldPoolSrv;
import stocktales.dataBook.model.entity.scripSp.FPBAJFINANCE;
import stocktales.dataBook.model.pojo.PJIntervalSimple;
import stocktales.dataBook.model.repo.repoIntf.IFieldPoolShortSimple;
import stocktales.dataBook.model.repo.scripSp.RepoBAJFINANCE;

@Service("FPBAJFINANCESrv")
public class FPBAJFINANCESrv implements IScripSpFieldPoolSrv<FPBAJFINANCE>
{
	@Autowired
	private RepoBAJFINANCE repoBFAL;
	
	@Override
	public List<IFieldPoolShortSimple> findAllByInterval(
	        EnumInterval intervalType
	)
	{
		
		return repoBFAL.findAllByInterval(intervalType);
	}
	
	@Override
	public List<FPBAJFINANCE> getAnnualData(
	
	)
	{
		
		return repoBFAL.findAll().stream().filter(x -> x.getInterval() == EnumInterval.Annual)
		        .collect(Collectors.toList());
	}
	
	@Override
	public List<FPBAJFINANCE> getQuarterlyData(
	
	)
	{
		return repoBFAL.findAll().stream().filter(x -> x.getInterval() == EnumInterval.Quarterly)
		        .collect(Collectors.toList());
	}
	
	@Override
	public Optional<FPBAJFINANCE> getFieldPoolDataById(
	        long fpid
	)
	{
		
		return repoBFAL.findById(fpid);
	}
	
	@Override
	public List<FPBAJFINANCE> getFieldPoolDataByDuration(
	        PJIntervalSimple interval
	)
	{
		return repoBFAL.findAllByIntervalAndValmAndVald(interval.getInterval(), interval.getValm(), interval.getVald());
	}
	
	@Override
	public boolean isDataExistingforDuration(
	        PJIntervalSimple interval
	)
	{
		boolean isfound = false;
		if (this.getFieldPoolDataByDuration(interval) != null)
		{
			isfound = true;
		}
		
		return isfound;
	}
	
	@Override
	public FPBAJFINANCE saveorUpdate(
	        Object T
	)
	{
		
		return repoBFAL.save((FPBAJFINANCE) T);
	}
	
	@Override
	public void deleteById(
	        long fpid
	)
	{
		repoBFAL.deleteById(fpid);
		
	}
	
}
