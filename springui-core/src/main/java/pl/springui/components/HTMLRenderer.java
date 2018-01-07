package pl.springui.components;

public interface HTMLRenderer {

  default void afterRenderResponse() {
  }

  default void beforeRenderResponse() {
  };

  String renderResponse();

}
