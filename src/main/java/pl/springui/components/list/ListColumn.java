package pl.springui.components.list;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ListColumn {
	public int position();

	public String name();
}
