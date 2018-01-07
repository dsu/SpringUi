package pl.springui.template.engine.xsl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CahedTransformerFactory {

	protected class XsltURIResolver implements URIResolver {

		protected String getTempletsBasePath() {
			return TEMPLATE_FOLDER + File.separator;
		}

		@Override
		public Source resolve(String href, String base) throws TransformerException {
			try {

				boolean noBaseProvided = false;
				href = href.trim();
				if (base == null || base.length() == 0) {
					base = getTempletsBasePath();
					noBaseProvided = true;
				}

				Path path = Paths.get(href);
				Path effectivePath = path;

				if (path.isAbsolute()) {
					Path basePath = Paths.get(getTempletsBasePath());
					effectivePath = Paths.get(basePath.toString(), path.toString()); // relativize
				} else {

					if (!noBaseProvided) {
						// delegate paths relative to a other file to a default
						// resolving mechanism
						return null;

					}
					// resolve based on a provided diractory
					Path basePath = Paths.get(base);
					effectivePath = basePath.resolve(path);

				}
				String resolved = effectivePath.normalize().toString();

				logger.trace("href: {} , resolved {} , base: {}", href, resolved, base);
				ClassLoader classLoader = getClass().getClassLoader();
				File file = new File(classLoader.getResource(resolved).getFile());
				return new StreamSource(file);
			} catch (Exception ex) {
				logger.error("XML/XSL uri: " + href + " resolve error :", ex);
				return null;
			}
		}
	}

	protected static final String TEMPLATE_FOLDER = "templates/xsl/";

	protected TransformerFactory factory;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Cacheable(cacheNames = "transformer")
	public Transformer getTransformer(String templatepath)
			throws TransformerConfigurationException, TransformerException {
		long ts = System.currentTimeMillis();
		Transformer transformer = factory.newTransformer(factory.getURIResolver().resolve(templatepath, null));
		logger.debug("Get transformer for {} in {} ms", templatepath, (System.currentTimeMillis() - ts));
		return transformer;
	}

	@PostConstruct
	public void init() {
		XsltURIResolver resolver = new XsltURIResolver();
		factory = TransformerFactory.newInstance();
		factory.setURIResolver(resolver);

	}

}
