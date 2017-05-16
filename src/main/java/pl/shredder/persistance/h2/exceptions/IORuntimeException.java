
package pl.shredder.persistance.h2.exceptions;

public class IORuntimeException extends RuntimeException {

	public IORuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause);
	}

	public IORuntimeException(Throwable cause) {
		super(cause);
	}

	public IORuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IORuntimeException(String message) {
		super(message);
	}

}
