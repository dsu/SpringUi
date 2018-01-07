package pl.springui.components.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@EqualsAndHashCode
@Scope("prototype")
public class SubmitBtnT extends BtnT {

	@Autowired
	public SubmitBtnT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx, engine);
	}

	@Profiler
	@Override
	public String getTemplatePath() {
		return "components/submit.xhtml";
	}

}
