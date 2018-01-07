package pl.springui.http;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import pl.springui.components.exceptions.NotFoundException;

/**
 * Za pomocą @RestControllerAdvice można przechwytywać dowlone exceptiony
 * z @RestController. Jezeli execlption nie jest wywolany z meotdy kontrolera a
 * np. z powodu braku metody pasującej do ścieżki to użytkownik zostanie
 * przekierowany na /error
 * 
 * @author dsu
 *
 */
@RestControllerAdvice
@ControllerAdvice
public class GlobalControllerExceptionHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
			HttpRequestMethodNotSupportedException.class })
	public ResponseEntity<Object> badRequest(HttpServletRequest req, Exception exception) {
		logger.debug("GlobalControllerExceptionHandler: badRequest", exception);
		return null;
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String notFoundException(Exception ex, WebRequest req) {
		logger.debug("GlobalControllerExceptionHandler: notFoundException", ex);
		return "404";
	}

	/*- 
	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String unknownException(Exception ex, WebRequest req) {
		return "500";
	}
	
	
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public String requestHandlingNoHandlerFound(final NoHandlerFoundException ex) {
		logger.debug("342432432");
		return "dsadsadsa";
	}
	*/

}