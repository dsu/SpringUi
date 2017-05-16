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
@Primary
@Scope("prototype")

@JavaScriptStack(directory = "/js", value = { "jquery.js" }, position = 1)
@JavaScriptStack(directory = "/js", value = { "bootstrap.min.js", "js.cookie.min.js", "ui.js" }, position = 2)
@StyleSheetStack(directory = "/bootstrap3/css", value = { "bootstrap.min.css",
		"bootstrap-theme.min.css" }, position = 1)
@StyleSheetStack(directory = "/font-awesome/css/", value = { "font-awesome.css" }, position = 2)
public class Layout extends UiComponent {

	protected MapTemplateEngine engine;

	protected UiComponent content;
	protected UiComponent fotter;
	protected UiComponent menu;
	protected UiComponent headerJs;
	protected UiComponent headerCss;

	public Layout(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Override
	public void restoreView() {
		addChild(content);
		addChild(fotter);
		addChild(menu);
		addChild(headerJs);
		addChild(headerCss);
		super.restoreView();
	}

	@Profiler
	@Override
	public String renderResponse() {
		writeToViewModel("content", content);
		writeToViewModel("fotter", fotter);
		writeToViewModel("menu", menu);
		writeToViewModel("header_js", headerJs);
		writeToViewModel("header_css", headerCss);
		return engine.procesTemplateAsString(viewModel, "components/layout.xhtml");
	}

	public UiComponent getContent() {
		return content;
	}

	public void setContent(UiComponent content) {
		this.content = content;
	}

	public UiComponent getFotter() {
		return fotter;
	}

	public void setFotter(UiComponent fotter) {
		this.fotter = fotter;
	}

	public UiComponent getMenu() {
		return menu;
	}

	public void setMenu(UiComponent menu) {
		this.menu = menu;
	}

	public UiComponent getHeaderJs() {
		return headerJs;
	}

	public void setHeaderJs(UiComponent headerJs) {
		this.headerJs = headerJs;
	}

	public UiComponent getHeaderCss() {
		return headerCss;
	}

	public void setHeaderCss(UiComponent headerCss) {
		this.headerCss = headerCss;
	}

}
