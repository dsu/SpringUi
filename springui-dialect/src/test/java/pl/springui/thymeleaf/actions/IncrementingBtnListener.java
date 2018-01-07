package pl.springui.thymeleaf.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.AbstractUiPhaseListener;
import pl.springui.components.UiComponentI;
import pl.springui.components.form.BtnT;
import pl.springui.http.UiCtx;

@Component("btnListener")
@Scope("request")
public class IncrementingBtnListener extends AbstractUiPhaseListener {

	public IncrementingBtnListener(@Autowired UiCtx ctx) {
		super(ctx);
	}

	@Override
	public void beforeProcess(UiComponentI c) {
		BtnT btn = (BtnT) c;
		if (btn == null) {
			throw new RuntimeException("Component doesn't exists");
		}
		Integer counter = (Integer) ctx.getAttribute("counter");
		btn.setLabel("Click no. " + counter + " on this page");
	}

}
