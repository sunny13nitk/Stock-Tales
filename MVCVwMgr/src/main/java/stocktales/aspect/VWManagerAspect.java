package stocktales.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.annotations.RetainView;
import stocktales.services.impl.VWManagerSrv;

@Aspect
@Service
public class VWManagerAspect
{
	@Autowired
	private VWManagerSrv vwMgrSrv;

	/*
	 * ADVICE - On Any End point in Controllers Package Annotated with Retain View
	 * Annotation
	 * 
	 * @Before is used since End point might trigger an exception so we capture the
	 * viewName before actual execution
	 */

	@Before("anyPublicStringReturningControllerMethods() && onRetainViewAnnotatedEndPoint()")
	public void retainViewAdvice(JoinPoint jp) throws Throwable
	{

		if (jp != null)
		{
			Object target = jp.getTarget();
			if (target != null)
			{
				MethodSignature signature = (MethodSignature) jp.getSignature();
				Method method = signature.getMethod();
				if (method != null)
				{
					RetainView[] rvWAnn = method.getAnnotationsByType(RetainView.class);
					if (rvWAnn != null)
					{
						if (rvWAnn.length == 1) // Only One Annotation of this type permitted on a single End point
						{
							RetainView rvwA = rvWAnn[0];
							if (rvwA != null && vwMgrSrv != null)
							{
								vwMgrSrv.setViewName(rvwA.viewName());
							}
						}
					}
				}

			}
		}

	}

	/**
	 * Point CUT - On Execution of Any of the View Controller's Public Methods In
	 * the Controllers Package that return a String (View name)
	 */
	@Pointcut("execution(* stocktales.controllers..*.*(..))")
	public void anyPublicStringReturningControllerMethods()
	{

	}

	/**
	 * Point CUT - Annotated with @RetainView Annotation
	 */
	@Pointcut("@annotation(stocktales.annotations.RetainView)")
	public void onRetainViewAnnotatedEndPoint()
	{

	}

	// PRIVATE ROUTINES

	private Annotation getAnnotationforObjbyAnnType(Object obj, final Class<? extends Annotation> annotation)
	{
		Annotation ann = null;

		if (obj != null)
		{

			Class<?> klass = obj.getClass();
			ann = klass.getAnnotation(annotation);

		}

		return ann;
	}
}
