package pl.springui.http;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javassist.NotFoundException;

public class BaseController {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Exception> handleException(Exception e) {
		log.error("> handleException");
		log.error("- Exception: ", e);
		log.error("< handleException");
		return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public String handleNotFoundError(HttpServletRequest req, NotFoundException exception) {

		return "redirect:google.pl?exception=" + exception.getLocalizedMessage();
	}
}
