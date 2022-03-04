package stocktales.healthcheck.repo.intf;

import stocktales.healthcheck.enums.EnumHCCriteria;

public interface IRepoCfgSrvStext
{
	EnumHCCriteria getStext(
	);
	
	String getSrvname(
	);
	
	Boolean getForFinancials(
	);
}
