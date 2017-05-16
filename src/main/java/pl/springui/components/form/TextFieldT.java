package pl.springui.components.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

@Component
@Scope("prototype")
@EqualsAndHashCode
public class TextFieldT extends AbstractInputField {

	protected MapTemplateEngine engine;

	@Autowired
	public TextFieldT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Override
	public String renderResponse() {
		putToViewModel("label", label);
		putToViewModel("value", value);
		return engine.procesTemplateAsString(viewModel, "components/input.xhtml");
	}

}
