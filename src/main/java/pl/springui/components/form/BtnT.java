package pl.springui.components.form;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Primary
@Component
@EqualsAndHashCode
@Scope("prototype")
public class BtnT extends UiComponent {

	protected MapTemplateEngine engine;
	protected String label;
	protected String onClick;

	@Autowired
	public BtnT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Override
	@Profiler
	public String renderResponse() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("label", label);
		ctx.put("onclick", onClick);
		StringWriter writer = new StringWriter();
		engine.procesTemplate(ctx, getTemplatePath(), writer);
		return writer.toString();
	}

	public String getTemplatePath() {
		return "components/btn.xhtml";
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

}
