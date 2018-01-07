package pl.springui.components.tree;

import java.io.Serializable;

import pl.springui.components.UiComponentI;

/**
 * Contains all the components of a View Tree
 * 
 * @author dsu
 *
 */
public interface ViewTreeKeeper extends Serializable {

  void clear(String guid);

  /**
   * Needs to be executed only once during page generation
   */
  UiTree createTree(String viewGuid);

  UiComponentI getComponent(String guid, String componentId);

  UiTree getTree(String guid);

}