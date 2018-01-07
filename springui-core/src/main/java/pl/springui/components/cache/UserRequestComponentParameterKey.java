package pl.springui.components.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;

/**
 * Key for methods with Component as a method parameter
 * 
 * @author dsu
 *
 */
@Component("uiComponentArgumentKey")
public class UserRequestComponentParameterKey implements KeyGenerator {

	@Override
	public Object generate(Object o, Method method, Object... params) {
		UiComponent c = (UiComponent) params[0];
		Object key = c.getCacheKey();
		return key;

	}
}
