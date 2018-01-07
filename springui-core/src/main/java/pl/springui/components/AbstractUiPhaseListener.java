package pl.springui.components;

import pl.springui.http.UiCtx;

public class AbstractUiPhaseListener implements UiPhaseListener {

	protected UiCtx ctx;

	public AbstractUiPhaseListener(UiCtx ctx) {
		this.ctx = ctx;
	}
}
