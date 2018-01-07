package pl.springui.components.form;

import static j2html.TagCreator.div;
import static j2html.TagCreator.input;
import static j2html.TagCreator.label;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
@EqualsAndHashCode
public class TextFieldJ extends AbstractInputField {

	@Autowired
	public TextFieldJ(UiCtx ctx) {
		super(ctx);
	}

	@Override
	public String renderResponse() {
		return div().with(label().withText(label)).with(input().attr("type", "text").withValue(value)).toString();
	}

}
