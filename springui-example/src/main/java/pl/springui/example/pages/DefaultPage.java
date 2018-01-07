package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;

import pl.springui.components.ExampleComponent;
import pl.springui.components.UiComponent;
import pl.springui.components.resources.Scripts;
import pl.springui.components.resources.Styles;
import pl.springui.example.components.layouts.AdminLTELayout;
import pl.springui.example.components.layouts.Bootstrap3Layout;
import pl.springui.example.components.layouts.VerticalMenu;
import pl.springui.http.UiCtx;

@ExampleComponent
public class DefaultPage extends UiComponent {

	@Autowired
	Scripts scripts;

	@Autowired
	Styles styles;

	@Autowired
	AdminLTELayout layout;

	@Autowired
	VerticalMenu menu;

	@Autowired
	public DefaultPage(UiCtx ctx) {
		super(ctx);
		logger.debug("New default page");
	}

	@Override
	public String renderResponse() {
		logger.debug("Render layout response:");
		return layout.renderResponse();
	}

	@Override
	public void restoreView() {

		layout.setHeaderJs(scripts);
		layout.setHeaderCss(styles);

		menu.addItem("Crud", "fa-th-large", "/crud").addSubMenuItem("CMS", "", "/cms/test");

		menu.addItem("404", "fa-th-large", "/xyz");
		menu.addItem("500", "fa-th-large", "/500");

		menu.setImage("/images/logo.png");
		layout.setMenu(menu);
		addChild(layout);

		super.restoreView();
		logger.debug("Restore listPage");
	}

}
