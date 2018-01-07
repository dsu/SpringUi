package pl.springui.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Annotation for methods that needs to be profiled on dev profile
 * 
 * @author dsu
 *
 */
@Profile("profiler")
@Component
@Aspect
public class SimpleProfiler extends AbstractProfiler {
	@Override
	@Around(value = "@within(Profiler) || @annotation(Profiler)")
	public Object profile(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		return super.profile(proceedingJoinPoint);
	}
}
