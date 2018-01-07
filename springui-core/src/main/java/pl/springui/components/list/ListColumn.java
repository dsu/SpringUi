package pl.springui.components.list;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a DTO field to appear on AutoList - only getters!
 * 
 * @author dsu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ListColumn {
	public boolean key() default false;

	public String name();

	public int position() default 0;

	public boolean visible() default true;

}
