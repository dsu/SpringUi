package pl.springui.components.utils;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.template.engine.MapTemplateEngine;

/**
 * Generates HTML that causes a redirect
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class RedirectPage {

  protected MapTemplateEngine engine;

  @Autowired
  public RedirectPage(@Qualifier("thymeleaf") MapTemplateEngine engine) {
    this.engine = engine;
  }

  public String renderResponse(String uri) {
    HashMap<String, Object> viewModel = new HashMap<>();
    viewModel.put("uri", uri);
    return engine.procesTemplateAsString(viewModel, "/components/redirect.xhtml");
  }

}
