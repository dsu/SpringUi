package pl.springui.components;

@FunctionalInterface
public interface UiCallback<T> {

	public void callback(T caller);
}
