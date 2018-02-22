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
public class SelectFieldT extends AbstractInputField {

	protected MapTemplateEngine engine;

	private String[] options;

	@Autowired
	public SelectFieldT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Override
	public String renderResponse() {
		if (!isVisible()) {
			return renderPlaceHolder();
		}

		putToViewModel("label", getLabel());
		putToViewModel("value", getValue());
		putToViewModel("name", getName());
		putToViewModel("message", getMessage());
		putToViewModel("options", getOptions());

		logger.debug("render select field {}  with a message {} , viewModel message: {}, options : {}", getLabel(),
				getMessage(), viewModel.get("message"), getOptions());
		String r = engine.procesTemplateAsString(viewModel, getTemplatePath());
		return r;
	}

	protected String getTemplatePath() {
		return "components/fields/select.xhtml";
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

}
