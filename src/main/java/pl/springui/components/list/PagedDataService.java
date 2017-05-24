package pl.springui.components.list;

import java.util.List;

/**
 * Implements a class that can provide elements for a list
 * 
 * @author dsu
 *
 * @param <T>
 */
public interface PagedDataService<T> {

	List<T> geElements(int page, int limit);

	int getAllCount();

	List<T> getAllElements();

}