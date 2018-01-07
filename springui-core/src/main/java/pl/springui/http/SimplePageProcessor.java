package pl.springui.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.HTMLRenderer;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SimplePageProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void process(HttpServletRequest request, HttpServletResponse response, HTMLRenderer page)
			throws IOException {

		setHeaders(response);

		String result = page.renderResponse();
		// checkForARootComponent();
		response.getWriter().write(result);
		response.getWriter().close();
	}

	protected void setHeaders(HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

}
