package pl.springui.example.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.UiComponent;
import pl.springui.components.tree.IgnoreDanglingComponent;
import pl.springui.example.service.DocumentService;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

@IgnoreDanglingComponent
@Component
@Scope(value = "prototype", proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
@EqualsAndHashCode
public class CachedFooter extends UiComponent {

	protected MapTemplateEngine engine;

	@Autowired
	DocumentService service;

	@Autowired
	public CachedFooter(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Cacheable(cacheNames = "stopka", keyGenerator = "uiComponentKey")
	@Override
	public String renderResponse() {
		viewModel.put("columns", service.getAll());
		return engine.procesTemplateAsString(viewModel, "examples/footer.html");
	}

	@Override
	public void restoreView() {
		super.restoreView();
	}
}
