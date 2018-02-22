package pl.springui.components.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * To help generic DAO classes to choose proper filtering query for a field
 *
 * @author dsu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FormInput {

  public int column() default 0;

  String name() default "";

  public int row() default 0;

  public String[] options() default "";

  InputType type() default InputType.TEXT;

  String label() default "";

  /**
   * Role that user need to have in order to be able to edit this field
   * 
   * @return
   */
  public String securityRole() default "";

}
