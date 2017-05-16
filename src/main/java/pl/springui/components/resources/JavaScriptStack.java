package pl.springui.components.resources;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(JavaScriptStacks.class)
public @interface JavaScriptStack {

	public String[] value() default {};

	public String directory() default "";

	public int position() default 100;

}