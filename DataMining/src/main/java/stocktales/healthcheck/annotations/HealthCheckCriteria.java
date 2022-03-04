package stocktales.healthcheck.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import stocktales.healthcheck.enums.EnumHCCriteria;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HealthCheckCriteria
{
	EnumHCCriteria criteria() default EnumHCCriteria.Other;
	
	boolean isApplicableforFinancials() default true;
}
