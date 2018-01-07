package pl.springui.template.engine.xsl;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import pl.springui.components.exceptions.ComponentRenderingException;
import pl.springui.components.utils.ViewModelTrace;
import pl.springui.components.utils.ViewModelTraces;

public class AbstractXslEngine {

	protected static String nodeToString(Node node)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();

		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(new DOMSource(node), new StreamResult(buffer));
		String str = buffer.toString();
		return str;
	}
	protected static String xmlToString(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

	protected CahedTransformerFactory factory;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ViewModelTraces traces;

	@Autowired
	public AbstractXslEngine(CahedTransformerFactory factory) {
		this.factory = factory;
	}

	private void traceModel(DOMSource xmlSouroce, long executionTs) {
		if (traces != null && traces.isEnabled()) {
			try {
				Node node = xmlSouroce.getNode();

				ViewModelTrace trace = new ViewModelTrace(getClass());
				trace.setExecutionMs(executionTs);
				trace.setXmlString(nodeToString(node));
				traces.addTrace(trace);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	protected void transformXml(String templatepath, DOMSource xmlSouroce, Writer writer) {
		long startTime = System.currentTimeMillis();

		try {
			Transformer transformer = factory.getTransformer(templatepath);
			transformer.transform(xmlSouroce, new StreamResult(writer));
			long executionTs = System.currentTimeMillis() - startTime;
			traceModel(xmlSouroce, executionTs);
			logger.debug("xsl/xml tt {} ms", executionTs);

		} catch (TransformerException e) {
			throw new ComponentRenderingException(e);
		}

	}

}