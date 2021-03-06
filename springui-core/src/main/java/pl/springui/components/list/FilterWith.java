package pl.springui.components.list;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * To help generic DAO classes to choose proper filtering query for a field
 *
 * @author dsu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterWith {
	FilterType type() default FilterType.EQUALS;

}
