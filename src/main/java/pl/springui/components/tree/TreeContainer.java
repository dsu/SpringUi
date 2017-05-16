package pl.springui.components.tree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.UiComponent;
import pl.springui.components.exceptions.UiViewExpired;

/**
 * Contains all components for a session. This class is not thread safe! Add
 * components in a single thread.
 * 
 * @author dsu
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TreeContainer {

	private static final String VIEWGUID_PARAMETER_NAME = "viewguid";
	private static final int MIN_VIEW_KEY_LENGTH = 12;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final int MAX_VIEW_PER_SESSION = 15;

	// FIXME - zmiana na cache ? Nie usuwaj starego wpisu jezeli uzywany.

	private Map<String, Tree> trees = new LinkedHashMap<String, Tree>(MAX_VIEW_PER_SESSION) {
		@Override
		protected boolean removeEldestEntry(Entry<String, Tree> entry) {
			boolean remove = size() > MAX_VIEW_PER_SESSION;
			if (remove) {
				logger.debug("remove eldest entry from the tree container when adding {}", entry.getKey());
			}
			return remove;
		}
	};

	public UiComponent getComponent(HttpServletRequest req, String componentIds) {
		String key = getCurrentViewGuid(req);
		Tree tree = trees.get(key);
		logger.debug("Check tree with key: {} = {}", tree, key);

		logger.debug("All trees {}", trees.size());
		for (Entry<String, Tree> t : trees.entrySet()) {
			logger.debug("Existing tree: {}, size: {}", t.getKey(), t.getValue().getAllComponents().size());
		}
		if (tree != null) {
			return tree.getComponent(componentIds);
		} else {
			return null;
		}
	}

	public void clear(HttpServletRequest req) {
		String key = getCurrentViewGuid(req);
		Tree tree = trees.get(key);
		logger.debug("clear tree {}", key);
		if (tree != null) {
			tree.clear();
		}

	}

	public Tree getTree(HttpServletRequest req) {
		String key = getCurrentViewGuid(req);
		logger.debug("get tree {}", key);

		if (key == null || key.length() < MIN_VIEW_KEY_LENGTH) {
			key = java.util.UUID.randomUUID().toString();
			req.setAttribute(VIEWGUID_PARAMETER_NAME, key);
			Tree newTree = new Tree(key);
			trees.put(key, newTree);
			logger.debug("new tree {} inserted", key);
			return newTree;
		}

		Tree tree = trees.get(key);
		if (tree == null) {
			// new tree
			throw new UiViewExpired("View has expired!");
		}
		logger.debug("returing tree from the session");
		return tree;
	}

	protected String getCurrentViewGuid(HttpServletRequest req) {
		String viewId = req.getParameter(VIEWGUID_PARAMETER_NAME);
		if (viewId == null) {
			return (String) req.getAttribute(VIEWGUID_PARAMETER_NAME);
		} else
			return viewId;
	}

}
