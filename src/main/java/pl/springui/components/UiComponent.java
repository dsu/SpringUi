package pl.springui.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.exceptions.InvalidComponentStatus;
import pl.springui.http.UiCtx;
import pl.springui.utils.Profiler;

/**
 * Klasa automatyzuje podstawowe kwestie zwiazane z komponentami. Komponenty
 * liście moga sie chyba obyc bez tej klasy.
 * 
 * Zamiast interfejsów może bedzie mozan dodac adnotaccje dla metod
 * odpowiedzialnych za validacje, renderowanie itd
 * 
 * @author dsu
 *
 */

@Component
@Scope("prototype")
@Lazy
public abstract class UiComponent implements HTMLRenderer, Serializable {

	private static final String CLIENT_ID_MODEL_KEY = "clientId";

	protected UiCtx ctx;
	protected List<UiComponent> children = new ArrayList<UiComponent>();
	protected boolean restoreViewApplied = false;
	protected boolean apllyRequestApplied = false;
	protected boolean processApplied = false;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public UiComponent(UiCtx ctx) {
		super();
		this.ctx = ctx;
		assignUniqueClientId(ctx);
	}

	protected void assignUniqueClientId(UiCtx ctx) {
		setClientId(this.getClass().getSimpleName() + "-" + ctx.getNextClientId());
	}

	protected String clientId;

	// FIXME - lazy initialization
	protected Map<String, Object> viewModel = new HashMap<String, Object>();

	protected void writeToViewModel(String key, UiComponent c) {
		if (key != null && c != null) {
			viewModel.put(key, c.renderResponse());
		}
	}

	protected void writeToViewModel(String key, String v) {
		if (key != null && v != null) {
			viewModel.put(key, v);
		}
	}

	protected void putToViewModel(String key, Object o) {
		if (key != null && o != null) {
			viewModel.put(key, o);
		}
	}

	/**
	 * Id in JS/CSS. Naming convention: http://getbem.com/introduction/ Block -
	 * parent component
	 */
	public String getClientId() {
		return clientId;
	}

	public void setCtx(UiCtx ctx) {
		this.ctx = ctx;
	}

	protected void addChild(UiComponent c) {
		if (c == null) {
			return;
		}

		children.add(c);
		logger.trace("Registering a child: {}, {}", c.getClientId(), c.getClass().getSimpleName());
		ctx.registerUi(c.getClientId(), c);
	}

	@Profiler
	public void setClientId(String clientId) {
		if (clientId == null) {
			throw new IllegalArgumentException("Client id cannot be null!");
		}

		logger.trace("Register a component: {}, {} ", clientId, this.getClass().getSimpleName());
		this.clientId = clientId;
		viewModel.put(CLIENT_ID_MODEL_KEY, clientId);
	}

	public UiCtx getCtx() {
		return ctx;
	}

	/**
	 * Application logic
	 */
	public void process() {
		logger.trace("process phase of  {}", this.getClass().getSimpleName());
		if (processApplied) {
			throw new InvalidComponentStatus("Process stage already applied");
		}
		processApplied = true;

		for (UiComponent c : children) {
			logger.trace("process of a child {}", c.getClass().getSimpleName());
			c.process();
		}
	}

	/**
	 * Register children or load children from the tree
	 */
	public void restoreView() {
		logger.trace("restoreView of " + this.getClass().getName());
		if (restoreViewApplied) {
			throw new InvalidComponentStatus("Restore View stage already applied");
		}
		restoreViewApplied = true;

		for (UiComponent c : children) {
			logger.trace("restoreView of a child " + c.getClass().getName());
			c.restoreView();
		}
	}

	public void applyRequest() {
		logger.trace("applyRequest of " + this.getClass().getName());
		if (apllyRequestApplied) {
			throw new InvalidComponentStatus("Apply request stage already applied");
		}
		apllyRequestApplied = true;

		for (UiComponent c : children) {
			logger.trace("applyRequest of a child " + c.getClass().getName());
			c.applyRequest();
		}
	}

	/**
	 * Default cache key for caching the component - can be used with Spring
	 * Cache key generator. Key is based on the request parameters. Session
	 * parameters are not included?
	 * 
	 * @return
	 */
	public Object getCacheKey() {
		return getCtx().getSessionRequestHash();
	}

	public String executePhases() {
		logger.trace("Execute phases of {}", this.getClass().getName());
		restoreView();
		applyRequest();
		process();
		return renderResponse();
	}

	public void clearPhases() {
		restoreViewApplied = false;
		apllyRequestApplied = false;
		processApplied = false;
		for (UiComponent c : children) {
			c.clearPhases();
		}
	}

}
