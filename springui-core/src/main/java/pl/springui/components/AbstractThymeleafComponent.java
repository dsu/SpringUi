package pl.springui.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import pl.springui.components.exceptions.UiException;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

/**
 * Displays a template without any extra logic
 * 
 * @author dsu
 *
 */
public abstract class AbstractThymeleafComponent extends UiComponent {

  protected MapTemplateEngine engine;
  protected String templatePath;

  @Autowired
  public AbstractThymeleafComponent(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
    super(ctx);
    this.engine = engine;
  }

  public MapTemplateEngine getEngine() {
    return engine;
  }

  public String getTemplatePath() {
    return templatePath;
  }

  @Override
  public String renderResponse() {
    if (templatePath == null) {
      throw new UiException("Template path is null");
    }

    return engine.procesTemplateAsString(viewModel, templatePath);
  }

  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }

}
