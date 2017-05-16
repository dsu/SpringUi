package pl.springui.example.components.list;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.list.AbstractList;
import pl.springui.example.model.User;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
public class UserListT extends AbstractList<User> {

	protected MapTemplateEngine engine;

	@Autowired
	public UserListT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		putToViewModel("elements", getCurrentElements());
		return engine.procesTemplateAsString(viewModel, getTemplatePath());
	}

	protected String getTemplatePath() {
		throw new NotImplementedException("This is an example, there is no template");
	}

}
