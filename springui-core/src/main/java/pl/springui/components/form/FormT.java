package pl.springui.components.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.springui.components.HTMLRenderer;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * A <form> build by Thymeleaf
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class FormT extends UiComponent {

  protected MapTemplateEngine engine;
  protected List<UiComponent> fields = new ArrayList<UiComponent>();

  @Autowired
  public FormT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
    super(ctx);
    this.engine = engine;
  }

  public void add(UiComponent field) {
    addChild(field);
    fields.add(field);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FormT other = (FormT) obj;
    if (fields == null) {
      if (other.fields != null) {
        return false;
      }
    } else if (!fields.equals(other.fields)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
    return result;
  }

  @Profiler
  @Override
  public String renderResponse() {
    if (!isVisible()) {
      return renderPlaceHolder();
    }
    // render fields separately
    ArrayList<String> renderedFields = new ArrayList<String>();
    for (HTMLRenderer c : fields) {
      renderedFields.add(c.renderResponse());
    }

    putToViewModel("fields", renderedFields);
    putStringToViewModel("viewguid", ctx.getViewGuid());

    return engine.procesTemplateAsString(viewModel, "components/form.xhtml");
  }

}
