package pl.springui.components.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

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

	public Tree(String key) {
		this.key = key;
		if (key == null || key.length() < 1) {
			throw new RuntimeException("Empty tree key!");
		}
	}

	public void registerUi(String clientId, UiComponent uiComponent) {
		// FIXME exception gdy duplikat
		tree.put(clientId, uiComponent);
	}

	public Map<String, UiComponent> getTree() {
		return tree;
	}

	public Collection<UiComponent> getAllComponents() {
		return tree.values();
	}

	public UiComponent getComponent(String refreshIds) {
		return tree.get(refreshIds);
	}

	public void clear() {
		tree.clear();
	}

	public String getKey() {
		return key;
	}
}
