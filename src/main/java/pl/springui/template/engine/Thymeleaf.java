package pl.springui.template.engine;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slieb.formatter.HtmlExceptionFormatOptions;
import org.slieb.formatter.HtmlExceptionFormatter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.tree.TreeContainer;

/**
 * 
 * Jezeli ladowanie z servletContext to pliki musza byc w
 * src/main/webapp/WEB-INF
 *
 * @author dsu
 *
 */
@Component
@Lazy
@ConfigurationProperties("springui")
public class Thymeleaf implements MapTemplateEngine {

	public static final String CHARACTER_ENCODING = "UTF-8";
	public static final String TEMPLATE_FOLDER = "templates/thymeleaf/";

	private static long CACHE_MS = 1000 * 5;

	private boolean productionMode = false;
	private static boolean initialized;
	private TemplateEngine templateEngine;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	public void init() {
		// ServletContextTemplateResolver templateResolver = new
		// ServletContextTemplateResolver(ctx);

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		/*
		 * XHTML Valid XHTML HTML5 Legacy HTML5
		 */
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding(CHARACTER_ENCODING);
		templateResolver.setCacheable(true);
		templateResolver.setCacheTTLMs(CACHE_MS);

		templateEngine = new TemplateEngine();
		// templateEngine.addDialect(new LayoutDialect());
		// templateEngine.addDialect(new HelloDialect());
		templateEngine.setTemplateResolver(templateResolver);

		initialized = templateEngine.isInitialized();
		logger.debug("thymeleaf initialized: {} ", initialized);
	}

	@Override
	public void procesTemplate(Map<String, Object> model, String templatepath, Writer writer) {

		try {
			model.put("generatedTs", new Date().toString());
			templatepath = TEMPLATE_FOLDER + File.separator + templatepath;
			long startTime = System.currentTimeMillis();

			if (logger.isTraceEnabled()) {
				for (Entry<String, Object> e : model.entrySet()) {
					logger.trace("model variable {} = {}", e.getKey(), e.getValue());
				}
			}

			Context ctx = new Context(Locale.getDefault(), model);
			templateEngine.process(templatepath, ctx, writer);
			long estimatedTime = System.currentTimeMillis() - startTime;
			logger.trace("thymeleaf ts: {}", estimatedTime);
		} catch (Exception e) {
			// Fixme not in production
			logger.warn("Thymelaf error", e);
			if (!productionMode) {
				HtmlExceptionFormatOptions options = new HtmlExceptionFormatOptions();
				options.setPrintDetails(true);
				String html = new HtmlExceptionFormatter().toString(e);
				try {
					writer.write(html);
				} catch (IOException ex) {
					logger.warn("Error displaying an error message form Thymeleaf ...", ex);
				}
			} else {
				throw new UserVisibleError("Page cannot be displayed. Report the error please.");
			}

		}

	}

}