package pl.springui.components.tree;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.UiConfig;
import pl.springui.components.UiComponentI;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UiViewExpired;

/**
 * Assumes that the GUID cannot be guessed nor shared per different sessions
 * 
 * @author dsu
 *
 */
@Component
@Primary
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CaheViewTreeKeeper implements ViewTreeKeeper {

  private static final String CACHE_NAME = "trees";
  protected static final Logger logger = LoggerFactory.getLogger(CaheViewTreeKeeper.class);
  
  @Autowired
  private CacheManager cacheManager;
  /**
   * A number to be sure this guid was created in this session
   */
  private final long seed = new Date().getTime();

  private int viewCreated = 0;

  private String addSeed(String viewGuid) {
    return viewGuid + seed;
  }

  private void checkForDOSAttack(String viewGuid) {
    if (viewCreated > UiConfig.warningViewCreatedPerSession()) {
      try {
        // slow down user
        logger.warn("User has created too many View Trees {} per single session {} !", viewCreated,
            viewGuid);
        Thread.sleep(200);
      } catch (InterruptedException e) {
        logger.warn("Slepp error", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.tree.TreeContainer#clear(javax.servlet.http. HttpServletRequest)
   */
  @Override
  public synchronized void clear(String viewGuid) {
    viewGuid = addSeed(viewGuid);

    UiTree tree = getValueFromCache(viewGuid);
    logger.debug("clear tree {}", viewGuid);
    if (tree != null) {
      tree.clear();
    }

  }

  @Override
  public synchronized UiTree createTree(String viewGuid) {
    if (viewGuid == null) {
      throw new UiException("View id can't be null");
    }
    viewGuid = addSeed(viewGuid);
    logger.trace("createTree  {}", viewGuid);
    UiTree newTree = new UiTree(viewGuid);
    viewCreated = viewCreated + 1;
    checkForDOSAttack(viewGuid);
    cacheManager.getCache(CACHE_NAME).put(viewGuid, newTree);
    logger.trace("new tree {} inserted", viewGuid);
    return newTree;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.tree.TreeContainer#getComponent(javax.servlet.http
   * .HttpServletRequest, java.lang.String)
   */
  @Override
  public synchronized UiComponentI getComponent(String viewGuid, String componentId) {

    // validateSession(req);

    viewGuid = addSeed(viewGuid);

    UiTree tree = getValueFromCache(viewGuid);
    logger.trace("Check tree with key: {} = {}", tree, viewGuid);

    if (tree != null) {
      return tree.getComponent(componentId);
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.tree.TreeContainer#getTree(javax.servlet.http. HttpServletRequest)
   */
  @Override
  public synchronized UiTree getTree(String viewGuid) {
    viewGuid = addSeed(viewGuid);
    // validateSession(req);
    UiTree tree = getValueFromCache(viewGuid);
    if (tree == null) {
      // new tree ?
      throw new UiViewExpired("View has expired!");
    }
    logger.trace("returing tree from the session");
    return tree;
  }

  private UiTree getValueFromCache(String viewGuid) {
    if (viewGuid == null) {
      throw new UiException("View id musn't be null");
    }

    ValueWrapper valueWrapper = cacheManager.getCache(CACHE_NAME).get(viewGuid);
    if (valueWrapper != null) {
      UiTree tree = (UiTree) valueWrapper.get();
      return tree;
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return "CaheTreeMapContainer [viewCreated=" + viewCreated + ", seed=" + seed + "]";
  }

}
