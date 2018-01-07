package pl.springui.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.springui.components.UiComponentI;

public interface ComponentsProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.http.ComponentsProcessor#process(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * pl.springui.components.UiComponent)
	 */
	void process(HttpServletRequest request, HttpServletResponse response, UiComponentI page);

}