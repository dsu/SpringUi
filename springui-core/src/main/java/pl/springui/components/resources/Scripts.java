package pl.springui.components.resources;

import static j2html.TagCreator.script;

import java.util.Collection;
import java.util.Date;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponentI;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
public class Scripts extends HtmlImports {

  private static long refreshingParameter = new Date().getTime();

  @Autowired
  public Scripts(UiCtx ctx) {
    super(ctx);

  }

  @Override
  public void applyRequest() {

    Collection<UiComponentI> allComponents = getCtx().getAllComponents();
    logger.trace("applyRequest of Scripts for {} components", allComponents.size());
    for (UiComponentI c : allComponents) {
      Class<?> targetClass = AopUtils.getTargetClass(c);
      logger.debug("Process JS files annotations in {}", targetClass);

      boolean hasJsStacks = targetClass.isAnnotationPresent(JavaScriptStacks.class);
      if (hasJsStacks) {
        JavaScriptStacks[] annotationsArrayByType = targetClass
            .getAnnotationsByType(JavaScriptStacks.class);
        for (JavaScriptStacks jsArray : annotationsArrayByType) {
          for (JavaScriptStack js : jsArray.value()) {
            processAnnotation(js);
          }
        }
      }
      boolean hasJs = targetClass.isAnnotationPresent(JavaScriptStack.class);
      if (hasJs) {
        JavaScriptStack[] annotationsByType = targetClass
            .getAnnotationsByType(JavaScriptStack.class);
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

        logger.debug("Found JS file : {}", jsPath);
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
