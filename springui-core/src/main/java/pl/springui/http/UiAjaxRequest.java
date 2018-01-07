package pl.springui.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.UiAction;
import pl.springui.components.UiComponentI;
import pl.springui.components.UiPhaseListener;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UiAjaxRequest {

  protected static final String COMPONENTS_SEPARATOR = ",";
  public static UiAjaxRequest getInstance(HttpServletRequest request,
      HttpServletResponse response) {
    return new UiAjaxRequest(request, response);
  }

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected final HttpServletRequest request;
  protected final HttpServletResponse response;
  protected Optional<String> ids;
  protected Optional<String> actionsIds;
  protected Optional<String> listenersIds;

  public UiAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
    super();
    this.request = request;
    this.response = response;
  }

  public Optional<String> getActions() {
    return actionsIds;
  }

  public Optional<String> getIds() {
    return ids;
  }

  public Optional<String> getListeners() {
    return listenersIds;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public List<UiAction> restoreActions(ApplicationContext appContext) {
    List<UiAction> actions = new ArrayList<>();
    if (actionsIds.isPresent()) {
      String[] actionNames = actionsIds.get().split(COMPONENTS_SEPARATOR);
      for (String name : actionNames) {
        if (name != null && name.length() > 0) {
          UiAction bean = appContext.getBean(name, UiAction.class);
          if (bean != null) {
            actions.add(bean);
          }
        }
      }
    }
    return actions;
  }

  public List<UiComponentI> restoreComponentsFromCtx(UiCtx ctx) {
    if (ids.isPresent()) {
      String[] conponentsIds = ids.get().split(COMPONENTS_SEPARATOR);
      List<UiComponentI> components = new ArrayList<>();
      if (conponentsIds.length > 0) {
        for (String componentId : conponentsIds) {
          componentId = componentId.trim();
          if (componentId.length() > 0) {
            logger.debug("processAjax of a component {}", componentId);

            UiComponentI c = ctx.getComponent(componentId);
            if (c == null) {
              if (logger.isDebugEnabled()) {
                Collection<UiComponentI> allComponents = ctx.getAllComponents();
                logger.debug("Known components: {}", allComponents);
              }
              throw new RuntimeException("Component " + componentId + " doesn't exists!");
            }
            components.add(c);
          }
        }
      }
      return components;
    } else {
      return Collections.emptyList();
    }
  }

  public List<UiPhaseListener> restoreListeners(ApplicationContext appContext) {
    List<UiPhaseListener> listeners = new ArrayList<>();
    if (listenersIds.isPresent()) {
      String[] actionNames = listenersIds.get().split(COMPONENTS_SEPARATOR);
      for (String name : actionNames) {
        if (name != null && name.length() > 0) {
          UiPhaseListener bean = appContext.getBean(name, UiPhaseListener.class);
          if (bean != null) {
            listeners.add(bean);
          }
        }
      }
    }
    return listeners;
  }

  public void setActions(Optional<String> actions) {
    this.actionsIds = actions;
  }

  public void setIds(Optional<String> ids) {
    this.ids = ids;
  }

  public void setListeners(Optional<String> listeners) {
    this.listenersIds = listeners;
  }
}
