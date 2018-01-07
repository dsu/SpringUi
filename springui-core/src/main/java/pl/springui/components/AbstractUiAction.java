package pl.springui.components;

import pl.springui.http.UiCtx;

public class AbstractUiAction implements UiAction {

	protected UiCtx ctx;

	public AbstractUiAction(UiCtx ctx) {
		this.ctx = ctx;
	}

	@Override
	public void beforeApplyRequest() {
	}

	@Override
	public void beforeProcess() {
	}

	@Override
	public void beforeRender() {
	}
}
