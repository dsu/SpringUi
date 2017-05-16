package pl.springui.components.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.HTMLRenderer;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * TODO - dodac sprawdzanie unikalnosci nazw pol? Dodać wspolny interfejs dla
 * pol typu input. Przy prztwarzaniu requesta czytac komponenty z zapisanego
 * drzewa, czy odtwarzac w kodzie w locie? Inaczej moze byc w przypadku ajaxa -
 * wtedy zapisanie drzewa ma wiekszy sens. Dodac przydzielanie unikalnych id w
 * JS - client id.
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class FormT extends UiComponent {

	protected MapTemplateEngine engine;
	protected List<HTMLRenderer> fields = new ArrayList<HTMLRenderer>();

	@Autowired
	public FormT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	public void add(UiComponent field) {
		addChild(field);
		fields.add(field);
	}

	@Profiler
	@Override
	public String renderResponse() {
		// render fields separately
		ArrayList<String> renderedFields = new ArrayList<String>();
		for (HTMLRenderer c : fields) {
			renderedFields.add(c.renderResponse());
		}
		putToViewModel("fields", renderedFields);
		return engine.procesTemplateAsString(viewModel, "components/form.xhtml");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormT other = (FormT) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		return true;
	}

}
