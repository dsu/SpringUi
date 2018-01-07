package pl.springui.example.components.layouts;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.components.UiComponentI;
import pl.springui.components.resources.JavaScriptStack;
import pl.springui.components.resources.StyleSheetStack;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Component
@Primary
@Scope("prototype")
@JavaScriptStack(directory = "/js", value = { "jquery-3.2.1.min.js" }, position = 1)
@JavaScriptStack(directory = "/js", value = { "js.cookie.min.js", "ui.js" }, position = 2)
@JavaScriptStack(directory = "/bootstrap3/js", value = { "bootstrap.min.js" }, position = 3)
@StyleSheetStack(directory = "/bootstrap3/css", value = { "bootstrap.min.css",
		"bootstrap-theme.min.css" }, position = 1)
public class Bootstrap3Layout extends UiComponent {

	protected UiComponent content;

	protected MapTemplateEngine engine;
	protected UiComponent fotter;
	protected UiComponent headerCss;
	protected UiComponent headerJs;
	protected UiComponent menu;

	public Bootstrap3Layout(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	public UiComponentI getContent() {
		return content;
	}

	public UiComponentI getFotter() {
		return fotter;
	}

	public UiComponentI getHeaderCss() {
		return headerCss;
	}

	public UiComponentI getHeaderJs() {
		return headerJs;
	}

	public UiComponentI getMenu() {
		return menu;
	}

	@Profiler
	@Override
	public String renderResponse() {

		writeToViewModel("content", content);
		writeToViewModel("fotter", fotter);
		writeToViewModel("menu", menu);
		writeToViewModel("header_js", headerJs);
		writeToViewModel("header_css", headerCss);
		return engine.procesTemplateAsString(viewModel, "example/layout.xhtml");
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

	public void setContent(UiComponent content) {
		this.content = content;
	}

	public void setFotter(UiComponent fotter) {
		this.fotter = fotter;
	}

	public void setHeaderCss(UiComponent headerCss) {
		this.headerCss = headerCss;
	}

	public void setHeaderJs(UiComponent headerJs) {
		this.headerJs = headerJs;
	}

	public void setMenu(UiComponent menu) {
		this.menu = menu;
	}

	protected void writeToViewModel(String key, UiComponent c) {

		if (c == null || key == null) {
			return;
		}
		viewModel.put(key, c.renderResponse());

	}

}
