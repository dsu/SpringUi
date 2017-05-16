package pl.springui.template.engine;

import java.io.Writer;

import javax.xml.transform.dom.DOMSource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 * Standard XSL/XML processing
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class XslXmlEngine extends AbstractXslEngine implements XmlTemplateEngine {

	@Override
	public void procesXmlTemplate(Document document, String templatePath, Writer writer) {
		System.out.println(xmlToString(document));
		DOMSource xmlSouroce = new DOMSource(document);
		transformXml(templatePath, xmlSouroce, writer);

	}

}
