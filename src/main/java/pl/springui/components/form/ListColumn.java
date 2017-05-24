package pl.springui.components.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates a DTO field to appear on AutoList
 * @author dsu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ListColumn {
	public String name();

	public int position();
}
