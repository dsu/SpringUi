package pl.springui.components.layouts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
public class BootstrapGrid extends UiComponent {

	protected MapTemplateEngine engine;
	protected List<List<UiComponent>> grid = new ArrayList<>();

	@Autowired
	public BootstrapGrid(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		putToViewModel("grid", grid);
		// List<List<String>> renderedGrid = new ArrayList<>(); //ew. zamian
		// tutaj wszystkiego
		return engine.procesTemplateAsString(viewModel, "components/bootstrap-grid.xhtml");
	}


	public void add(UiComponent comp) {
		add(comp, 0);
	}

	public void add(UiComponent comp, int rowNr) {

		while (grid.size() <= rowNr) {
			grid.add(new ArrayList<>());
		}

		List<UiComponent> row = grid.get(rowNr);
		if (row == null) {
			row = new ArrayList<>();
			grid.add(rowNr, row);
		}

		logger.trace("Add item to row : {} as {}", rowNr, grid.get(rowNr).size());

		row.add(comp);
		addChild(comp);
	}

}
