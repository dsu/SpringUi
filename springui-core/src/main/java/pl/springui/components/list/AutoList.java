package pl.springui.components.list;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.form.AutoFormT;
import pl.springui.components.form.FormAction;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * Creates simple paginated list based on getters annotated with @ListColumn annotation.
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
@EqualsAndHashCode
public class AutoList<T> extends AbstractList<T> {

  protected class Column implements Comparable<Column> {
    public String name;
    public int position = 0;
    public String value;

    public Column(String name, String value, int position) {
      super();
      this.name = name;
      this.value = value;
      this.position = position;
    }

    @Override
    public int compareTo(Column other) {
      return position - other.position;
    }

    @Override
    public String toString() {
      return "Column [name=" + name + ", value=" + value + ", position=" + position + "]";
    }

  }

  protected class Row {

    public List<Column> columns;

    public String onclick;

    public Row(List<Column> columns) {
      this.columns = columns;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Row other = (Row) obj;
      if (!getOuterType().equals(other.getOuterType())) {
        return false;
      }
      if (columns == null) {
        if (other.columns != null) {
          return false;
        }
      } else if (!columns.equals(other.columns)) {
        return false;
      }
      return true;
    }

    private AutoList getOuterType() {
      return AutoList.this;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((columns == null) ? 0 : columns.hashCode());
      return result;
    }

    @Override
    public String toString() {
      return "Row [onclick=" + onclick + ", columns=" + columns + "]";
    }

  }

  /**
   * A corresponding from
   */
  private AutoFormT<T> form;

  protected MapTemplateEngine engine;

  /**
   * because of Java's type erasure Class cannot be infered on runtime
   */
  protected Class listElementType;

  protected String onSelectExtraParameters;

  /**
   * Elements ids that needs to be refreshed when a list element is selected
   */
  protected String onSelectRefreshIds;

  @Autowired
  public AutoList(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
    super(ctx);
    this.engine = engine;
  }

  protected String getEmptyTemplatePath() {
    return "components/emptyList.xhtml";
  }

  protected String getOnSelectOnclick(String keyValue) {
    return "Ui.load({ids:[\'" + getOnSelectRefreshIds() + "\'],serializeIds: [], params:\'"
        + getForm().getIdParameterName() + "=" + keyValue + "&" + getOnSelectParameters() + "&"
        + getForm().getClientId() + ".action=" + FormAction.read + "\'})";
  }

  public String getOnSelectParameters() {
    if (onSelectExtraParameters == null) {
      return "";
    }
    return onSelectExtraParameters;
  }

  public String getOnSelectRefreshIds() {
    if (onSelectRefreshIds == null) {
      return "";
    }
    return onSelectRefreshIds;
  }

  protected String getTemplatePath() {
    return "components/list.xhtml";
  }

  public AutoFormT<T> getForm() {
    return form;
  }

  public void setForm(AutoFormT<T> form) {
    this.form = form;
  }

  /**
   * Create a row from a object
   * 
   * @param element
   * @return
   */
  protected Row makeRow(T element) {
    ArrayList<Column> columns = new ArrayList<Column>();
    String keyValue = null;
    boolean wasKey = false;

    if (listElementType == null && getCurrentElements().size() > 0) {
      // need to infer a type
      listElementType = getCurrentElements().get(0).getClass();
    }

    Method[] methods = listElementType.getMethods();
    for (Method m : methods) {
      if (m.isAnnotationPresent(ListColumn.class)) {
        ListColumn colMeta = m.getAnnotation(ListColumn.class);
        boolean visible = colMeta.visible();
        boolean isKey = colMeta.key();

        Object invoke;
        try {
          invoke = m.invoke(element);
          if (visible) {
            Column column = new Column(colMeta.name(), String.valueOf(invoke), colMeta.position());
            columns.add(column);
          }
          if (isKey) {

            if (wasKey) {
              throw new UiException(
                  "Key already defined for the list (duplicated key is " + colMeta.name() + ")!");
            }
            wasKey = true;
            keyValue = String.valueOf(invoke);
            logger.debug("Key for a row: {} = {}", colMeta.name(), keyValue);
          }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new UiException("Exception during acquiring the " + m.getName() + "  value", e);
        }

      }
    }

    Collections.sort(columns);
    Row row = new Row(columns);

    if (wasKey) {
      row.onclick = getOnSelectOnclick(keyValue);
    }
    return row;
  }

  @Profiler
  @Override
  public String renderResponse() {

    logger.debug("list render response, OnSelectRefreshIds: {}", getOnSelectRefreshIds());

    List<Row> rows = new ArrayList<>();
    List<T> elements = getCurrentElements();
    for (T e : elements) {
      AutoList<T>.Row row = makeRow(e);
      rows.add(row);
      logger.debug("A list row: {}", row);
    }

    if (rows.size() > 0) {
      putToViewModel("rows", rows);
      putToViewModel("pager", getPager());

      if (viewModel.get(CLIENT_ID_MODEL_KEY) == null) {
        throw new UiException("Client id is null");
      }

      logger.debug("List client id: {}", viewModel.get(CLIENT_ID_MODEL_KEY));

      return engine.procesTemplateAsString(viewModel, getTemplatePath());
    } else {
      putToViewModel("emptyListMessage", getEmptyListMessage());
      return engine.procesTemplateAsString(viewModel, getEmptyTemplatePath());
    }

  }

  public void setOnSelectParameters(String onSelectParameters) {

    this.onSelectExtraParameters = onSelectParameters;
  }

  public void setOnSelectRefreshIds(String onSelectRefreshIds) {

    this.onSelectRefreshIds = onSelectRefreshIds;
  }

}
