package pl.springui.template.engine;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.w3c.dom.Document;

public interface XmlTemplateEngine {

	void procesXmlTemplate(Document document, String templatePath, Writer writer);

	default String procesXmlTemplateAsString(Document document, String templatePath) {
		StringWriter writer = new StringWriter();
		procesXmlTemplate(document, templatePath, writer);
		return writer.toString();
	}

}