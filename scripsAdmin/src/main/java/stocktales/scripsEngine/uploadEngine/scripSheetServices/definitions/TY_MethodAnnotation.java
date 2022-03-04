/**
 * 
 */
package stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 
 * Type for Holding Method and Annotation so a class is scanned only Once
 * for all relevant annotations
 */
public class TY_MethodAnnotation
	{
		public Class<? extends Annotation>	annotationClass;
		public Method								method;
		
		/**
		 * @param annotationClass
		 * @param method
		 */
		public TY_MethodAnnotation(Class<? extends Annotation> annotationClass, Method method)
			{
				
				this.annotationClass = annotationClass;
				this.method = method;
			}
			
	}
