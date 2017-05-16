package pl.springui.components.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;

/**
 * Key for a Component implementations methods Notice: @Cachable deost work when
 * method is executed within a bean
 * http://stackoverflow.com/questions/12115996/spring-cache-cacheable-method-ignored-when-called-from-within-the-same-class
 * 
 * @author dsu
 *
 */
@Component("uiComponentKey")
public class UserRequestComponentRequestKey implements KeyGenerator {

	@Override
	public Object generate(Object o, Method method, Object... params) {
		UiComponent c = (UiComponent) o;
		Object key = c.getCacheKey();
		return key;
	}
}
