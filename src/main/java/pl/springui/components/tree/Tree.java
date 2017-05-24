package pl.springui.components.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.springui.components.UiComponent;

/**
 * Contains all the components for a single view
 * 
 * @author dsu
 *
 */
public class Tree {

	private Map<String, UiComponent> tree = new HashMap<>();
	private final String key;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public Tree(String key) {
		this.key = key;
		if (key == null || key.length() < 1) {
			throw new RuntimeException("Empty tree key!");
		}
	}

	public void clear() {
		logger.debug("clearing the tree");
		tree.clear();
	}

	public Collection<UiComponent> getAllComponents() {
		return tree.values();
	}

	public UiComponent getComponent(String refreshIds) {
		return tree.get(refreshIds);
	}

	public String getKey() {
		return key;
	}

	public Map<String, UiComponent> getTree() {
		return tree;
	}

	public void registerUi(String clientId, UiComponent uiComponent) {
		// FIXME exception gdy duplikat
		tree.put(clientId, uiComponent);
	}
}
