package pl.springui.components.resources;

import static j2html.TagCreator.script;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
public class Scripts extends HtmlImports {

	@Autowired
	public Scripts(UiCtx ctx) {
		super(ctx);

	}

	private static long refreshingParameter = new Date().getTime();

	@Override
	public void applyRequest() {

		Collection<UiComponent> allComponents = getCtx().getAllComponents();
		logger.trace("applyRequest of Scripts for {} components", allComponents.size());
		for (UiComponent c : allComponents) {

			boolean hasJsStacks = c.getClass().isAnnotationPresent(JavaScriptStacks.class);
			if (hasJsStacks) {
				JavaScriptStacks[] annotationsArrayByType = c.getClass().getAnnotationsByType(JavaScriptStacks.class);
				for (JavaScriptStacks jsArray : annotationsArrayByType) {
					for (JavaScriptStack js : jsArray.value()) {
						processAnnotation(js);
					}
				}
			}
			boolean hasJs = c.getClass().isAnnotationPresent(JavaScriptStack.class);
			logger.trace("Has js: {}, {}", hasJs, c);
			if (hasJs) {
				JavaScriptStack[] annotationsByType = c.getClass().getAnnotationsByType(JavaScriptStack.class);
				for (JavaScriptStack js : annotationsByType) {
					processAnnotation(js);
				}
			}
		}

	}

	private void processAnnotation(JavaScriptStack js) {
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
				if (jsPath.contains("?")) {
					jsPath = jsPath + "&" + refreshingParameter;
				} else {
					jsPath = jsPath + "?" + refreshingParameter;
				}
				add(directory + jsPath, position);
			}
		}
	}

	@Override
	public String renderResponse() {
		StringBuilder sb = new StringBuilder();
		for (Link l : links) {
			sb.append(script().withType("text/javascript").withSrc(l.link).toString()).append("\n");
		}
		return sb.toString();
	}

}
