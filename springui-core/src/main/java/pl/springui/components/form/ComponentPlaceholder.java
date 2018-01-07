package pl.springui.components.form;

import static j2html.TagCreator.span;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
@EqualsAndHashCode
public class ComponentPlaceholder extends UiComponent {

	public ComponentPlaceholder(UiCtx ctx, String clientId) {
		super(ctx);
		setClientId(clientId);
	}

	@Override
	public String renderResponse() {
		return span().withId(clientId).toString();
	}

}
