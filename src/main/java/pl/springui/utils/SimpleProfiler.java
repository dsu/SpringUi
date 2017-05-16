package pl.springui.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

/**
 * Annotation for methods that needs to be profiled on prod profile. Can add
 * some extra milliseconds due to AOP magic.
 * 
 * @author dsu
 *
 */
@Component
@Profile("prod")
@Aspect
public class SimpleProfiler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Spring AOP 'around' reference method signature is bounded like this, the
	 * method name "profile" should be same as defined in spring.xml aop:around
	 * section.
	 **/
	@Around(value = "@within(Profiler) || @annotation(Profiler)")
	public Object profile(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(proceedingJoinPoint.toShortString());
		boolean isExceptionThrown = false;
		try {
			// execute the profiled method
			return proceedingJoinPoint.proceed();
		} catch (RuntimeException e) {
			isExceptionThrown = true;
			throw e;
		} finally {
			stopWatch.stop();
			TaskInfo taskInfo = stopWatch.getLastTaskInfo();
			// Log the method's profiling result
			String profileMessage = taskInfo.getTaskName() + ": " + taskInfo.getTimeMillis() + " ms"
					+ (isExceptionThrown ? " (thrown Exception)" : "");
			logger.info(profileMessage);
		}
	}
}