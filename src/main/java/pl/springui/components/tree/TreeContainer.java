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
import org.springframework.web.context.request.ServletWebRequest;

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

	public void clear(HttpServletRequest req) {
		String key = getCurrentViewGuid(req);
		Tree tree = trees.get(key);
		logger.debug("clear tree {}", key);
		if (tree != null) {
			tree.clear();
		}

	}

	public UiComponent getComponent(HttpServletRequest req, String componentIds) {
		String key = getCurrentViewGuid(req);
		Tree tree = trees.get(key);
		logger.trace("Check tree with key: {} = {}", tree, key);

		logger.trace("All trees {}", trees.size());
		for (Entry<String, Tree> t : trees.entrySet()) {
			logger.trace("Existing tree: {}, size: {}", t.getKey(), t.getValue().getAllComponents().size());
		}
		if (tree != null) {
			return tree.getComponent(componentIds);
		} else {
			return null;
		}
	}

	protected String getCurrentViewGuid(HttpServletRequest req) {
		// programatically set for the request
		String viewId = (String) req.getAttribute(VIEWGUID_PARAMETER_NAME);
		if (viewId == null) {
			// key from a ajax request
			return req.getParameter(VIEWGUID_PARAMETER_NAME);
		} else
			return viewId;
	}

	public Tree getTree(HttpServletRequest req) {
		String viewGuid = getCurrentViewGuid(req);
		logger.trace("get tree {}", viewGuid);

		if (viewGuid == null || viewGuid.length() < MIN_VIEW_KEY_LENGTH) {
			viewGuid = java.util.UUID.randomUUID().toString();
			req.setAttribute(VIEWGUID_PARAMETER_NAME, viewGuid);
			Tree newTree = new Tree(viewGuid);
			trees.put(viewGuid, newTree);
			logger.trace("new tree {} inserted", viewGuid);
			return newTree;
		}

		Tree tree = trees.get(viewGuid);
		if (tree == null) {
			// new tree
			throw new UiViewExpired("View has expired!");
		}
		logger.trace("returing tree from the session");
		return tree;
	}

	public boolean isViewInitialization(ServletWebRequest req) {
		String viewId = req.getParameter(VIEWGUID_PARAMETER_NAME);
		return viewId == null;
	}

}
