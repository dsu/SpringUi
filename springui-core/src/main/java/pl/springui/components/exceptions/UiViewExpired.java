package pl.springui.components.exceptions;

/**
 * 
 * View tree has been already evicted
 * 
 * @author dsu
 *
 */
public class UiViewExpired extends UiException {

  public UiViewExpired(String msg) {
    super(msg);
  }

}
