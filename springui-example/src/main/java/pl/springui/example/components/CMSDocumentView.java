package pl.springui.example.components;

import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pl.springui.components.ExampleComponent;
import pl.springui.components.UiComponent;
import pl.springui.components.cache.ConfiguredCacheNames;
import pl.springui.components.exceptions.NotFoundException;
import pl.springui.components.exceptions.UiException;
import pl.springui.example.model.CmsContext;
import pl.springui.example.model.CmsDocument;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.XslFlatMapEngine;
import pl.springui.template.engine.XslXmlEngine;
import pl.springui.utils.Profiler;

@Component
@Scope("prototype")
@ExampleComponent
public class CMSDocumentView extends UiComponent {

	private XslFlatMapEngine mapEngine;
	private XslXmlEngine xmlEngine;
	private CmsContext cmsContext;

	@Autowired
	public CMSDocumentView(UiCtx ctx, XslFlatMapEngine engine, XslXmlEngine xmlEngine, CmsContext cmsContext) {
		super(ctx);
		this.mapEngine = engine;
		this.xmlEngine = xmlEngine;
		this.cmsContext = cmsContext;
	}

	public String getAlias() {
		return cmsContext.getAlias();
	}

	/**
	 * Caching when there is no request parameters - static pageCount onlny
	 */
	@Cacheable(cacheNames = ConfiguredCacheNames.CMS, keyGenerator = "cmsAliasKey")
	@Profiler
	@Override
	public String renderResponse() {
		logger.debug("CMS Component for {} executing ...", cmsContext);

		if (cmsContext.getDocument() == null) {
			throw new NotFoundException("There is not document in the CMS context");
		}

		if (cmsContext.getDocument().isCategory()) {
			// FIXME - to powinno byc w metodzie process!
			List<CmsDocument> docs = cmsContext.getSubdocuments();
			Document doc = serializeCategory(cmsContext.getDocument(), docs);
			return xmlEngine.procesXmlTemplateAsString(doc, "components/cms/category.xsl");
		} else {
			return mapEngine.procesTemplateAsString(cmsContext.getDocument().asMap(), "components/cms/document.xsl");
		}

	}

	protected Document serializeCategory(CmsDocument mainDocument, List<CmsDocument> docs) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			doc = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException xmlE) {
			throw new UiException(xmlE);
		}

		Element rootElement = doc.createElement("category");

		for (Entry<String, Object> e : mainDocument.asMap().entrySet()) {
			Element xmlE = doc.createElement(StringEscapeUtils.escapeXml(e.getKey()));
			xmlE.setTextContent((String) e.getValue());
			rootElement.appendChild(xmlE);
		}

		doc.appendChild(rootElement);

		Element sub = doc.createElement("subdocuments");
		rootElement.appendChild(sub);

		for (CmsDocument cmsDoc : docs) {
			Element xmlDoc = doc.createElement("document");
			for (Entry<String, Object> e : cmsDoc.asMap().entrySet()) {
				Element xmlE = doc.createElement(StringEscapeUtils.escapeXml(e.getKey()));
				xmlE.setTextContent((String) e.getValue());
				xmlDoc.appendChild(xmlE);
			}
			sub.appendChild(xmlDoc);
		}
		return doc;
	}

}
