package pl.springui.template.engine;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Uses fields from a Java object as a data source
 * 
 * @author dsu
 *
 */
public interface ObjectTemplateEngine {

	void procesTemplate(Object model, String templatepath, Writer writer);

	default String procesTemplateAsString(Object model, String templatePath) {
		StringWriter writer = new StringWriter();
		procesTemplate(model, templatePath, writer);
		return writer.toString();
	}
}
