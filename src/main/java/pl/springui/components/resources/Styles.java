package pl.springui.components.resources;

import static j2html.TagCreator.link;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
public class Styles extends HtmlImports {

	public Styles(UiCtx ctx) {
		super(ctx);
	}

	@Override
	public void applyRequest() {

		Collection<UiComponent> allComponents = getCtx().getAllComponents();
		for (UiComponent c : allComponents) {

			if (c.getClass().isAnnotationPresent(StyleSheetStacks.class)) {
				StyleSheetStacks[] annotationsArrayByType = c.getClass().getAnnotationsByType(StyleSheetStacks.class);
				for (StyleSheetStacks jsArray : annotationsArrayByType) {
					for (StyleSheetStack js : jsArray.value()) {
						processAnnotation(js);
					}
				}
			}

			if (c.getClass().isAnnotationPresent(StyleSheetStack.class)) {
				StyleSheetStack[] annotationsByType = c.getClass().getAnnotationsByType(StyleSheetStack.class);
				for (StyleSheetStack js : annotationsByType) {
					processAnnotation(js);
				}
			}
		}

	}

	private void processAnnotation(StyleSheetStack js) {

		String directory = js.directory();
		int position = js.position();
		if (directory == null) {
			directory = "";
		}
		if (!directory.endsWith(PATH_SEPARATOR)) {
			directory = directory + PATH_SEPARATOR;
		}
		String[] value = js.value();
		if (value != null) {
			for (String jsPath : value) {
				add(directory + jsPath, position);
				logger.trace("Add css {}", jsPath);
			}
		}
	}

	@Override
	public String renderResponse() {
		StringBuilder sb = new StringBuilder();
		for (Link l : links) {
			sb.append(link().withType("text/css").withHref(l.link).withRel("stylesheet").toString()).append("\n");
		}
		return sb.toString();
	}

}
