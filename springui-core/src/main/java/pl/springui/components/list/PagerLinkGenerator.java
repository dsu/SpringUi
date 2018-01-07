package pl.springui.components.list;

/**
 * Helper classes that helps to generate URI's in a pager
 * 
 * @author dsu
 *
 * @param <T>
 */
public interface PagerLinkGenerator<T> {

  String generate(AbstractList<T> list, int nr);

}
