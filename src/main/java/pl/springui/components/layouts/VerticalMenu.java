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

@Component
@Scope("prototype")
public class VerticalMenu extends UiComponent {

	protected MapTemplateEngine engine;
	protected List<MenuItem> menu = new LinkedList<MenuItem>();
	protected String image;

	@Autowired
	public VerticalMenu(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		putToViewModel("image", image);
		putToViewModel("menu", menu);
		return engine.procesTemplateAsString(viewModel, "components/vertical-menu.xhtml");
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		// TODO jakis framework do zarzadzania plikami statycznymi - taki jak w
		// tapestry - np. sprawdzanie czy istnieje
		this.image = image;
	}

	public List<MenuItem> getMenu() {
		return menu;
	}

	public void addItem(MenuItem item) {
		menu.add(item);
	}

	public MenuItem addItem(String label, String icon, String href) {
		MenuItem i = new MenuItem(label, icon, href);
		menu.add(i);
		return i;
	}

	public void setMenu(List<MenuItem> menu) {
		this.menu = menu;
	}

	public class MenuItem {
		private String label;
		private String icon;
		private String href;
		private List<MenuItem> menu = new LinkedList<MenuItem>();

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

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public List<MenuItem> getMenu() {
			return menu;
		}

		public void setMenu(List<MenuItem> menu) {
			this.menu = menu;
		}

		public MenuItem(String label, String icon, String href) {
			super();
			this.label = label;
			this.icon = icon;
			this.href = href;
		}

	}

}
