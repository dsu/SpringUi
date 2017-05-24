package pl.springui.template.engine;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import pl.springui.components.exceptions.ComponentRenderingException;

public class AbstractXslEngine {

	protected static final String TEMPLATE_FOLDER = "templates/xsl/";
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

	protected TransformerFactory factory;

	public AbstractXslEngine() {
		super();
	}

	@PostConstruct
	public void init() {
		factory = TransformerFactory.newInstance();
	}

	protected void transformXml(String templatepath, DOMSource xmlSouroce, Writer writer) {
		templatepath = TEMPLATE_FOLDER + File.separator + templatepath;

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(templatepath).getFile());
		Source xslt = new StreamSource(file);

		System.out.println("template file : " + file.getName() + ", " + file.exists());

		try {
			Transformer transformer = factory.newTransformer(xslt);
			transformer.transform(xmlSouroce, new StreamResult(writer));
		} catch (TransformerException e) {
			throw new ComponentRenderingException(e);
		}
	}

}