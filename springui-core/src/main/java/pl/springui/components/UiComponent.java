package pl.springui.components;

import static j2html.TagCreator.span;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.cache.CacheKeyGenerator;
import pl.springui.components.exceptions.AlreadyRegisteredException;
import pl.springui.components.exceptions.InvalidComponentStatus;
import pl.springui.http.UiCtx;
import pl.springui.utils.Profiler;

/**
 * Klasa automatyzuje podstawowe kwestie zwiazane z komponentami. Komponenty liście moga sie chyba
 * obyc bez tej klasy.
 * 
 * Zamiast interfejsów może bedzie mozan dodac adnotaccje dla metod odpowiedzialnych za validacje,
 * renderowanie itd
 * 
 * @author dsu
 *
 */

@Component
@Scope("prototype")
@Lazy
public abstract class UiComponent
    implements JsRenderer, CacheKeyGenerator, Serializable, UiComponentI {

  protected static final String CLIENT_ID_MODEL_KEY = "clientId";
  protected static final String EMPTY_STRING = "";
  private static final String VIEW_ID_MODEL_KEY = "viewId";
  protected boolean apllyRequestApplied = false;
  protected List<UiComponentI> children = new ArrayList<UiComponentI>();
  protected String clientId;
  protected UiCtx ctx;
  /**
   * Javascript to be executed after Ajax rendering
   */
  protected String javaScript = EMPTY_STRING;

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected UiComponentI parent = null;

  protected boolean processApplied = false;

  protected boolean restoreViewApplied = false;
  // FIXME - lazy initialization
  protected Map<String, Object> viewModel = new HashMap<String, Object>();
  protected boolean visible = true;

  @Autowired
  public UiComponent(UiCtx ctx) {
    super();
    this.ctx = ctx;
    assignUniqueClientId(ctx);
  }

  protected void addChild(UiComponentI c) {
    if (c == null) {
      logger.warn("Child component is null!");
      return;
    }

    if (restoreViewApplied) {
      logger.warn(
          "Child was added after restoreView, make sure this phase is execuded for a child {} !",
          c);
    }

    boolean isLazy = c.getClass().isAnnotationPresent(Lazy.class);
    if (isLazy) {
      logger.warn(
          "Child components ({}) schould not be lazy - the will be created on any refernece!",
          c.getClass());
    }

    if (children.contains(c)) {

      logger.debug("All children");
      for (UiComponentI child : children) {
        logger.debug("Child {}", child);
      }

      throw new AlreadyRegisteredException(c.getClientId());
    }

    logger.debug("Registering a child: {}, {}", c.getClientId(), c.getClass().getSimpleName());

    c.setParent(this);
    children.add(c);
    ctx.registerUi(c.getClientId(), c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#addJs(java.lang.String)
   */
  @Override
  public void addJs(String js) {
    if (javaScript != null && javaScript.length() > 0) {
      javaScript = javaScript + ";";
    }
    javaScript = javaScript + js;
  }

  /**
   * Clean after render response
   */
  @Override
  public void afterRenderResponse() {
    viewModel.clear();
    for (UiComponentI c : children) {
      c.afterRenderResponse();
    }

    logger.debug("afterRenderResponse {} , class: {}, annotation: {}", this,
        AopUtils.getTargetClass(this),
        AopUtils.getTargetClass(this).isAnnotationPresent(StatelessComponent.class));

    if (AopUtils.getTargetClass(this).isAnnotationPresent(StatelessComponent.class)) {
      unregisterItself();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#applyRequest()
   */
  @Override
  public void applyRequest() {
    logger.trace("applyRequest of " + this.getClass().getName());
    if (apllyRequestApplied) {
      throw new InvalidComponentStatus("Apply request stage already applied");
    }
    apllyRequestApplied = true;

    for (UiComponentI c : children) {
      logger.trace("applyRequest of a child " + c.getClass().getName());
      c.applyRequest();
    }
  }

  protected void assignUniqueClientId(UiCtx ctx) {
    setClientId(this.getClass().getSimpleName() + "-" + ctx.getNextAvailableClientId());
  }

  @Override
  public void beforeRenderResponse() {
    logger.debug("beforeRenderResponse: Put {} as client id , children : {}", clientId,
        children.size());
    viewModel.put(CLIENT_ID_MODEL_KEY, getClientId());
    viewModel.put(VIEW_ID_MODEL_KEY, ctx.getViewGuid());

    for (UiComponentI c : children) {
      logger.trace("beforeRenderResponse: Child {} as client id", c.getClientId());
      c.beforeRenderResponse();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#clearChildren()
   */
  @Override
  public void clearChildren() {
    for (UiComponentI c : children) {
      c.clearChildren();
      getCtx().unregister(c);
    }
    children = new ArrayList<UiComponentI>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#clearPhases()
   */
  @Override
  public void clearPhases() {
    logger.trace("clearPhases in {}", this.getClientId());
    restoreViewApplied = false;
    apllyRequestApplied = false;
    processApplied = false;
    for (UiComponentI c : children) {
      c.clearPhases();
    }
    javaScript = EMPTY_STRING;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UiComponent other = (UiComponent) obj;
    if (clientId == null) {
      if (other.clientId != null) {
        return false;
      }
    } else if (!clientId.equals(other.clientId)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#executePhases()
   */
  @Override
  public String executePhases() {
    logger.debug("Execute phases of {}", this.getClass().getName());
    restoreView();
    applyRequest();
    process();
    beforeRenderResponse();

    String redirect = redirect();
    if (redirect != null) {
      return "redirect:" + redirect;
    }

    String result = renderResponse();
    afterRenderResponse();
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#getCacheKey()
   */
  @Override
  public Object getCacheKey() {
    return getCtx().getSessionRequestHash();
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#getChildren()
   */
  @Override
  public List<UiComponentI> getChildren() {
    return children;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#getClientId()
   */
  @Override
  public String getClientId() {
    return clientId;
  }

  public UiCtx getCtx() {
    return ctx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#getParent()
   */
  @Override
  public UiComponentI getParent() {
    return parent;
  }

  protected boolean hasAnyRequestParameters() {
    boolean has = getCtx().hasRequestParameters();
    logger.debug("has parmeters: {}", has);
    return has;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#isVisible()
   */
  @Override
  public boolean isVisible() {
    return visible;
  }

  protected void printRequest() {
    logger.trace("===== request =====");
    for (String key : ctx.getReq().getParameterMap().keySet()) {
      logger.trace("{}={}", key, getCtx().getReq().getParameter(key));
    }
    logger.trace("===== ------- =====");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#process()
   */
  @Override
  public void process() {
    logger.trace("process phase of  {}", this.getClass().getSimpleName());
    if (processApplied) {
      throw new InvalidComponentStatus("Process stage already applied for " + getClientId());
    }
    processApplied = true;

    for (UiComponentI c : children) {
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

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#redirect()
   */
  @Override
  public String redirect() {
    for (UiComponentI c : children) {
      logger.trace("redirect stage of a child {}", c.getClass().getSimpleName());
      String childRedirect = c.redirect();
      if (childRedirect != null) {
        logger.debug("Redirecting to  {} by {} component", childRedirect,
            c.getClass().getSimpleName());
        return childRedirect;
      }
    }
    return null;
  }

  protected void removeChild(UiComponentI c) {
    if (c == null) {
      logger.warn("Child component is null!");
      return;
    }
    c.setParent(null);
    children.remove(c);
    ctx.unregister(c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#renderJs()
   */
  @Override
  public String renderJs() {
    return javaScript;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#renderPlaceHolder()
   */
  @Override
  public String renderPlaceHolder() {
    return span().withId(getClientId()).withStyle("display:none;").toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#restoreView()
   */
  @Override
  public void restoreView() {

    logger.trace("restoreView of a " + this.getClass().getName());

    if (restoreViewApplied) {
      logger.warn("Restore View stage already applied for {}, id: {}", this.getClass().getName(),
          this.getClientId());
      throw new InvalidComponentStatus("Restore View stage already applied");
    }

    for (UiComponentI c : children) {
      logger.trace("trestoreView of a child " + c.getClass().getName());
      c.restoreView();
    }
    restoreViewApplied = true;

  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#setClientId(java.lang.String)
   */
  @Override
  @Profiler
  public void setClientId(String clientId) {
    if (clientId == null) {
      throw new IllegalArgumentException("Client id cannot be null!");
    }

    logger.trace("Register a component: {}, {} ", clientId, this.getClass().getSimpleName());
    this.clientId = clientId;

  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#setCtx(pl.springui.http.UiCtx)
   */
  @Override
  public void setCtx(UiCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public void setParent(UiComponentI parent) {
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.UiComponentI#setVisible(boolean)
   */
  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public String toString() {
    String parnetId = parent == null ? "none" : parent.getClientId();
    return "UiComponent [parent=" + parnetId + ", clientId=" + clientId + "]";
  }

  protected void unregisterAnnotatedChildren() {
    for (UiComponentI c : children) {
      if (AopUtils.getTargetClass(c).isAnnotationPresent(StatelessComponent.class)) {
        removeChild(c);
      }
    }
  }

  protected void unregisterItself() {

    for (UiComponentI c : children) {
      if (c.getClass().isAnnotationPresent(StatelessComponent.class)) {
        removeChild(c);
      }
    }
    clearChildren();
    this.setParent(null);
    ctx.unregister(this);
    logger.debug("{} Unregistered", this);
  }

}
