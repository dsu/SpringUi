package pl.springui.example.service;

import java.util.List;

public interface PagedDataService<T> {

	List<T> geElements(int page, int limit);

	int getAllCount();

	List<T> getAllElements();

}