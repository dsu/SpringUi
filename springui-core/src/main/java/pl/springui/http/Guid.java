package pl.springui.http;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Represents a view tree identificator
 * 
 * @author dsu
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Guid {

  private static final int MIN_LENGTH = 20;
  protected static final String VIEWGUID_COOKIE = "VIEW_GUID";
  static final String VIEWGUID_PARAMETER_NAME = "viewguid";
  protected static final Logger logger = LoggerFactory.getLogger(Guid.class);
  private final HttpServletRequest req;
  private final String sessionHash;

  public Guid(@Autowired HttpServletRequest req) {
    super();
    this.req = req;
    sessionHash = String.valueOf(req.getSession(true).getId().hashCode());
    logger.debug("New session hash : {}", sessionHash);
  }

  public String createGuid() {
    String viewGuid = getCurrentViewGuid();

    if (viewGuid != null) {
      throw new SecurityException("Guid already exists!");
    }
    viewGuid = sessionHash + java.util.UUID.randomUUID().toString();
    req.setAttribute(VIEWGUID_PARAMETER_NAME, viewGuid);
    return viewGuid;
  }

  public String getCurrentViewGuid() {
    // programatically set for the request
    String viewId = (String) req.getAttribute(VIEWGUID_PARAMETER_NAME);
    if (viewId == null) {
      // key from a ajax request
      viewId = req.getParameter(VIEWGUID_PARAMETER_NAME);
    }

    return viewId;

  }

  public boolean isValid() {
    String currentViewGuid = getCurrentViewGuid();
    if (currentViewGuid == null || currentViewGuid.length() < MIN_LENGTH) {
      return false;
    }

    if (!currentViewGuid.startsWith(sessionHash)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "Guid [sessionHash=" + sessionHash + "]";
  }

  public boolean viewGuidExists() {
    return getCurrentViewGuid() != null;
  }

}
