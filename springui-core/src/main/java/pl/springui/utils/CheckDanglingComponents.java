package pl.springui.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponentI;

@Aspect
@Profile("dev")
@Component
public class CheckDanglingComponents {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Before("execution(* pl.springui.components.UiComponent.renderResponse())")
	public void before(JoinPoint joinPoint) throws Throwable {
		UiComponentI c = (UiComponentI) joinPoint.getTarget();
		if (c.getParent() == null) {
			logger.warn("Dangling component! {}", c);
		}
	}

}