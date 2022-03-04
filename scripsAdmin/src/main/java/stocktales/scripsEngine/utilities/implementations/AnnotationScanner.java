/**
 * 
 */
package stocktales.scripsEngine.utilities.implementations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import stocktales.scripsEngine.uploadEngine.exceptions.EX_DuplicateAnnotationException;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.definitions.TY_MethodAnnotation;

/**
 * Scans an Object for Annotation and returns the relevant Method
 *
 */
public class AnnotationScanner
{
	/************************************************************************************************************************************************
	 * Get Annotations Class and Methods for a Particualr Object Class in an ArrayList
	 * 
	 * @param classType   - Class to Scan for Annotations
	 * @param annotations - Annotations to scan for - specify fully qualified Class Object
	 * @return - Array List of Annotations and respective Methods
	 ************************************************************************************************************************************************/
	public static ArrayList<TY_MethodAnnotation> getMethodsforAnnotations(
	        final Class<?> classType, final Class<? extends Annotation>[] annotations
	)
	{
		ArrayList<TY_MethodAnnotation> methodAnnotatons = new ArrayList<TY_MethodAnnotation>();
		
		Class<?> klass = classType;
		
		final ArrayList<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
		for (final Method method : allMethods)
		{
			for (Class<? extends Annotation> annotation : annotations)
			{
				if (method.isAnnotationPresent(annotation))
				{
					// Annotation annotInstance = method.getAnnotation(annotation);
					// TODO process annotInstance
					methodAnnotatons.add(new TY_MethodAnnotation(annotation, method));
				}
			}
			
		}
		
		return methodAnnotatons;
	}
	
	/************************************************************************************************************************************************
	 * Get Methods for a Particualr Object Class that implments specified Annotation
	 * 
	 * @param classType   - Class to Scan for Annotations
	 * @param annotations - Annotations to scan for - specify fully qualified Class Objects
	 * @return - Method that implments the Annotation in question
	 ************************************************************************************************************************************************/
	public static ArrayList<Method> getMethodsforAnnotation(
	        final Class<?> classType, final Class<? extends Annotation> annotation
	)
	{
		ArrayList<Method> methods = new ArrayList<Method>();
		
		Class<?> klass = classType;
		
		final ArrayList<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
		for (final Method method : allMethods)
		{
			if (method.isAnnotationPresent(annotation))
			{
				// Annotation annotInstance = method.getAnnotation(annotation);
				// TODO process annotInstance
				methods.add(method);
			}
		}
		
		return methods;
	}
	
	/****************************************************************************************************************************
	 * Get Methods for a Specific Annotation from the Annotation List Also Checks for Unique Occurence of an annotation
	 * within the list and throws exception in case checkUnique is set to true
	 * 
	 * @param annotationList     - List of Annotation and Methods - To Geneate for an Object Class refer method
	 *                           "getMethodsforAnnotations"
	 * @param annotationtoSearch - Annotation to Seach for - Fully Qualified Class Object
	 * @param checkUnique        - Set to true if should throw an Exception in case of multiple methods detected
	 *                           implementing the annotation in question
	 * @return - ArrayList of Methods
	 * @throws EX_DuplicateAnnotationException - In case of multiple methods detected implementing the annotation in
	 *                                         question
	 ******************************************************************************************************************************/
	public static ArrayList<Method> getMethodfromAnnotationList(
	        ArrayList<TY_MethodAnnotation> annotationList, final Class<? extends Annotation> annotationtoSearch,
	        boolean checkUnique
	) throws EX_DuplicateAnnotationException
	{
		ArrayList<Method>              methods  = new ArrayList<Method>();
		ArrayList<TY_MethodAnnotation> filtered = new ArrayList<TY_MethodAnnotation>();
		
		if (annotationList != null && annotationtoSearch != null)
		{
			if (annotationList.size() > 0)
			{
				filtered = (ArrayList<TY_MethodAnnotation>) annotationList.stream()
				        .filter(x -> x.annotationClass.equals(annotationtoSearch)).collect(Collectors.toList());
				if (checkUnique == true)
				{
					/**
					 * If size > 1 throw duplicate Annotation Exception
					 */
					if (filtered.size() > 1)
					{
						throw new EX_DuplicateAnnotationException(new Object[]
						{ annotationtoSearch.getSimpleName() });
					} else
					{
						/**
						 * Loop thorugh List and return methods list
						 */
						for (TY_MethodAnnotation ty_MethodAnnotation : filtered)
						{
							methods.add(ty_MethodAnnotation.method);
						}
					}
				} else
				{
					/**
					 * Loop thorugh List and return methods list
					 */
					for (TY_MethodAnnotation ty_MethodAnnotation : filtered)
					{
						methods.add(ty_MethodAnnotation.method);
					}
				}
			}
		}
		
		return methods;
		
	}
	
	/**
	 * Get Annotation from a Particular Object Instance and the Annotation Type Fully Qualified
	 * 
	 * @param obj        - Object Instance that has one of the methods Annotated with Annotation to be Search Specified
	 * @param annotation - Fully Qualified Annotation Class
	 * @return - Annotation Instance derived from Method within the Class
	 */
	public static Annotation getAnnotationforObjMethodsbyAnnType(
	        Object obj, final Class<? extends Annotation> annotation
	)
	{
		Annotation ann = null;
		
		if (obj != null)
		{
			
			Class<?> klass = obj.getClass();
			
			final ArrayList<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
			for (final Method method : allMethods)
			{
				if (method.isAnnotationPresent(annotation))
				{
					ann = method.getAnnotation(annotation);
					
				}
			}
			
		}
		
		return ann;
	}
	
	public static Annotation getAnnotationforObjbyAnnType(
	        Object obj, final Class<? extends Annotation> annotation
	)
	{
		Annotation ann = null;
		
		if (obj != null)
		{
			
			Class<?> klass = obj.getClass();
			if (klass != null)
			{
				Annotation[] annList = klass.getAnnotations();
				if (annList != null)
				{
					for (int i = 0; i < annList.length; i++)
					{
						if (annList[i].getClass().equals(annotation))
						{
							ann = annList[i];
							
						}
					}
				}
			}
			
		}
		
		return ann;
	}
	
	/*
	 * Get Annotation for a Join or Proceeding JoinPoint for a fully Qualified Annotation Class For Multiple Instances
	 * of same Annotation over various Jp.Target- Class Methods, only the one where out of the signature of target
	 * Methods matches the signature of invoked JP/PJP , the annotation is returned e.g. @DBMark on both save and delete
	 * method in DAO class ; but at runtime if delte method is trigerred the Annotation on Delete Method is only
	 * returned
	 */
	public static Annotation getAnnotationforJPbyAnnType(
	        Object jp, final Class<? extends Annotation> annotation
	)
	{
		Annotation ann = null;
		
		if (jp != null)
		{
			if (jp instanceof JoinPoint || jp instanceof ProceedingJoinPoint)
			{
				JoinPoint jpCast = (JoinPoint) jp; // Join point in Super Interface of PJP so will cast to more
				                                   // generic
				
				Class<?> klass = jpCast.getTarget().getClass();
				
				final ArrayList<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
				for (final Method method : allMethods)
				{
					if (method.isAnnotationPresent(annotation))
					{
						if (method.getName().equals(jpCast.getSignature().getName()))
						{
							/*
							 * Looking Specifically for Join Point Method if the Class has multiple same Annotations
							 */
							ann = method.getAnnotation(annotation);
						}
						
					}
				}
				
			}
			
		}
		
		return ann;
	}
}
