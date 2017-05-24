package pl.springui.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.UiComponent;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.utils.Profiler;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AjaxProcessor implements AjaxComponentsProcessor {

	private static final String COMPONENTS_SEPARATOR = ",";

	@Autowired
	protected UiCtx ctx;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.http.AjaxComponentsProcessor#process(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * java.lang.String)
	 */
	@Override
	@Profiler
	public void process(HttpServletRequest request, HttpServletResponse response, String ids) throws IOException {
		logger.debug("processAjax of {}", ids);
		setJsonHeaders(response);
		String[] conponentsIds = ids.split(COMPONENTS_SEPARATOR);
		List<UiComponent> components = new ArrayList<>();
		if (conponentsIds.length > 0) {
			for (String componentId : conponentsIds) {
				if (componentId.length() > 0) {
					logger.debug("processAjax of a component {}", componentId);
					UiComponent c = ctx.getComponent(componentId);
					if (c == null) {
						throw new RuntimeException("Component " + componentId + "doesn't exists!");
					}
					components.add(c);
				}
			}
		}

		try {
			writeComponentAjaxResponse(request, response, components);
		} catch (Exception e) {
			if (ctx.isProductionMode()) {
				throw new UserVisibleError("500");
			} else {
				throw new UiException(e);
			}
		}

	}

	protected void setJsonHeaders(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

	protected void writeComponentAjaxResponse(HttpServletRequest request, HttpServletResponse response,
			List<UiComponent> components) throws IOException, JSONException {
		long currentTimeMillis = System.currentTimeMillis();

		JSONArray componetsArray = new JSONArray();
		for (UiComponent c : components) {
			c.clearPhases();
			String result = c.executeAjaxPhases();
			JSONObject componetResponse = new JSONObject();
			componetResponse.put("html", result);
			componetResponse.put("js", ""); // TODO zamiast zwracania stringa
											// zwraca obiekt?
			componetResponse.put("ids", c.getClientId());
			componetsArray.put(componetResponse);
		}

		logger.debug("Json response: {}", componetsArray);
		response.getWriter().write(componetsArray.toString());
		response.getWriter().close();
		logger.debug("ts: {}", (System.currentTimeMillis() - currentTimeMillis));
	}

}
