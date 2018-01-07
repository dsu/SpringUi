package pl.springui.components;

public interface CSVComponent {

	String getCsvFileName();

	/**
	 * List of records. Each element contains a collection of columns
	 * @return
	 */
	Iterable<?> getRecords();

}
