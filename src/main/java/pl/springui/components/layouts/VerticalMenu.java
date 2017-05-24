package pl.springui.components.layouts;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * W inspinii jeżeli menu ma submenu to nie będzie można kliknąć w nie.
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class VerticalMenu extends UiComponent {

	public class MenuItem {
		private String label;
		private String icon;
		private boolean active;
		private String href;
		private List<MenuItem> menu = new LinkedList<MenuItem>();

		public MenuItem(String label, String icon, String href) {
			super();
			this.label = label;
			this.icon = icon;
			this.href = href;
		}

		/**
		 * Add element to this menu sub menu
		 * 
		 * @param label
		 * @param icon
		 * @param href
		 */
		public MenuItem addSubMenuItem(String label, String icon, String href) {
			MenuItem i = new MenuItem(label, icon, href);
			menu.add(i);
			return this;
		}

		public void checkWhenActive(String uri) {
			logger.debug("Check if menu item active {}, uri: {} ", href, uri);
			for (MenuItem i : menu) {
				if (i.href.equals(uri)) {
					i.setActive(true);
					setActive(true);
					logger.debug("is active");
					break;
				}
			}
			if (href.equals(uri)) {
				logger.debug("is active");
				setActive(true);
			}
		}

		public String getHref() {
			return href;
		}

		public String getIcon() {
			return icon;
		}

		public String getLabel() {
			return label;
		}

		public List<MenuItem> getMenu() {
			return menu;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public void setMenu(List<MenuItem> menu) {
			this.menu = menu;
		}

	}
	protected MapTemplateEngine engine;
	protected List<MenuItem> menu = new LinkedList<MenuItem>();

	protected String image;

	@Autowired
	public VerticalMenu(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	public void addItem(MenuItem item) {
		menu.add(item);
	}

	public MenuItem addItem(String label, String icon, String href) {
		MenuItem i = new MenuItem(label, icon, href);
		menu.add(i);
		return i;
	}

	@Override
	public void applyRequest() {

		String uri = ctx.getCurrentUri();
		logger.debug("Restore menu ( {} ) for {}", menu.size(), uri);
		for (MenuItem item : menu) {
			item.checkWhenActive(uri);
		}

		super.applyRequest();
	}

	public String getImage() {
		return image;
	}

	public List<MenuItem> getMenu() {
		return menu;
	}

	@Profiler
	@Override
	public String renderResponse() {
		putToViewModel("image", image);
		putToViewModel("menu", menu);
		return engine.procesTemplateAsString(viewModel, "components/vertical-menu.xhtml");
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setMenu(List<MenuItem> menu) {
		this.menu = menu;
	}

}
