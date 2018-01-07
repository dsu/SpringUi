package pl.springui.components.tree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.UiConfig;
import pl.springui.components.UiComponentI;
import pl.springui.components.exceptions.UiViewExpired;

/**
 * Keeps all the trees in a session. This implementation is likely to cause RAM problems with many
 * users and long session TTL
 * 
 * @author dsu
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapViewTreeKeeper implements ViewTreeKeeper {

  protected static final Logger logger = LoggerFactory.getLogger(MapViewTreeKeeper.class);

  private final int MAX_VIEW_PER_SESSION = UiConfig.getVewPerSession();

  // FIXME - zmiana na cache ? Nie usuwaj starego wpisu jezeli uzywany.

  private Map<String, UiTree> trees = new LinkedHashMap<String, UiTree>(MAX_VIEW_PER_SESSION) {
    @Override
    protected boolean removeEldestEntry(Entry<String, UiTree> entry) {
      boolean remove = size() > MAX_VIEW_PER_SESSION;
      if (remove) {
        logger.debug("remove eldest entry from the tree container when adding {}", entry.getKey());
      }
      return remove;
    }
  };

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.components.tree.TreeContainer#clear(javax.servlet.http. HttpServletRequest)
   */
  @Override
  public synchronized void clear(String viewGuid) {
    // validateSession(req);
    UiTree tree = trees.get(viewGuid);
    logger.debug("clear tree {}", viewGuid);
    if (tree != null) {
      tree.clear();
    }

  }

  @Override
  public synchronized UiTree createTree(String viewGuid) {
    logger.trace("createTree  {}", viewGuid);
    // create new tree
    UiTree newTree = new UiTree(viewGuid);
    trees.put(viewGuid, newTree);
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
  public synchronized UiComponentI getComponent(String guid, String componentId) {

    // validateSession(req);

    UiTree tree = trees.get(guid);
    logger.trace("Check tree with key: {} = {}", tree, guid);

    logger.trace("All trees {}", trees.size());
    for (Entry<String, UiTree> t : trees.entrySet()) {
      logger.trace("Existing tree: {}, size: {}", t.getKey(),
          t.getValue().getAllComponents().size());
    }
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

    // validateSession(req);

    UiTree tree = trees.get(viewGuid);
    if (tree == null) {
      // new tree
      throw new UiViewExpired("View has expired!");
    }
    logger.trace("returing tree from the session");
    return tree;
  }

}
