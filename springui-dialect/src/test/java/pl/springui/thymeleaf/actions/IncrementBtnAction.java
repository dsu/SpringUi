package pl.springui.thymeleaf.actions;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.AbstractUiAction;
import pl.springui.http.UiCtx;

@Component("incrementBtn")
@Scope("request")
public class IncrementBtnAction extends AbstractUiAction {

	public IncrementBtnAction(UiCtx ctx) {
		super(ctx);
	}

	@Override
	public void beforeApplyRequest() {

		Integer counter = (Integer) ctx.getAttribute("counter");
		if (counter == null) {
			counter = 1;
			ctx.putAttribute("counter", counter);
		} else {
			counter = counter + 1;
			ctx.putAttribute("counter", counter);
		}

	}

}
