package pl.springui.components.tree;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.springui.components.UiComponentI;

/**
 * Contains all the components for a single view
 * 
 * @author dsu
 *
 */
public class UiTree {

	protected static final Logger logger = LoggerFactory.getLogger(UiTree.class);
	/**
	 * Custom view scoped attributes.
	 */
	protected Map<String, Object> attributes = new HashMap<>();
	private final String key;
	protected long lastAccess = new Date().getTime();
	private Map<String, UiComponentI> tree = new HashMap<>();

	public UiTree(String key) {
		this.key = key;
		if (key == null || key.length() < 1) {
			throw new RuntimeException("Empty tree key!");
		}
	}

	public void clear() {
		logger.debug("clearing the tree");
		tree.clear();
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
		UiTree other = (UiTree) obj;
		if (key == null) {
			if (other.key != null) {
        return false;
      }
		} else if (!key.equals(other.key)) {
      return false;
    }
		return true;
	}

	public Collection<UiComponentI> getAllComponents() {
		return tree.values();
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public UiComponentI getComponent(String componentId) {
		return tree.get(componentId);
	}

	public String getKey() {
		return key;
	}

	public Map<String, UiComponentI> getTree() {
		return tree;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	public Object putAttribute(String key, Object value) {
		return attributes.put(key, value);
	}

	public void registerUi(String clientId, UiComponentI uiComponent) {
		// FIXME exception gdy duplikat
		if (tree.containsKey(clientId)) {
			logger.warn("{} component already in the tree", clientId);
		}

		tree.put(clientId, uiComponent);
	}

	public boolean remove(String clientId) {
		if (clientId != null) {
			boolean ok = tree.remove(clientId) != null;
			logger.debug("{} , removed from the tree result: {}, contains: {}", clientId, ok,
					tree.containsKey(clientId));
			return ok;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Tree [key=" + key + ", lastAccess=" + lastAccess + " children=" + tree.size() + "]";
	}

}
