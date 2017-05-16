package pl.springui.components.layouts;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.components.resources.JavaScriptStack;
import pl.springui.components.resources.StyleSheetStack;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")

@JavaScriptStack(directory = "/layout/js", value = { "jquery.metisMenu.js", "jquery.slimscroll.min.js",
		"layout.js" }, position = 2)
@JavaScriptStack(directory = "/js", value = { "jquery.js", "bootstrap.min.js", "js.cookie.min.js",
		"ui.js" }, position = 1)
@StyleSheetStack(directory = "/layout/css", value = { "bootstrap.min.css", "animate.css", "style.css" }, position = 1)
@StyleSheetStack(directory = "/font-awesome/css/", value = { "font-awesome.css" }, position = 2)
public class Inspinia extends Layout {

	public Inspinia(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx, engine);
	}

}
