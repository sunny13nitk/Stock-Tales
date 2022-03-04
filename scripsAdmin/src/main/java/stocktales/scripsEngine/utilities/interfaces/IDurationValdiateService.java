package stocktales.scripsEngine.utilities.interfaces;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;

/**
 * Interface for Duration Validation Service
 *
 */
public interface IDurationValdiateService
{
	/**
	 * Validate Year in Comparison from Base to Penultimate Year
	 * 
	 * @param cmpYear - Year in Comparison
	 * @throws EX_General
	 */
	public void validateYear_BasetoPenultimate(
	        int cmpYear
	) throws EX_General;
	
	/**
	 * Validate Year in Comparison from Base to Current Year
	 * 
	 * @param cmpYear - Year in Comparison
	 * @throws EX_General
	 */
	public void validateYear_BasetoCurrent(
	        int cmpYear
	) throws EX_General;
	
	/**
	 * Validate Quarter and Year to from base Year and base Quarter to Current Year and Penultimate Quarter
	 * 
	 * @param cmpYear - Year in Comparison
	 * @param Quarter - Quarter in Comparison
	 * @throws EX_General
	 */
	public void validateQYear_BasetoPenultimateQ(
	        int cmpYear, int Quarter
	) throws EX_General;
	
}
