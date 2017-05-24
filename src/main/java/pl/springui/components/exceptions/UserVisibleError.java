package pl.springui.components.exceptions;

/**
 * Error that should cause displaying 404/500/etc error page for the user
 * 
 * @author dsu
 *
 */
public class UserVisibleError extends UiException {

	public UserVisibleError(String msg) {
		super(msg);
	}

}
