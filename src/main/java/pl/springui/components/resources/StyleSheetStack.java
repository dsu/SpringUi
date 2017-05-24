package pl.springui.components.resources;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JavaScript files to load before initializing the client-side connector.
 *
 * @return an array of JavaScript file URLs
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(StyleSheetStacks.class)
public @interface StyleSheetStack {

	public String directory() default "";

	public int position() default 100;

	public String[] value() default {};

}
