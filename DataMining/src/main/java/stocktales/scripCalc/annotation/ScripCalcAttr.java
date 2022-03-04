/**
 * 
 */
package stocktales.scripCalc.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import stocktales.scripCalc.enums.EnumCalcOperation;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;

@Retention(RUNTIME)
@Target(FIELD)
/**
 * Scrip Calculation Attributes Specification 
 * 	- On : Custom Fields needed to be Computed
 *   - DEfined with : Scrip Sheet Name, Attribute1, Attribute2, Kind of Operation - EnumCalcOperation
 *   - Used to define Services that may contain 1 or more fields with this annotations to get values for a scrip
 *   - Handled: Centrally via Aspect
 *   - Utilizes : ScripDataContainer 
 *   - Sheet Name from stocktales.scripsEngine.pojos.helperObjs.SheetNames
 *
 */
public @interface ScripCalcAttr
{
	String label() default "";
	
	String sheetName() default SheetNames.Analysis;
	
	String attr1() default "";
	
	String attr2() default "";
	
	EnumCalcOperation operator() default EnumCalcOperation.Average;
	
}
