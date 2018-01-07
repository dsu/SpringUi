package pl.springui.thymeleaf.dialect;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

public class CommonRootUiComponent extends UiComponent {

	public CommonRootUiComponent(UiCtx ctx) {
		super(ctx);
	}

	@Override
	public String renderResponse() {
		return null;
	}

}
