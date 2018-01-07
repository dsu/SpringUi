package pl.springui.template.engine;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slieb.formatter.HtmlExceptionFormatOptions;
import org.slieb.formatter.HtmlExceptionFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.utils.ViewModelTrace;
import pl.springui.components.utils.ViewModelTraces;

/**
 * 
 * Jezeli ladowanie z servletContext to pliki musza byc w src/main/webapp/WEB-INF
 *
 * @author dsu
 *
 */
@Component
@Lazy
@Primary
@ConfigurationProperties("springui")
public class Thymeleaf implements MapTemplateEngine {

  // FIXME - configurable
  protected static long CACHE_MS = 1000 * 60 * 15;
  public static final String CHARACTER_ENCODING = "UTF-8";

  // classpath:/templates/thymeleaf/ ?
  public static final String TEMPLATE_FOLDER = "templates/thymeleaf/";
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected boolean productionMode = false;
  protected TemplateEngine templateEngine;

  @Autowired
  private ViewModelTraces traces;

  public ViewModelTraces getTraces() {
    return traces;
  }

  @PostConstruct
  public void init() {
    // ServletContextTemplateResolver templateResolver = new
    // ServletContextTemplateResolver(ctx);

    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    templateResolver.setTemplateMode("HTML");
    templateResolver.setCharacterEncoding(CHARACTER_ENCODING);

    if (productionMode) {
      logger.debug("Enabling thymeleaf cache");
      templateResolver.setCacheable(true);
      templateResolver.setCacheTTLMs(CACHE_MS);
    } else {
      logger.debug("Disabling thymeleaf cache");
      templateResolver.setCacheable(false);
    }

    templateEngine = new TemplateEngine();
    // templateEngine.addDialect(new LayoutDialect());
    // templateEngine.addDialect(new HelloDialect());
    templateEngine.setTemplateResolver(templateResolver);

  }

  public boolean isProductionMode() {
    return productionMode;
  }

  @Override
  public void procesTemplate(Map<String, Object> model, String templatepath, Writer writer) {

    logger.debug("Processing ... " + templatepath);
    model.put("generatedTs", new Date().toString());
    Path path = Paths.get(TEMPLATE_FOLDER, templatepath);
    long startTime = System.currentTimeMillis();
    if (logger.isTraceEnabled()) {
      for (Entry<String, Object> e : model.entrySet()) {
        logger.trace("model variable {} = {}", e.getKey(), e.getValue());
      }
    }

    Context ctx = new Context(Locale.getDefault(), model);
    long executionTs = 0;
    Exception thymeleafException = null;
    try {
      templateEngine.process(path.toString(), ctx, writer);
      executionTs = System.currentTimeMillis() - startTime;
      logger.trace("thymeleaf ts: {}", executionTs);
    } catch (Exception e) {
      logger.warn("Thymelaf processing exception", e);
      thymeleafException = e;
    }

    traceModel(model, executionTs, thymeleafException);

    if (thymeleafException != null) {
      if (!productionMode) {
        HtmlExceptionFormatOptions options = new HtmlExceptionFormatOptions();
        options.setPrintDetails(true);
        String html = new HtmlExceptionFormatter().toString(thymeleafException);
        try {
          writer.write(html);
        } catch (IOException ex) {
          logger.warn("Error displaying an error message form Thymeleaf ...", ex);
        }
      } else {
        throw new UserVisibleError("Page cannot be rendered. Report the error please.");
      }

    }
  }

  public void setProductionMode(boolean productionMode) {
    this.productionMode = productionMode;
  }

  public void setTraces(ViewModelTraces traces) {
    this.traces = traces;
  }

  protected void traceModel(Map<String, Object> model, long executionTs, Exception exception) {

    if (traces != null && traces.isEnabled()) {
      ViewModelTrace trace = new ViewModelTrace(getClass());
      trace.setMapSouroce(model);
      trace.setExecutionMs(executionTs);
      trace.setProcessingException(exception);
      logger.debug("new trace : {}", trace);
      traces.addTrace(trace);
    }
  }

}