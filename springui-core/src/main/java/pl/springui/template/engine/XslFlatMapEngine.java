package pl.springui.template.engine;

import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pl.springui.template.engine.xsl.AbstractXslEngine;
import pl.springui.template.engine.xsl.CahedTransformerFactory;

/**
 * Converts a map to a XML and processes it in a XSL. It is for a very basic
 * usage, for a flat data structures.
 * 
 * @author dsu
 *
 */
@Component
@Lazy
public class XslFlatMapEngine extends AbstractXslEngine implements MapTemplateEngine {

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

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().newDocument();

		Element rootElement = doc.createElement("doc");
		doc.appendChild(rootElement);

		for (Entry<String, Object> e : model.entrySet()) {
			Element xmlE = doc.createElement(StringEscapeUtils.escapeXml(e.getKey()));
			xmlE.setTextContent((String) e.getValue());
			rootElement.appendChild(xmlE);
		}
		return doc;
	}

	@Autowired
	public XslFlatMapEngine(CahedTransformerFactory factory) {
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
		xmlModel.getDocumentElement().setAttribute("created-ts", new Date().toString());
		logger.debug(xmlToString(xmlModel));
		DOMSource xmlSouroce = new DOMSource(xmlModel);

		transformXml(templatepath, xmlSouroce, writer);

	}

}
