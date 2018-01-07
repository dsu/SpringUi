package pl.springui.example.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
public class SimpleThymeleafLayout extends UiComponent {

	private MapTemplateEngine engine;

	@Autowired
	private CachedFooter footer;

	@Autowired
	public SimpleThymeleafLayout(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		return engine.procesTemplateAsString(viewModel, "example/layout.xhtml");
	}

	@Override
	public void restoreView() {
		addChild(footer);
		super.restoreView();
	}

}
