package pl.springui.template.engine;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * http://www.vogella.com/tutorials/FreeMarker/article.html
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class Freemarker implements MapTemplateEngine {

  private static final String TEMPLATE_PATH = "templates/ftl";
  private Configuration cfg;
  private boolean isDevmode = true;

  @PostConstruct
  public void init() {

    // 1. Configure FreeMarker
    //
    // You should do this ONLY ONCE, when your application starts,
    // then reuse the same Configuration object elsewhere.

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);

    // Where do we load the templates from:
    // cfg.setServletContextForTemplateLoading(ctx, TEMPLATE_PATH);
    cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), TEMPLATE_PATH);

    cfg.setDefaultEncoding("UTF-8");
    cfg.setLocale(Locale.getDefault());
    // FIXME - dont show error on production
    if (isDevmode) {
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    } else {
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    this.cfg = cfg;

  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.template.engine.MapTemplateEngine#procesTemplate(java.util. Map,
   * java.lang.String, java.io.StringWriter)
   */
  @Override
  public void procesTemplate(Map<String, Object> model, String templatePath, Writer writer) {

    // 2.2. Get the template
    Template template;
    try {
      template = cfg.getTemplate(templatePath);

      // 2.3. Generate the output

      if (isDevmode) {
        Writer consoleWriter = new OutputStreamWriter(System.out);
        template.process(model, consoleWriter);
      }
      template.process(model, writer);

    } catch (Exception e1) {

      e1.printStackTrace();
    }

  }

}
