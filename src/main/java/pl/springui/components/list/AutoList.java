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
import pl.springui.components.resources.JavaScriptStack;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * Creates simple paginated list based on getters annotated with @ListColumn
 * annotation.
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
@EqualsAndHashCode
public class AutoList<T> extends AbstractList<T> {

	protected MapTemplateEngine engine;

	/**
	 * because of Java's type erasure Class cannot be infered on runtime
	 */
	private Class listElementType;

	@Autowired
	public AutoList(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	protected List<List<Column>> getRows() {
		List<List<Column>> rows = new ArrayList<>();
		for (T e : getCurrentElements()) {
			rows.add(getColumns(e));
		}

		return rows;
	}

	protected List<Column> getColumns(T element) {
		ArrayList<AutoList<T>.Column> columns = new ArrayList<Column>();

		if (listElementType == null && getCurrentElements().size() > 0) {
			// need to infer a type
			listElementType = getCurrentElements().get(0).getClass();
		}

		if (listElementType == null) {
			return columns;
		} else {

			Method[] methods = listElementType.getMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(ListColumn.class)) {
					ListColumn colMeta = m.getAnnotation(ListColumn.class);
					Object invoke;
					try {
						invoke = m.invoke(element);
						Column column = new Column(colMeta.name(), String.valueOf(invoke), colMeta.position());
						columns.add(column);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new UiException("Exception during acquiring the " + m.getName() + "  value", e);
					}
				}
			}
		}

		Collections.sort(columns);
		return columns;
	}

	@Profiler
	@Override
	public String renderResponse() {
		List<List<Column>> rows = getRows();

		if (rows.size() > 0) {

			putToViewModel("rows", rows);

			for (List<Column> row : rows) {
				logger.trace("row, size: {}, cols: {}", row.size(), row);
			}

			putToViewModel("pager", getPager());
			return engine.procesTemplateAsString(viewModel, getTemplatePath());
		} else {
			putToViewModel("emptyListMessage", getEmptyListMessage());
			return engine.procesTemplateAsString(viewModel, getEmptyTemplatePath());
		}

	}

	protected String getTemplatePath() {
		return "components/list.xhtml";
	}

	protected String getEmptyTemplatePath() {
		return "components/emptyList.xhtml";
	}

	protected class Column implements Comparable<Column> {
		public String name;
		public String value;
		public int position = 0;

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

}
