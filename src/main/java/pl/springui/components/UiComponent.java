package pl.springui.components;

import static j2html.TagCreator.span;

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

import pl.springui.components.exceptions.AlreadyRegisteredException;
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
public abstract class UiComponent implements HTMLRenderer, JsRenderer, Serializable {

	protected static final String CLIENT_ID_MODEL_KEY = "clientId";
	protected static final String EMPTY_STRING = "";
	protected UiCtx ctx;
	protected List<UiComponent> children = new ArrayList<UiComponent>();
	protected boolean restoreViewApplied = false;
	protected boolean apllyRequestApplied = false;
	protected boolean processApplied = false;
	protected boolean visible = true;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String clientId;

	// FIXME - lazy initialization
	protected Map<String, Object> viewModel = new HashMap<String, Object>();

	@Autowired
	public UiComponent(UiCtx ctx) {
		super();
		this.ctx = ctx;
		assignUniqueClientId(ctx);
	}

	protected void addChild(UiComponent c) {
		if (c == null) {
			return;
		}

		if (children.contains(c)) {
			throw new AlreadyRegisteredException(c.getClientId());
		}

		children.add(c);
		logger.trace("Registering a child: {}, {}", c.getClientId(), c.getClass().getSimpleName());
		ctx.registerUi(c.getClientId(), c);
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

	protected void assignUniqueClientId(UiCtx ctx) {
		setClientId(this.getClass().getSimpleName() + "-" + ctx.getNextClientId());
	}

	public void clearPhases() {
		logger.trace("clearPhases in {}", this.getClientId());
		restoreViewApplied = false;
		apllyRequestApplied = false;
		processApplied = false;
		for (UiComponent c : children) {
			c.clearPhases();
		}
	}

	public String executeAjaxPhases() {
		logger.debug("Execute ajax phases of {}", this.getClass().getName());
		applyRequest();
		process();
		beforeRenderResponse();
		String result = renderResponse();
		afterRenderResponse();
		return result;
	}

	/**
	 * Clean after render response
	 */
	protected void afterRenderResponse() {
		viewModel.clear();
		for (UiComponent c : children) {
			c.afterRenderResponse();
		}
	}

	protected void beforeRenderResponse() {
		logger.debug("Put {} as client id", clientId);
		viewModel.put(CLIENT_ID_MODEL_KEY, clientId);

		for (UiComponent c : children) {
			c.beforeRenderResponse();
		}
	}

	public String executePhases() {
		logger.debug("Execute phases of {}", this.getClass().getName());
		restoreView();
		applyRequest();
		process();
		beforeRenderResponse();
		String result = renderResponse();
		afterRenderResponse();
		return result;
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

	/**
	 * Id in JS/CSS. Naming convention: http://getbem.com/introduction/ Block -
	 * parent component
	 */
	public String getClientId() {
		return clientId;
	}

	public UiCtx getCtx() {
		return ctx;
	}

	protected boolean hasAnyRequestParameters() {
		boolean has = getCtx().hasRequestParameters();
		logger.debug("has parmeters: {}", has);
		return has;
	}

	public boolean isVisible() {
		return visible;
	}

	protected void printRequest() {
		logger.debug("===== request =====");
		for (String key : ctx.getReq().getParameterMap().keySet()) {
			logger.debug("{}={}", key, getCtx().getReq().getParameter(key));
		}
		logger.debug("===== ------- =====");
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

	protected void putStringToViewModel(String key, String v) {
		if (key != null && v != null) {
			viewModel.put(key, v);
		}
	}

	protected void putToViewModel(String key, Object o) {
		if (key != null) {
			viewModel.put(key, o);
		}
	}

	@Override
	public String renderJs() {
		return EMPTY_STRING;
	}

	public String renderPlaceHolder() {
		return span().withId(getClientId()).withStyle("display:none;").toString();
	}

	/**
	 * Register children or load children from the tree
	 */
	public void restoreView() {

		logger.trace("restoreView of a " + this.getClass().getName());

		if (restoreViewApplied) {
			logger.warn("Restore View stage already applied for {}, id: {}", this.getClass().getName(),
					this.getClientId());
			throw new InvalidComponentStatus("Restore View stage already applied");
		}

		restoreViewApplied = true;

		for (UiComponent c : children) {
			logger.trace("\trestoreView of a child " + c.getClass().getName());
			c.restoreView();
		}
	}

	@Profiler
	public void setClientId(String clientId) {
		if (clientId == null) {
			throw new IllegalArgumentException("Client id cannot be null!");
		}

		logger.trace("Register a component: {}, {} ", clientId, this.getClass().getSimpleName());
		this.clientId = clientId;

	}

	public void setCtx(UiCtx ctx) {
		this.ctx = ctx;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
