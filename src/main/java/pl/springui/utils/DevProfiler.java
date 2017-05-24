package pl.springui.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.context.annotation.Profile;

/**
 * Annotation for methods that needs to be profiled on dev profile
 * 
 * @author dsu
 *
 */
@Profile("dev")
public class DevProfiler extends SimpleProfiler {

	@Override
	@Around(value = "@within(ProductionProfile) || @annotation(ProductionProfile)")
	public Object profile(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		return super.profile(proceedingJoinPoint);
	}
}
