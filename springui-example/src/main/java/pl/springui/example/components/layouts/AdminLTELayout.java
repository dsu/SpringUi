package pl.springui.example.components.layouts;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.resources.JavaScriptStack;
import pl.springui.components.resources.StyleSheetStack;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

@Component
@Scope("prototype")
@JavaScriptStack(directory = "/js", value = { "jquery-3.2.1.min.js" }, position = 1)
@JavaScriptStack(directory = "/bootstrap3/js", value = { "bootstrap.min.js" }, position = 2)
@JavaScriptStack(directory = "/js", value = { "js.cookie.min.js", "ui.js" }, position = 2)
@JavaScriptStack(directory = "/jquery-slimscroll", value = { "jquery.slimscroll.min.js" }, position = 3)
@JavaScriptStack(directory = "/fastclick", value = { "lib/fastclick.js" }, position = 3)
@JavaScriptStack(directory = "/admin_lte/js", value = { "adminlte.js" }, position = 4)
@JavaScriptStack(directory = "/admin_lte/js", value = { "demo.js" }, position = 5)
@JavaScriptStack(directory = "/toastr", value = { "toastr.min.js" }, position = 5)

@StyleSheetStack(directory = "/bootstrap3/css", value = { "bootstrap.min.css",
		"bootstrap-theme.min.css" }, position = 1)
@StyleSheetStack(directory = "/font-awesome/css", value = { "font-awesome.min.css" }, position = 4)
@StyleSheetStack(directory = "/Ionicons/css", value = { "ionicons.min.css" }, position = 5)
@StyleSheetStack(directory = "/admin_lte/css", value = { "admin_lte.min.css" }, position = 6)
@StyleSheetStack(directory = "/css", value = { "animate.css" }, position = 6)

@StyleSheetStack(directory = "/toastr", value = { "toastr.min.css" }, position = 6)

public class AdminLTELayout extends Bootstrap3Layout {

	public AdminLTELayout(UiCtx ctx, MapTemplateEngine engine) {
		super(ctx, engine);
	}

}
