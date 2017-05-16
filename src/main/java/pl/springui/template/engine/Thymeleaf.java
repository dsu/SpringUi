package pl.springui.template.engine;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slieb.formatter.HtmlExceptionFormatOptions;
import org.slieb.formatter.HtmlExceptionFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

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
public class Thymeleaf implements MapTemplateEngine {

	public static final String CHARACTER_ENCODING = "UTF-8";
	public static final String TEMPLATE_FOLDER = "templates/thymeleaf/";

	private TemplateEngine templateEngine;

	private static long CACHE_MS = 1000 * 5;
	private static boolean initialized;
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

	public void procesTemplate(Map<String, Object> model, String templatepath, Writer writer) {

		try {
			templatepath = TEMPLATE_FOLDER + File.separator + templatepath;
			long startTime = System.currentTimeMillis();
			Context ctx = new Context(Locale.getDefault(), model);
			templateEngine.process(templatepath, ctx, writer);
			long estimatedTime = System.currentTimeMillis() - startTime;

		} catch (Exception e) {

			// Fixme not in production
			HtmlExceptionFormatOptions options = new HtmlExceptionFormatOptions();
			options.setPrintDetails(true);
			String html = new HtmlExceptionFormatter().toString(e);
			try {
				writer.write(html);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

}