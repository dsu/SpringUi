package pl.springui.template.engine;

import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 * Converts an object to a XML with JAXB and processes it in a XSL. You can use
 * JAXB annotations to control how an object will be transformed.
 * 
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class XslObjectEngine extends AbstractXslEngine implements ObjectTemplateEngine {

	protected static Document jaxbObjectToXML(Object model) throws ParserConfigurationException, JAXBException {

		JAXBContext context = JAXBContext.newInstance();
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().newDocument();
		m.marshal(model, doc);
		return doc;

	}

	public void procesTemplate(Object model, String templatepath, Writer writer) {

		Document xmlModel;
		try {
			xmlModel = jaxbObjectToXML(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(xmlToString(xmlModel));
		DOMSource xmlSouroce = new DOMSource(xmlModel);
		transformXml(templatepath, xmlSouroce, writer);

	}

}
