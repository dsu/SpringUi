package pl.springui.template.engine;

import java.io.Writer;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import pl.springui.template.engine.xsl.AbstractXslEngine;
import pl.springui.template.engine.xsl.CahedTransformerFactory;

/**
 * Converts a map to a XML and processes it in a XSL. Uses default JAXB
 * serialization.
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class XslMapEngine extends AbstractXslEngine implements MapTemplateEngine {

	@XmlRootElement(name = "root")
	@XmlAccessorType(XmlAccessType.FIELD)
	protected static class XmlModelWrapper {

		Map<String, Object> model;

		public XmlModelWrapper() {
		}

		public XmlModelWrapper(Map<String, Object> model) {
			super();
			this.model = model;
		}

		public Map<String, Object> getModel() {
			return model;
		}

		public void setModel(Map<String, Object> model) {
			this.model = model;
		}

	}

	protected static Document mapToXML(Map<String, Object> model) throws ParserConfigurationException, JAXBException {

		JAXBContext context = JAXBContext.newInstance(XmlModelWrapper.class, java.util.ArrayList.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE); // To
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().newDocument();
		m.marshal(new XmlModelWrapper(model), doc);
		return doc;

	}

	@Autowired
	public XslMapEngine(CahedTransformerFactory factory) {
		super(factory);
	}

	@Override
	public void procesTemplate(Map<String, Object> model, String templatepath, Writer writer) {

		Document xmlModel;
		try {
			xmlModel = mapToXML(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.debug(xmlToString(xmlModel));
		DOMSource xmlSouroce = new DOMSource(xmlModel);

		transformXml(templatepath, xmlSouroce, writer);

	}

}
