package pl.springui.example.components.layouts;

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
public class BootstrapMenu extends UiComponent {

	public class MenuItem {
		private String href;
		private String icon;
		private String label;
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
	protected String image;

	protected List<MenuItem> menu = new LinkedList<MenuItem>();
	
	@Autowired
	public BootstrapMenu(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
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
		return engine.procesTemplateAsString(viewModel, "components/inspinia-menu.xhtml");
	}

	public void setImage(String image) {
		// TODO jakis framework do zarzadzania plikami statycznymi - taki jak w
		// tapestry - np. sprawdzanie czy istnieje
		this.image = image;
	}

	public void setMenu(List<MenuItem> menu) {
		this.menu = menu;
	}

}
