package pl.springui;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import pl.springui.components.exceptions.ErrorComponent;
import pl.springui.http.SimplePageProcessor;
import pl.springui.template.engine.Thymeleaf;

/**
 * Basic Controller which is called for unhandled errors
 */
@RestController
public class BaseErrorController implements ErrorController {

	private final static String ERROR_PATH = "/error";

	private static HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null) {
			try {
				return HttpStatus.valueOf(statusCode);
			} catch (Exception ex) {
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	@Autowired
	protected Thymeleaf engine;

	/**
	 * Error Attributes in the Application
	 */
	private ErrorAttributes errorAttributes;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Controller for the Error Controller
	 * 
	 * @param errorAttributes
	 */
	public BaseErrorController(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	/**
	 * Supports other formats like JSON, XML
	 * 
	 * @param request
	 * @return
	 * @throws JSONException
	 */
	@RequestMapping(value = ERROR_PATH)
	@ResponseBody
	public String error(WebRequest request) throws JSONException {
		Map<String, Object> body = errorAttributes.getErrorAttributes(request, true);
		logger.debug("error :" + body);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("error", body.get("status"));
		jsonObject.put("message", String.valueOf(body.get("error")));
		// alert(jsonValue.error + " : " + jsonValue.message);
		return jsonObject.toString();
	}

	/**
	 * Supports the HTML Error View
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = ERROR_PATH, produces = "text/html")
	public void errorHtml(WebRequest request, HttpServletRequest req, HttpServletResponse response) throws IOException {
		Map<String, Object> body = errorAttributes.getErrorAttributes(request, true);
		logger.debug("errorHtml: " + body);
		ErrorComponent component = new ErrorComponent(engine);
		component.setErrorAttributes(body);
		int status = Integer.parseInt(String.valueOf(body.get("status")));
		response.setStatus(status);
		new SimplePageProcessor().process(req, response, component);
	}

	/**
	 * Returns the path of the error page.
	 *
	 * @return the error path
	 */
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}
}