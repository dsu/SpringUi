package pl.springui.components.exceptions;

public class UiException extends RuntimeException {

	public UiException(Exception e) {
		super(e);
	}

	public UiException(String msg) {
		super(msg);
	}

	public UiException(String msg, Exception e) {
		super(msg, e);
	}

}
