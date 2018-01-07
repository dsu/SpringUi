package pl.springui.example.components.layouts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
public class BootstrapGridXsl extends BootstrapGrid {

	@Autowired
	public BootstrapGridXsl(UiCtx ctx, @Qualifier("xslEngine") MapTemplateEngine engine) {
		super(ctx, engine);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		putToViewModel("grid", grid);
		return engine.procesTemplateAsString(viewModel, "components/grid.xsl");
	}

}
