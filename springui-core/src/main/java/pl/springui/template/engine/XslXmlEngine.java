package pl.springui.template.engine;

import java.io.Writer;
import java.util.Date;

import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import pl.springui.template.engine.xsl.AbstractXslEngine;
import pl.springui.template.engine.xsl.CahedTransformerFactory;

/**
 * Standard XSL/XML processing
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class XslXmlEngine extends AbstractXslEngine implements XmlTemplateEngine {

	@Autowired
	public XslXmlEngine(CahedTransformerFactory factory) {
		super(factory);
	}

	@Override
	public void procesXmlTemplate(Document document, String templatePath, Writer writer) {
		document.getDocumentElement().setAttribute("created-ts", new Date().toString());
		logger.debug(xmlToString(document));
		DOMSource xmlSouroce = new DOMSource(document);
		transformXml(templatePath, xmlSouroce, writer);

	}

}
