package pl.springui.components.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import pl.springui.example.components.CMSDocumentView;

/**
 * Key for a Component implementations methods Notice: @Cachable deost work when
 * method is executed within a bean
 * http://stackoverflow.com/questions/12115996/spring-cache-cacheable-method-ignored-when-called-from-within-the-same-class
 * 
 * @author dsu
 *
 */
@Component("cmsAliasKey")
public class CMSAliasKey implements KeyGenerator {

	@Override
	public Object generate(Object o, Method method, Object... params) {
		CMSDocumentView c = (CMSDocumentView) o;
		System.out.println("alias key :" + c.getAlias());
		//TODO - dodac metode to czyszczenia cache
		return c.getAlias();
	}
}
