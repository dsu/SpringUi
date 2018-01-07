package pl.springui.proxy;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.HTMLRenderer;
import pl.springui.proxy.beans.BaseBean;

@EnableCaching
@Configuration
@ComponentScan(value = { "pl.springui.proxy" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopTest {

	/**
	 * Dziala po interfejsie tylko kiedy jest primary, Qualifier nie dziala.
	 * 
	 * @author dsu
	 *
	 */
	// @Lazy
	@Primary
	// @Qualifier("s")
	@Component
	@EqualsAndHashCode
	@Scope(value = "prototype", proxyMode = org.springframework.context.annotation.ScopedProxyMode.INTERFACES)
	class BeanA extends BaseBean implements HTMLRenderer {
	}

	@Component
	@Scope(value = "prototype")
	static class TestBeanContainer extends BaseBean {

		// @Qualifier("s")
		@Autowired
		HTMLRenderer beanA;

		@PostConstruct
		public void initIt() {
			System.out.println("Container @PostConstruct");
		}

		public void test() {
			System.out.println("test:");
			BaseBean.print(beanA);
			BaseBean.print(beanA);
		}

	}

	@Test
	public void testCache() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AopTest.class);
		TestBeanContainer bean = context.getBean(TestBeanContainer.class);
		bean.test();
		bean.test();
	}

}
