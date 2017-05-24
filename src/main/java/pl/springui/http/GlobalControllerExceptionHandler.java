package pl.springui.http;

import org.springframework.http.HttpStatus;
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
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String notFoundException(Exception ex, WebRequest req) {
		System.out.println("GlobalControllerExceptionHandler: notFoundException");
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
		System.out.println("342432432");
		return "dsadsadsa";
	}
	*/

}