package pl.springui.template.engine;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Process a template with a map as a data source.
 * 
 * @author dsu
 *
 */
public interface MapTemplateEngine {

	void procesTemplate(Map<String, Object> model, String templatePath, Writer writer);

	default String procesTemplateAsString(Map<String, Object> model, String templatePath) {
		StringWriter writer = new StringWriter();
		procesTemplate(model, templatePath, writer);
		return writer.toString();
	}

}