package pl.springui.proxy.beans;

import java.util.Date;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.HTMLRenderer;


@Component
@EqualsAndHashCode
@Scope(value = "singleton", proxyMode = org.springframework.context.annotation.ScopedProxyMode.NO)
public class CachedMethodBeanNoModeSingleton extends BaseBean implements HTMLRenderer {

	public CachedMethodBeanNoModeSingleton() {
		super();
	}

	@Override
  @Cacheable(cacheNames = "entities", keyGenerator = "keyGenerator")
	public String renderResponse() {
		System.out.println("execute ...");
		return "cached " + new Date() + ", id: " + getId();
	}

}
