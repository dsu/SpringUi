package pl.springui.components.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import pl.springui.components.exceptions.UiException;

/**
 * Key for a Component implementations methods Notice: @Cachable deost work when method is executed
 * within a bean
 * http://stackoverflow.com/questions/12115996/spring-cache-cacheable-method-ignored-when-called-from-within-the-same-class
 * 
 * @author dsu
 *
 */
@Component("uiComponentKey")
public class UserRequestComponentRequestKey implements KeyGenerator {

  protected final static Logger logger = LoggerFactory
      .getLogger(UserRequestComponentRequestKey.class);

  @Override
  public Object generate(Object o, Method method, Object... params) {

    try {
      method = o.getClass().getMethod("getCacheKey");
      Object invoke = method.invoke(o);
      return invoke;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new UiException("Object doesnt have valid getCacheKey method", e);
    }
  }
}
