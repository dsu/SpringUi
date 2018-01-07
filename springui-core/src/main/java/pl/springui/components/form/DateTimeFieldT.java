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
public class DateTimeFieldT extends AbstractInputField {

	protected MapTemplateEngine engine;

	@Autowired
	public DateTimeFieldT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	protected String getTemplatePath() {
		return "components/fields/datetime.xhtml";
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

		logger.debug("render datetime field {}  with a message {} , viewModel message: {} , id: {}", getLabel(),
				getMessage(), viewModel.get("message"), getClientId());
		String r = engine.procesTemplateAsString(viewModel, getTemplatePath());
		return r;
	}

}
