package pl.springui.example.service;

import java.util.List;

import pl.springui.example.model.User;

public interface PagedDataService<T> {

	List<T> getAllElements();

	List<T> geElements(int page, int limit);

	int getAllCount();

}