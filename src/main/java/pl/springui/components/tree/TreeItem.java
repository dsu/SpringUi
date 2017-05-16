package pl.springui.components.tree;

import java.util.Collection;

import pl.springui.components.UiComponent;

@Deprecated
public class TreeItem {

	private UiComponent parent;
	private UiComponent component;
	private Collection<UiComponent> children;

	public UiComponent getParent() {
		return parent;
	}

	public UiComponent getComponent() {
		return component;
	}

	public Collection<UiComponent> getChildren() {
		return children;
	}

	public void setParent(UiComponent parent) {
		this.parent = parent;
	}

	public void setComponent(UiComponent component) {
		this.component = component;
	}

	public void setChildren(Collection<UiComponent> children) {
		this.children = children;
	}

}
