package pl.springui.components.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import pl.springui.components.HTMLRenderer;
import pl.springui.template.engine.MapTemplateEngine;

/**
 * Show model received by a template to a developer
 * 
 * @author dsu
 *
 */
@Component
@Primary
public class ViewModelTracer implements ViewModelTraceRendered, HTMLRenderer {

	private MapTemplateEngine engine;
	private ViewModelTraces traces;

	@Autowired
	public ViewModelTracer(@Qualifier("thymeleaf") MapTemplateEngine engine, ViewModelTraces traces) {
		this.engine = engine;
		this.traces = traces;
	}

	@Override
	public String renderResponse() {

		if (!traces.isEnabled()) {
			return "Tracking is disabled";
		}

		HashMap<String, Object> viewModel = new HashMap<String, Object>();
		ArrayList list = new ArrayList(traces.getTraces());
		Collections.reverse(list); // from the newest
		viewModel.put("traces", list);
		return engine.procesTemplateAsString(viewModel, "components/view_model_traces.xhtml");

	}

}
