package pl.springui.components.exceptions;

public class UiException extends RuntimeException {

	public UiException(String msg, Exception e) {
		super(msg, e);
	}

	public UiException(String msg) {
		super(msg);
	}

	public UiException(Exception e) {
		super(e);
	}

}
