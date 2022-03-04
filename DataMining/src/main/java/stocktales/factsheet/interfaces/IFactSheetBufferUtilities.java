package stocktales.factsheet.interfaces;

import java.util.List;

import stocktales.factsheet.helperPOJO.MinMaxBalSheetYear;

/*
 * This will Act on FactSheetBuffer Session Srv Bean - Have it Autowired
 * to execute Utility Specific Methods on Same
 */
public interface IFactSheetBufferUtilities
{
	public MinMaxBalSheetYear getMinMaXBalSheetYearforScrip(
	        String scCode
	);
	
	public List<MinMaxBalSheetYear> getBalSheetYears(
	);
	
}
