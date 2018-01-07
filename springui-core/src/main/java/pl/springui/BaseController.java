package pl.springui;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.springui.components.exceptions.UiViewExpired;
import pl.springui.components.utils.ViewModelTracer;
import pl.springui.http.AjaxProcessor;
import pl.springui.http.PageProcessor;
import pl.springui.http.SimplePageProcessor;
import pl.springui.http.UiAjaxRequest;

@RestController
public class BaseController {

	@Lazy
	@Autowired
	AjaxProcessor ajaxProcessor;

	@Autowired
	ServletContext context;

	@Autowired
	ApplicationContext ctx;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Lazy
	@Autowired
	PageProcessor pageProcessor;

	@Lazy
	@Autowired
	ViewModelTracer tracer;

	/**
	 * Na podstawie drzewa z sesji odświeża/przetwarza wybrane komponenty TODO -
	 * zrobić na podstawie jsona
	 * 
	 * @param request
	 * @param response
	 * @param componentIds
	 * @throws IOException
	 */
	@RequestMapping("/ajax")
	void ajax(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("ids[]") Optional<String> componentIds, @RequestParam("actions[]") Optional<String> actions,
			@RequestParam("listeners[]") Optional<String> listeners) throws IOException {
		try {

			UiAjaxRequest ajaxRequest = UiAjaxRequest.getInstance(request, response);
			ajaxRequest.setActions(actions);
			ajaxRequest.setListeners(listeners);
			ajaxRequest.setIds(componentIds);

			ajaxProcessor.process(ajaxRequest);
		} catch (UiViewExpired expiredEx) {
			logger.warn("Ajax processor UiViewExpired exception", expiredEx.getMessage());
			response.setStatus(418);
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
		}
	}

	@GetMapping("/500")
	public void show500(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("500 error page");
		throw new RuntimeException("A 500 error!");
	}

	@RequestMapping("/springui")
	public void traceModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		new SimplePageProcessor().process(request, response, tracer);
	}

}
