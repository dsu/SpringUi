package pl.springui.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.springui.utils.Profiler;

public interface AjaxComponentsProcessor {

	void process(HttpServletRequest request, HttpServletResponse response, String componentIds) throws IOException;

}