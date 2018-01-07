package pl.springui.components.form;

public interface CRUDService<T> {

	Message delete(T formBean);

	T getNew();

	T read(int id);

	Message save(T formBean);

}
