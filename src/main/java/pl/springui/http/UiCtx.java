package pl.springui.http;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

import pl.springui.components.UiComponent;
import pl.springui.components.tree.Tree;
import pl.springui.components.tree.TreeContainer;

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

	private ServletWebRequest req;

	protected TreeContainer trees;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private int clientIdNoCounter = 0;
	private boolean productionMode = false;

	public UiCtx(@Autowired ServletWebRequest req, @Autowired TreeContainer trees) {
		super();
		this.req = req;
		this.trees = trees;
	}

	public void clearTree() {
		trees.getTree(req.getRequest()).clear();
	}

	public Collection<UiComponent> getAllComponents() {
		return getTree().getAllComponents();

	}

	public UiComponent getComponent(String componentId) {
		return trees.getComponent(req.getRequest(), componentId);
	}

	public String getCurrentUri() {
		return getReq().getRequest().getRequestURI();
	}

	public String getNextClientId() {
		clientIdNoCounter++;
		return "ui-" + clientIdNoCounter; // FIXME - coś z nazwa komponentu
	}

	public ServletWebRequest getReq() {
		return req;
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

	private Tree getTree() {
		return trees.getTree(req.getRequest());
	}

	public String getViewGuid() {
		return trees.getTree(req.getRequest()).getKey();
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

	public boolean isProductionMode() {
		return productionMode;
	}

	public boolean isViewInitialization() {
		return trees.isViewInitialization(req);
	}

	public void registerUi(String clientId, UiComponent uiComponent) {
		getTree().registerUi(clientId, uiComponent);
	}

	public void setProductionMode(boolean productionMode) {
		this.productionMode = productionMode;
	}

	public void setReq(ServletWebRequest req) {
		this.req = req;
	}

}
