package pl.springui.components.resources;

import static j2html.TagCreator.link;

import java.util.Collection;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.UiComponentI;
import pl.springui.http.UiCtx;

@Component
@Scope("prototype")
public class Styles extends HtmlImports {

  public Styles(UiCtx ctx) {
    super(ctx);
  }

  @Override
  public void applyRequest() {

    Collection<UiComponentI> allComponents = getCtx().getAllComponents();
    for (UiComponentI c : allComponents) {
      Class<?> targetClass = AopUtils.getTargetClass(c);
      logger.debug("Process css files annotations in {}", targetClass);
      processStack(targetClass);
      processSingleStack(targetClass);
    }

  }

  protected void processAnnotation(StyleSheetStack css) {

    String directory = css.directory();
    int position = css.position();
    if (directory == null) {
      directory = "";
    }
    if (!directory.endsWith(PATH_SEPARATOR)) {
      directory = directory + PATH_SEPARATOR;
    }
    String[] value = css.value();
    if (value != null) {
      for (String cssPath : value) {
        add(directory + cssPath, position);
        logger.trace("Add css {}", cssPath);
      }
    }
  }

  protected void processSingleStack(Class<?> targetClass) {
    if (targetClass.isAnnotationPresent(StyleSheetStack.class)) {
      StyleSheetStack[] annotationsByType = targetClass.getAnnotationsByType(StyleSheetStack.class);
      for (StyleSheetStack js : annotationsByType) {
        processAnnotation(js);
      }
    }
  }

  protected void processStack(Class targetClass) {

    if (targetClass.isAnnotationPresent(StyleSheetStacks.class)) {
      logger.debug("Has @StyleSheetStack");
      StyleSheetStacks[] annotationsArrayByType = (StyleSheetStacks[]) targetClass
          .getAnnotationsByType(StyleSheetStacks.class);
      for (StyleSheetStacks jsArray : annotationsArrayByType) {
        for (StyleSheetStack js : jsArray.value()) {
          processAnnotation(js);
        }
      }
    }
  }

  @Override
  public String renderResponse() {
    StringBuilder sb = new StringBuilder();
    for (Link l : links) {
      sb.append(link().withType("text/css").withHref(l.link).withRel("stylesheet").toString())
          .append("\n");
    }
    return sb.toString();
  }

}
