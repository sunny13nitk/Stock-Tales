package stocktales.dataBook.model.repo.repoIntf;

import org.springframework.beans.factory.annotation.Value;

import stocktales.dataBook.enums.EnumInterval;

public interface IFieldPoolShort
{
	Long getId(
	);
	
	EnumInterval getInterval(
	);
	
	Integer getValm(
	);
	
	Integer getVald(
	);
	
	@Value("#{target.valm + '- Q' + target.vald}")
	String getIntervalText(
	);
	
	String getNotes(
	);
}
