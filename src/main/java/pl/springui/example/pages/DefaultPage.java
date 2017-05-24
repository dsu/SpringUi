package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;

import pl.springui.components.UiComponent;
import pl.springui.components.layouts.Inspinia;
import pl.springui.components.layouts.VerticalMenu;
import pl.springui.components.resources.Scripts;
import pl.springui.components.resources.Styles;
import pl.springui.http.UiCtx;

public class DefaultPage extends UiComponent {

	@Autowired
	Scripts scripts;

	@Autowired
	Styles styles;

	@Autowired
	Inspinia layout;

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

		menu.addItem("Home", "fa-files-o", "#");
		menu.addItem("Crud", "fa-th-large", "#").addSubMenuItem("Document", "", "/bw/promocje/test")
				.addSubMenuItem("Category", "fa-files-o", "/bw/promocje").addSubMenuItem("CRUD", "fa-files-o", "/crud");

		menu.addItem("404", "fa-th-large", "/gdzie-byli-rodzice");
		menu.addItem("500", "fa-th-large", "/500");

		menu.setImage("/img/logo.png");
		layout.setMenu(menu);
		addChild(layout);

		super.restoreView();
		logger.debug("Restore listPage");
	}

	/**
	 * Caching when there is no request paramters - static pages onlny
	 */
	// @Override
	// @Cacheable(cacheNames = "pages", keyGenerator = "uiComponentKey", unless
	// = "@listPage.hasAnyRequestParameters()")
	// public String executePhases() {
	// return super.executePhases();
	// }

}
