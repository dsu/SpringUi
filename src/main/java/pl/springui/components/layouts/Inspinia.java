package pl.springui.components.layouts;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.resources.JavaScriptStack;
import pl.springui.components.resources.StyleSheetStack;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

@Component
@Scope("prototype")

@JavaScriptStack(directory = "/inspinia/js", value = { "jquery.metisMenu.js", "jquery.slimscroll.min.js",
		"inspinia.js" }, position = 3)
@JavaScriptStack(directory = "/js", value = { "jquery-ui-1.10.4.min.js", "bootstrap.min.js", "js.cookie.min.js",
		"ui.js" }, position = 2)
@JavaScriptStack(directory = "/js", value = { "jquery.js" }, position = 1)
@StyleSheetStack(directory = "/inspinia/css", value = { "bootstrap.min.css", "animate.css", "style.css" }, position = 1)
@StyleSheetStack(directory = "/font-awesome/css/", value = { "font-awesome.css" }, position = 2)
public class Inspinia extends Layout {

	public Inspinia(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx, engine);
	}

}
