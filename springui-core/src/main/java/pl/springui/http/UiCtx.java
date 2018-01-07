package pl.springui.http;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

import pl.springui.components.UiComponent;
import pl.springui.components.UiComponentI;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.form.Message;
import pl.springui.components.list.AutoList;
import pl.springui.components.tree.IgnoreDanglingComponent;
import pl.springui.components.tree.UiTree;
import pl.springui.components.tree.ViewTreeKeeper;

/**
 * Fixme - zrobic interfejs, brak ekspozycji innych obiektów - możliwość łatwej podminay.
 * 
 * @author dsu
 *
 */
@ConfigurationProperties("springui")
@Component
@Lazy
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UiCtx {

  private int clientIdNoCounter = 0;

  private Guid guid;

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private List<Message> messages = new ArrayList<Message>();
  private boolean productionMode = false;

  private ServletWebRequest req;

  protected ViewTreeKeeper trees;

  public UiCtx(@Autowired ServletWebRequest req, @Autowired ViewTreeKeeper trees,
      @Autowired Guid guid) {
    super();
    this.guid = guid;
    this.req = req;
    this.trees = trees;
  }

  /**
   * Msg displayed to the user after ajax request;
   * 
   * @param message
   */
  public void addMessage(Message message) {
    logger.debug("Adding new ctx message {}", message);
    messages.add(message);
  }

  public boolean canRestoreView() {
    if (!guid.isValid()) {
      logger.info("GUID {} is not valid", guid);
      return false;
    }

    if (trees.getTree(getViewGuid()) == null) {
      logger.info("GUID {} doesn't exist within container!", guid);
      return false;
    }
    return true;
  }

  public void checkForDanglingComponents() {
    if (!isProductionMode()) {
      Collection<UiComponentI> allComponents = getTree().getAllComponents();
      if (allComponents != null && allComponents.size() > 1) {

        ArrayList<UiComponentI> danglingComponents = new ArrayList<UiComponentI>();
        for (UiComponentI c : allComponents) {
          UiComponentI parent = c.getParent();

          if (parent == null
              && !AopUtils.getTargetClass(c).isAnnotationPresent(IgnoreDanglingComponent.class)) {
            danglingComponents.add(c);
          }
        }

        if (danglingComponents.size() > 0) {
          logger.warn("Dangling components ({}) : {}", danglingComponents.size(),
              danglingComponents);

          logger.warn("All components : {}", allComponents);
          throw new UiException("Invalid componet tree hierarchy");
        }
      }
    } else {
      logger.debug("Empty components tree");
    }
  }

  /**
   * Needs to be executed only once during page generation
   */
  public void createNewTree() {
    String vid = guid.createGuid();
    trees.createTree(vid);
  }

  public boolean existsInTheTree(String clientId) {
    return getTree().getComponent(clientId) != null;
  }

  public Collection<UiComponentI> getAllComponents() {
    return getTree().getAllComponents();
  }

  public Object getAttribute(String key) {
    return getTree().getAttributes().get(key);
  }

  public UiComponentI getComponent(String componentId) {
    return trees.getComponent(guid.getCurrentViewGuid(), componentId);
  }

  public <T extends UiComponentI> T getComponentByClass(Class<T> clazz) {
    for (UiComponentI c : getAllComponents()) {
      if (c != null) {
        if (clazz.equals(c.getClass())) {
          return (T) c;
        }
      }
    }
    // uwaga na klasy typu
    // pl.nc.components.Szuflada$$EnhancerBySpringCGLIB$$39bf640e_2,
    // mozna tez uzyc AopUtils.getTargetClass(c)
    for (UiComponentI c : getAllComponents()) {
      if (c != null) {
        if (clazz.isAssignableFrom(c.getClass())) {
          return (T) c;
        }
      }
    }
    return null;
  }

  public String getCurrentUri() {
    return getReq().getRequest().getRequestURI();
  }

  public List<Message> getMessages() {
    return messages;
  }

  public String getNextAvailableClientId() {
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

  private UiTree getTree() {
    return trees.getTree(guid.getCurrentViewGuid());
  }

  public String getViewGuid() {
    return guid.getCurrentViewGuid();
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

  public Object putAttribute(String key, Object value) {
    return getTree().putAttribute(key, value);
  }

  public void registerUi(String clientId, UiComponentI uiComponent) {
    getTree().registerUi(clientId, uiComponent);
  }

  public String requestToQueryString(String... excludedKeys) {

    StringBuilder sb = new StringBuilder();

    HttpServletRequest request = getReq().getRequest();
    for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
      String s = (String) e.nextElement();

      String key = s;

      if (excludedKeys != null) {
        boolean cont = false;
        for (String exKey : excludedKeys) {
          if (exKey != null && exKey.equalsIgnoreCase(key)) {
            cont = true;
            break;
          }
        }
        if (cont) {
          continue;
        }

      }

      String val = request.getParameter(s);

      if (s == null) {
        continue;
      }

      try {
        key = URLEncoder.encode(key, "UTF-8");
        val = URLEncoder.encode(val, "UTF-8");

        if (sb.length() > 0) {
          sb.append("&");
        }

        sb.append(key);
        sb.append("=");
        sb.append(val);
      } catch (Exception ex) {
        logger.warn("Query parse error ", e);
      }
    }
    return sb.toString();
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public void setProductionMode(boolean productionMode) {
    this.productionMode = productionMode;
  }

  public void setReq(ServletWebRequest req) {
    this.req = req;
  }

  public boolean unregister(UiComponentI c) {
    if (c != null) {
      logger.debug("Unregistering component {}", c);
      return trees.getTree(guid.getCurrentViewGuid()).remove(c.getClientId());
    }
    return false;
  }

  List<UiComponentI> extraProcess = new ArrayList<>();

  public void addToExtraProcess(UiComponentI e) {
    extraProcess.add(e);
  }

  public List<UiComponentI> getExtraProcess() {
    return extraProcess;
  }

  public void setExtraProcess(List<UiComponentI> extraProcess) {
    this.extraProcess = extraProcess;
  }

}
