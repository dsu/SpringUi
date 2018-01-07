package pl.springui.proxy;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.proxy.beans.BaseBean;
import pl.springui.proxy.beans.CachedFakeInterface;

/**
 * @Scope(value = "singleton", proxyMode =
 *              org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
 *              - only one instance
 * @Scope(value = "prototype", proxyMode =
 *              org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
 *              - different object every single REFERENCE
 * 
 * @Scope(value = "prototype", proxyMode =
 *              org.springframework.context.annotation.ScopedProxyMode.INTERFACES)
 *              - needs to implements a interface and be referenced by it,
 *              different instances
 * 
 * 
 *              Uwagi: z wewnątrz klasy obiekt widzi siebie zawsze jako obiekt
 *              bez proxy, odwolania zewnetrzne sa tylko przez Proxy
 *
 *
 *
 *Teoria:
 * Spring nie musi zawsze tworzyc proxy - jest opcja proxy mode - No, i to jest domyślne. Nie jest to konieczne do injekcji.
 * 
 *  Jeżeli jest już jakiś proxy (mode target class lub interfaces) to przy kazdym odwolaniu do obiektu spring sięga do fabryki i jezeli jest to prototype to tworzy nowy obiekt.
 *  Jeżeli jest to singleton to nigdy nie tworzy kolejnej instancji.
 *  
 *  
 *  @Lazy
 *  
 *  Jeżeli np. singleton ma taka adontacje (na klasie! nie odwilaniu) to nie będzie tworzony przy starcie kontekstu, tylko wtedy gdy będzie potrzebny.
 *  
 *  Jeżeli  @Lazy jest na referencji to Spring zawsze tworzy dodatkowe proxy (nawet jezeli juz jest jakies) i przy kazdym odwolaniu do obiektu siega do fabryki.
 *  Jeżeli jest to referencja typu klasy to używa CGLIB, jeżeli interfejsu to Javowe.
 * 
 */
@EnableCaching
@Configuration
@ComponentScan(value = { "pl.springui.proxy" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopWithCachableTest {

	@Component
	@Scope(value = "prototype")
	static class TestBeanContainer extends BaseBean {

		@Autowired
		CachedFakeInterface beanA;

		public TestBeanContainer() {
			super();
		}

		public void test() {
			BaseBean.print(beanA);
		}

	}

	@Bean(name = "springCM")
	public ConcurrentMapCacheManager cacheManager() {
		return new ConcurrentMapCacheManager("entities");
	}

	@Test
	public void testCache() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AopWithCachableTest.class);
		TestBeanContainer bean = context.getBean(TestBeanContainer.class);
		bean.test();
		bean.test();
	}

}
