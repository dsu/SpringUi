package pl.springui.http;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

import pl.springui.components.UiComponent;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.tree.Tree;
import pl.springui.components.tree.TreeContainer;
import pl.springui.utils.Profiler;

/**
 * Fixme - zrobic interfejs, brak ekspozycji innych obiektów - możliwość łatwej
 * podminay.
 * 
 * @author dsu
 *
 */
@ConfigurationProperties("springui")
@Component
@Lazy
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UiCtx {

	@Lazy
	@Autowired(required = true)
	private ApplicationContext ctx;

	@Autowired(required = true)
	private ServletWebRequest req;

	@Lazy
	@Autowired
	protected TreeContainer trees;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private int clientIdNoCounter = 0;
	private boolean productionMode = false;
	public boolean isProductionMode() {
		return productionMode;
	}

	public void setProductionMode(boolean productionMode) {
		this.productionMode = productionMode;
	}

	@Profiler
	public <T extends UiComponent> T get(Class<T> type) {
		T bean = ctx.getBean(type);
		return bean;
	}

	public ApplicationContext getCtx() {
		return ctx;
	}

	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public ServletWebRequest getReq() {
		return req;
	}

	public void setReq(ServletWebRequest req) {
		this.req = req;
	}

	public String getNextClientId() {
		clientIdNoCounter++;
		return "ui-" + clientIdNoCounter; // FIXME - coś z nazwa komponentu
	}

	private Tree getTree() {
		return trees.getTree(req.getRequest());
	}

	public void registerUi(String clientId, UiComponent uiComponent) {
		getTree().registerUi(clientId, uiComponent);
	}

	public Collection<UiComponent> getAllComponents() {
		return getTree().getAllComponents();

	}

	/**
	 * Unique key for session id and request parameters
	 * 
	 * @return
	 */
	public Object getSessionRequestHash() {
		Map<String, String[]> parameterMap = req.getParameterMap();
		StringBuilder sb = new StringBuilder();
		SortedSet<String> keys = new TreeSet<String>(parameterMap.keySet());
		for (String key : keys) {
			sb.append(key);
			sb.append("=");
			for (String value : parameterMap.get(key)) {
				sb.append(value);
				sb.append("&");
			}
		}

		String sessionId = req.getSessionId();
		sb.append("sessionId=");
		sb.append(sessionId);

		String path = req.getRequest().getRequestURI();
		sb.append("&path=");
		sb.append(path);

		String h = sb.toString();
		logger.debug("component cache key: {} ", h);
		return h;
	}

	/**
	 * There is at least one request parameter
	 * 
	 * @return
	 */
	public boolean hasRequestParameters() {
		logger.debug("parmeters count: {} ", req.getParameterMap().size());
		return req.getParameterMap().size() > 0;
	}

}
