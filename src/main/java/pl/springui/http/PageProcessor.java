package pl.springui.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.RootComponent;
import pl.springui.components.UiComponent;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.tree.TreeContainer;
import pl.springui.utils.Profiler;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PageProcessor implements ComponentsProcessor {

	private static final String VIEWGUID_COOKIE = "VIEW_GUID";

	@Autowired
	protected TreeContainer trees;

	@Autowired
	protected UiCtx ctx;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.http.ComponentsProcessor#process(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * pl.springui.components.UiComponent)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.http.ComponentsProcessor#process(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * pl.springui.components.UiComponent)
	 */
	@Override
	@Profiler
	public String process(HttpServletRequest request, HttpServletResponse response, UiComponent page) {

		trees.clear(request);

		if (ctx.isProductionMode()) {
			try {
				writeHtmlPage(request, response, page);
			} catch (Exception e) {
				return "redirect:/500.html";
			}
		} else {
			try {
				writeHtmlPage(request, response, page);
			} catch (Exception e) {
				throw new UiException(e);
			}
		}

		return null;

	}

	protected void writeHtmlPage(HttpServletRequest request, HttpServletResponse response, UiComponent page)
			throws IOException {
		long currentTimeMillis = System.currentTimeMillis();
		setHeaders(response);
		String viewGuid = trees.getTree(request).getKey();
		Cookies.set(response, VIEWGUID_COOKIE, viewGuid);
		String result = page.executePhases();
		// checkForARootComponent();
		response.getWriter().write(result);
		response.getWriter().close();
		logger.debug("ts for viewGuid : {}", viewGuid, (System.currentTimeMillis() - currentTimeMillis));
	}

	@Deprecated
	private void checkForARootComponent() {
		if (!ctx.isProductionMode()) {
			boolean hasAnyRootComponent = false;
			for (UiComponent c : ctx.getAllComponents()) {
				if (c instanceof RootComponent) {
					hasAnyRootComponent = true;
					break;
				}
			}
			if (!hasAnyRootComponent) {
				throw new UiException("There is no root component in the View!");
			}
		}
	}

	protected void setHeaders(HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

}
