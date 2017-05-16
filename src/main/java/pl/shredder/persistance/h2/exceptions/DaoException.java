package pl.shredder.persistance.h2.exceptions;

public class DaoException extends RuntimeException {

	public DaoException(String string, Exception ex) {
		super(string, ex);
	}

	public DaoException(String string) {
		super(string);
	}

}
