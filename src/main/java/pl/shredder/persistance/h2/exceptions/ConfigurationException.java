package pl.shredder.persistance.h2.exceptions;

public class ConfigurationException extends RuntimeException {

	public ConfigurationException(String string, Exception ex) {
		super(string, ex);
	}

	public ConfigurationException(String string) {
		super(string);
	}

}
